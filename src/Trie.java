import java.util.ArrayList;

/**
 * Created by Stepan on 7.10.2016.
 */
public class Trie
{
    Node root = new Node("",null);

    void insert(String key)
    {
        root.insert(key,null,"");
    }

    void insert(String key, int value)
    {
        root.insert(key, value, "");
    }

    ArrayList<Integer> find(String key)
    {
        return root.find(key, "");
    }

    void clearValues()
    {
        root.clear();
    }

    void print()
    {
        int floor = 0;
        root.print(floor);
    }
}

class Node
{
    String prefix;
    ArrayList<Node> childs = new ArrayList<>();
    ArrayList<Integer> values = new ArrayList<>();;

    public Node(String prefix, Integer value)
    {
        if (value != null)
            values.add(value);
        this.prefix = prefix;
    }

    public Node(String prefix, ArrayList<Integer> values, ArrayList<Node> childs)
    {
        this.prefix = prefix;
        this.values = new ArrayList<>();
        for (int v : values)
            this.values.add(v);
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
                    values.add(value);
                return true;
            }
            else if (key.length() < n.length())
            {
                Node node = new Node(prefix, values, childs);
                prefix = key.substring(curr.length());
                values.clear();
                if (value != null)
                    values.add(value);
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

        for (int i = prefix.length() - 1; i > 0; i--)
        {
            String ne = prefix.substring(0,i);
            if (key.indexOf(ne) == 0 && prefix.length() >= ne.length())
            {
                Node node = new Node(n.substring(ne.length()), values, childs);
                prefix = ne;
                values.clear();
                if (value != null)
                    values.add(value);
                childs.clear();
                childs.add(node);
                Node newOne = new Node(key.substring(curr.length()+prefix.length()),value);
                childs.add(newOne);
                return true;
            }
        }
        return false;
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
        values.clear();
        for (Node node : childs)
            node.clear();
    }

    public void print(int floor)
    {
        for (int i=0;i<floor;i++)
            System.out.print("|");

        if (floor > 0)
        {
            System.out.print("-" + prefix);
            if (values.size() > 0)
                System.out.print(" : ");
            for (int i : values)
                System.out.print(i+" ");
            System.out.println();
        }
        for (Node child: childs)
            child.print(floor+1);
    }
}
