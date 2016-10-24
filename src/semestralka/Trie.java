package semestralka;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Created by Martinek on 7.10.2016.
 *
 */
class Trie
{
    /**
     * Root node of trie
     */
    private Node root = new Node("",null);
    /**
     * Lock that allow async use of trie (for preventing freeze of gui)
     */
    private ReentrantLock lock = new ReentrantLock();
    /**
     * List of words contained in trie
     */
    private ObservableList<String> words = FXCollections.observableArrayList();

    /**
     * Getter for words
     * @return words contained in trie
     */
    ObservableList<String> getWords()
    {
        return words;
    }

    /**
     * Insert key without value for dictionary building
     * @param key Key word
     */
    void insert(String key)
    {
        if (key.length() == 0)
            return;
        lock.lock();
        if (!words.contains(key))
            words.add(key);
        root.insert(key,null,"");
        lock.unlock();
    }

    /**
     * Insert key with value
     * @param key Key word
     * @param value index of word
     */
    void insert(String key, int value)
    {
        lock.lock();
        if (!words.contains(key))
            words.add(key);
        root.insert(key, value, "");
        lock.unlock();
    }

    /**
     * Find and return indexes of key
     * @param key Key word
     * @return Returns null if key is not as word in dictionary, List[size==0] if key is in dictionary, List[size>0] if key is in text
     */
    List<Integer> find(String key)
    {
        lock.lock();
        List<Integer> list = root.find(key, "");
        lock.unlock();
        return list;
    }

    /**
     * Clear indexes of words in trie
     */
    void clearValues()
    {
        lock.lock();
        root.clear();
        lock.unlock();
    }

    /**
     * Calls print on standat output
     */
    void print()
    {
        print(System.out);
    }

    /**
     * Print trie on specified print stream
     * @param ps PrintStream
     */
    void print(PrintStream ps)
    {
        lock.lock();
        int floor = 0;
        root.print(floor,ps);
        lock.unlock();
    }

