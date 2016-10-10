import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args)
    {
        Trie trie = new Trie(true);

        long startTime = System.nanoTime();
        try (Stream<String> lines = Files.lines(Paths.get("big.txt"), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> parseLine(line,trie));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Create exec time: "+ TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
        //*/
        //trie.print();
        trie.getWorlds();
        trie.find("umpaluma");
        System.out.println("Max depth: "+trie.getMaxDepth());
    }

    public static void parseLine(String line,Trie trie)
    {
        String[] words = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
        for (String world : words)
            trie.insert(world);
    }
}

