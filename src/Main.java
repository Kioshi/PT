import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws FileNotFoundException
    {
        Trie trie = new Trie(true);
        // nacitanej soubor: 1079293 slov 6344520 Bytu load time 7221 ms
        long startTime = System.nanoTime();
/*
        try (Stream<String> lines = Files.lines(Paths.get("big.txt"), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> parseLine(line,trie));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //*/
        trie.load("out.txt"); // 328130 Bytu load time 607ms
        System.out.println("Create exec time: "+ TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
        //*/
        //trie.print();
        System.out.println("Uniq words: "+trie.getWorlds().size());
        System.out.println("Max depth: "+trie.getMaxDepth());
        trie.print();
        System.out.println(trie.validate());
        //trie.print(new PrintStream("out.txt"));
    }

    public static void parseLine(String line,Trie trie)
    {
        String[] words = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
        //System.out.println(line);
        for (String world : words)
            trie.insert(world);
    }
}