    /**
     * Load trie from dictionary file with specific syntax
     * @param f file to load from
     */
    void load(File f)
    {
        lock.lock();
        root = new Node("",null);
        words.clear();
        try (Stream<String> lines = Files.lines(Paths.get(f.getPath()), Charset.defaultCharset())) {
            lines.forEachOrdered(line ->
            {
                int vIndex = line.indexOf(':');
                int pIndex = line.indexOf('-');
                ArrayList<Integer> values = null;
                String prefix;
                if (vIndex == -1)
                    prefix = line.substring(pIndex+1);
                else
                {
                    prefix = line.substring(pIndex + 1, vIndex);
                    Scanner scanner = new Scanner(line.substring(vIndex+1));
                    values = new ArrayList<>();
                    while (scanner.hasNextInt())
                        values.add(scanner.nextInt());
                }

                if (values != null && !words.contains(prefix))
                    words.add(prefix);
                root.load(prefix,values, 1, pIndex);
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        lock.unlock();
    }

    /**
     * Create trie dictionary from text file
     * @param f file
     */
    void create(File f)
    {
        lock.lock();
        try (Stream<String> lines = Files.lines(Paths.get("big.txt"), Charset.defaultCharset())) {
            lines.forEachOrdered(line ->
            {
                String[] words = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
                for (String world : words)
                    insert(world);
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        lock.unlock();
    }

    /**
     * Validate if every child of root start with different character
     * @return true for valid trie false for invalid
     */
    public boolean validate()
    {
        lock.lock();
        boolean valid = root.validate();
        lock.unlock();
        return valid;
    }
}

class Node
{
    /**
     * Prefix of this node
     */
    private String prefix;
    /**
     * List of childs
     */
    private List<Node> childs = new ArrayList<>();
    /**
     * List of indexers, can be null
     */
    private List<Integer> values;

    /**
     * Standart constructor
     * @param prefix Prefix of this node
     * @param value can be null
     */
    public Node(String prefix, Integer value)
    {
        this.prefix = prefix;
        if (prefix.length() == 0)
            return;
        values = new ArrayList<>();
        if (value != null)
            values.add(value);
    }

    /**
     * Copy constructor that make node with new prefix and values and childs of old node
     * @param prefix Prefix of this node
     * @param values Values of node
     * @param childs Childs of node
     */
    public Node(String prefix, List<Integer> values, List<Node> childs)
    {
        this.prefix = prefix;
        if (values != null)
        {
            this.values = new ArrayList<>();
            for (int v : values)
                this.values.add(v);
        }
        this.childs = new ArrayList<>();
        for (Node c : childs)
            this.childs.add(c);
    }

    /**
     * Insert method that try to insert key to node
     * @param key Inserted key word
     * @param value index of key
     * @param curr Current string made from parents prefixes
     * @return true if inserted, false if not valid
     */
    boolean insert(String key, Integer value, String curr)
    {
        String n = curr + prefix;
        if (key.indexOf(n) == 0)
            return found(key,n,value,curr);
        else
            return notFound(key,n,value,curr);
    }

    /**
     * Mathod that will try split current node and insert to longes valid prefix
     * @param key Inserted key word
     * @param n Current value + prefix
     * @param value index of key
     * @param curr Current string made from parents prefixes
     * @return true if inserted, false if not valid
     */
    private boolean notFound(String key, String n, Integer value, String curr)
    {
        for (int i = prefix.length(); i > 0; i--)
        {
            String ne = curr+ prefix.substring(0,i);
            if (key.indexOf(ne) == 0)
            {
                split(n.substring(ne.length()),ne.substring(curr.length()));
                if (ne.length() == key.length())
                {
                    addValue(value);
                    return true;
                }
                Node newOne = new Node(key.substring(ne.length()), value);
                childs.add(newOne);
                return true;
            }
        }
        return false;
    }

    /**
     * Mathod that will try insert into current node, or split and insert if necessary
     * @param key Inserted key word
     * @param n Current value + prefix
     * @param value Index of key
     * @param curr Current string made from parents prefixes
     * @return true if inserted, false if not valid
     */
    private boolean found(String key, String n, Integer value, String curr)
    {
        if (key.length() == n.length())
        {
            if (value != null)
                addValue(value);
            return true;
        }
        else if (key.length() < n.length())
        {
            split(prefix,key.substring(curr.length()));
            addValue(value);
            return true;
        }

        for (Node child : childs)
            if (child.insert(key,value,n))
                return true;
        childs.add(new Node(key.substring(n.length()),value));
        return true;

    }

    /**
     * Split node
     * @param newName name of new node
     * @param newPrefix new name of current node
     */
    private void split(String newName, String newPrefix)
    {
        Node node = new Node(newName, values, childs);
        prefix = newPrefix;
        values = null;
        childs.clear();
        childs.add(node);

    }

    /**
     * Add value to current node
     * @param value index of key
     */
    private void addValue(Integer value)
    {
        if (values == null)
            values = new ArrayList<>();
        if (value != null && !values.contains(value))
            values.add(value);
    }

    /**
     * Tries to find key in this node and its childrens
     * @param key Key word we are looking for
     * @param curr Current string made from parent prefixes
     * @return List of indexes
     */
    List<Integer> find(String key, String curr)
    {
        String n = curr + prefix;
        if (key.indexOf(n) == 0)
        {
            if (key.length() == n.length())
                return values;

            for (Node child : childs)
            {
                List<Integer> list = child.find(key,n);
                if (list != null)
                    return list;
            }
        }
        return null;
    }

    /**
     * Clears indexes of node and childrens
     */
    void clear()
    {
        if (values != null)
            values.clear();
        for (Node node : childs)
            node.clear();
    }

    /**
     * Prints current node and childrens into PrintStream
     * @param floor Current recursion level for formating
     * @param ps PrintStream
     */
    void print(int floor, PrintStream ps)
    {
        for (int i=0;i<floor;i++)
            ps.print("|");

        if (floor > 0)
        {
            ps.print("-" + prefix);
            if (values != null )
            {
                ps.print(":");
                for (int i : values)
                    ps.print(i + " ");
            }
            ps.println();
        }
        for (Node child: childs)
            child.print(floor+1, ps);
    }

    /**
     * Load prefix into trie
     * @param prefix prefix of node
     * @param values indexes of key word
     * @param i Level of recursion
     * @param pIndex Index we are looking for
     */
    void load(String prefix, List<Integer> values, int i, int pIndex)
    {
        if (i >= pIndex)
        {
            childs.add(new Node(prefix, values, new ArrayList<>()));
            return;
        }
        childs.get(childs.size() - 1).load(prefix,values, i+1, pIndex);
    }

    /**
     * Check if childs are valid
     * @return true if valid else false
     */
    boolean validate()
    {
        List<Character> chars = new ArrayList<>();
        for (Node child: childs)
        {
            if (!child.validate()|| chars.contains(child.prefix.charAt(0)))
                return false;
            chars.add(child.prefix.charAt(0));
        }
        return true;
    }
}
