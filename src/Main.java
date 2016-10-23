import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class Main extends Application{

    public static void main(String[] args) throws FileNotFoundException
    {
        launch(args);
  //      Trie trie = new Trie(true);
        // nacitanej soubor: 1079293 slov 6344520 Bytu load time 7221 ms
/*
        try (Stream<String> lines = Files.lines(Paths.get("big.txt"), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> parseLine(line,trie));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //*/
        /*trie.load("out.txt"); // 328130 Bytu load time 607ms
        System.out.println("Create exec time: "+ TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
        //*/
        //trie.print();
       /* System.out.println("Uniq words: "+trie.getWorlds().size());
        System.out.println("Max depth: "+trie.getMaxDepth());
        //trie.print();
        System.out.println(trie.validate());
        //trie.print(new PrintStream("out.txt"));
        System.out.println("Hledane slovo: ");
        Scanner scanner = new Scanner(System.in);
        String string = scanner.next();
        ArrayList<String> words = trie.getWorlds();
        ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>(words.size());
        startTime = System.nanoTime();
        words.stream().parallel().forEach(s -> map.put(s,Levenshtein.distance(s,string)));
        System.out.println("Dist exec time: "+ TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS));

        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(map);
        //sorted_map.forEach((s, integer) -> System.out.println(s+" "+integer));
        int count = 0;
        for(Map.Entry<String, Integer> pair: sorted_map.entrySet())
        {
            if(count++ >= 10)
                break;
            System.out.println(pair.getKey()+" "+pair.getValue());
        }*/
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        GUI gui = new GUI();

        primaryStage.setTitle("PT");
        primaryStage.setScene(gui.getScene());

        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(400);
        primaryStage.show();
    }

}
/*
class ValueComparator implements Comparator<String>
{
    Map<String, Integer> base;

    public ValueComparator(ConcurrentHashMap<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(String a, String b) {
        if (base.get(a) < base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
*/