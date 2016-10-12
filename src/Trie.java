import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by Stepan on 7.10.2016.
 */
public class Trie
{
    boolean showStats;
    Node root = new Node("",null);
    long startTime;

    public Trie(boolean showStats)
    {
        this.showStats = showStats;
    }

    void insert(String key)
    {
        if (key.length() == 0)
            return;
        root.insert(key,null,"");

    }

    void insert(String key, int value)
    {
        root.insert(key, value, "");
    }

    ArrayList<Integer> find(String key)
    {
        startTime = System.nanoTime();
        ArrayList<Integer> list = root.find(key, "");
        if (showStats)
            System.out.println("Find exec time: "+TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
        return list;
    }
    ArrayList<String> getWorlds()
    {
        startTime = System.nanoTime();
        ArrayList<String> list = root.getWorld("");
        if (showStats)
            System.out.println("Get exec time: "+ TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
        return list;
    }

    int getMaxDepth()
    {
        return root.getMaxDepth(0);
    }

    void clearValues()
    {
        root.clear();
    }

    void print()
    {
        print(System.out);
    }

    void print(PrintStream ps)
    {
        int floor = 0;
        root.print(floor,ps);
    }

    public void load(String s)
    {
        int floor = 0;
        try (Stream<String> lines = Files.lines(Paths.get(s), Charset.defaultCharset())) {
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

                root.load(prefix,values, 1, pIndex);
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean validate()
    {
        return root.validate();
    }
}

class Node
{
    String prefix;
    ArrayList<Node> childs = new ArrayList<>();
    ArrayList<Integer> values;

    public Node(String prefix, Integer value)
    {
        this.prefix = prefix;
        if (prefix.length() == 0)
            return;
        values = new ArrayList<>();
        if (value != null)
            values.add(value);
    }

    public Node(String prefix, ArrayList<Integer> values, ArrayList<Node> childs)
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

    public boolean insert(String key, Integer value, String curr)
    {
        String n = curr + prefix;
        if (key.indexOf(n) == 0)
        {
            if (key.length() == n.length())
            {
                if (value != null)
                    addValue(value);
                return true;
            }
            else if (key.length() < n.length())
            {
                Node node = new Node(prefix, values, childs);
                prefix = key.substring(curr.length());
                values.clear();
                addValue(value);
                childs.clear();
                childs.add(node);
                return true;
            }

            for (Node child : childs)
                if (child.insert(key,value,n))
                    return true;
            childs.add(new Node(key.substring(n.length()),value));
            return true;
        }

        for (int i = prefix.length(); i > 0; i--)
        {
            String ne = curr+ prefix.substring(0,i);
            if (key.indexOf(ne) == 0)
            {
                Node node = new Node(n.substring(ne.length()), values, childs);
                prefix = ne.substring(curr.length());
                values = null;
                childs.clear();
                childs.add(node);
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

    private void addValue(Integer value)
    {
        if (values == null)
            values = new ArrayList<>();
        if (value != null)
            values.add(value);
    }

    ArrayList<Integer> find(String key, String curr)
    {
        String n = curr + prefix;
        if (key.indexOf(n) == 0)
        {
            if (key.length() == n.length())
                return values;

            for (Node child : childs)
            {
                ArrayList<Integer> list = child.find(key,n);
                if (list != null)
                    return list;
            }
        }
        return null;
    }

    public void clear()
    {
        if (values != null)
            values.clear();
        for (Node node : childs)
            node.clear();
    }

    public void print(int floor, PrintStream ps)
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

    ArrayList<String> getWorld(String curr)
    {
        ArrayList<String> list = new ArrayList<>();
        String word = curr + prefix;
        if (values != null)
            list.add(word);

        for (Node child : childs)
        {
            ArrayList<String> res = child.getWorld(word);
            if (res.size() != 0)
                list.addAll(res);
        }
        return list;
    }

    public int getMaxDepth(int depth)
    {
        int d = depth;
        for (Node child:childs)
            d = Math.max(d, child.getMaxDepth(depth + 1));
        return d;
    }

    public void load(String prefix, ArrayList<Integer> values, int i, int pIndex)
    {
        if (i >= pIndex)
        {
            childs.add(new Node(prefix, values, new ArrayList<Node>()));
            return;
        }
        childs.get(childs.size() - 1).load(prefix,values, i+1, pIndex);
    }

    public boolean validate()
    {
        ArrayList<Character> chars = new ArrayList<>();
        for (Node child: childs)
        {
            if (!child.validate()|| chars.contains(child.prefix.charAt(0)))
                return false;
            chars.add(child.prefix.charAt(0));
        }
        return true;
    }
}
