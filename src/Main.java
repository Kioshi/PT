import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args)
    {
        Trie trie = new Trie();
        try (Stream<String> lines = Files.lines(Paths.get("small.txt"), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> parseLine(line,trie));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        // write your code here
        trie.print();
        /*
        trie.insert("a");
        trie.insert("ab",1);
        trie.insert("abc",2);
        trie.insert("abcd",3);
        trie.insert("abce",3);
        trie.insert("b");
        trie.insert("acdb");
        trie.print();*/
    }

    public static void parseLine(String line,Trie trie)
    {
        String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        System.out.println(line);
        for (String world : words)
            trie.insert(world);
    }
}
