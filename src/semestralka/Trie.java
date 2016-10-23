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
    private Node root = new Node("",null);
    private ReentrantLock lock = new ReentrantLock();
    private ObservableList<String> words = FXCollections.observableArrayList();

    ObservableList<String> getWords()
    {
        return words;
    }

    void insert(String key)
    {
        if (key.length() == 0)
            return;
        lock.lock();
        root.insert(key,null,"");
        lock.unlock();
    }

    void insert(String key, int value)
    {
        lock.lock();
        if (!words.contains(key))
            words.add(key);
        root.insert(key, value, "");
        lock.unlock();
    }

    List<Integer> find(String key)
    {
        lock.lock();
        List<Integer> list = root.find(key, "");
        lock.unlock();
        return list;
    }


    void clearValues()
    {
        lock.lock();
        root.clear();
        lock.unlock();
    }

    void print()
    {
        print(System.out);
    }

    void print(PrintStream ps)
    {
        lock.lock();
        int floor = 0;
        root.print(floor,ps);
        lock.unlock();
    }

    void load(File f)
    {
        lock.lock();
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
    private String prefix;
    private List<Node> childs = new ArrayList<>();
    private List<Integer> values;

    public Node(String prefix, Integer value)
    {
        this.prefix = prefix;
        if (prefix.length() == 0)
            return;
        values = new ArrayList<>();
        if (value != null)
            values.add(value);
    }

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

    boolean insert(String key, Integer value, String curr)
    {
        String n = curr + prefix;
        if (key.indexOf(n) == 0)
            return found(key,n,value,curr);
        else
            return notFound(key,n,value,curr);
    }

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

    private void split(String newName, String newPrefix)
    {
        Node node = new Node(newName, values, childs);
        prefix = newPrefix;
        values = null;
        childs.clear();
        childs.add(node);

    }

    private void addValue(Integer value)
    {
        if (values == null)
            values = new ArrayList<>();
        if (value != null && !values.contains(value))
            values.add(value);
    }

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

    void clear()
    {
        if (values != null)
            values.clear();
        for (Node node : childs)
            node.clear();
    }

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

    private List<String> getWorld(String curr)
    {
        List<String> list = new ArrayList<>();
        String word = curr + prefix;
        if (values != null)
            list.add(word);

        for (Node child : childs)
        {
            List<String> res = child.getWorld(word);
            if (res.size() != 0)
                list.addAll(res);
        }
        return list;
    }

    void load(String prefix, List<Integer> values, int i, int pIndex)
    {
        if (i >= pIndex)
        {
            childs.add(new Node(prefix, values, new ArrayList<>()));
            return;
        }
        childs.get(childs.size() - 1).load(prefix,values, i+1, pIndex);
    }

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
