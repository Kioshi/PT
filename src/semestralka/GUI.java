package semestralka;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Created by Kalivoda on 19.10.2016.
 *
 */
public class GUI {
    private static Stage primaryStage;
    private Stage stageDict;
    private static StyleClassedTextArea taText;
    private TextField tfSearch;
    private File dictFile;
    private boolean textChanged = false;
    private Trie trie = new Trie();

    public GUI(Stage primaryStage)
    {
        GUI.primaryStage = primaryStage;
    }


    Scene getScene() {
        Scene scene = new Scene(getRoot());
        scene.getStylesheets().add("semestralka/style.css");
        return scene;
    }

    private Parent getRoot() {
        BorderPane rootPaneBP = new BorderPane();

        rootPaneBP.setCenter(getText());
        rootPaneBP.setBottom(getControl());
        return rootPaneBP;
    }

    private Node getText() {
        taText = new StyleClassedTextArea();
        taText.setOnKeyTyped(event -> textChanged = true);
        taText.maxWidth(100);
        return taText;
    }

    private Node getControl(){
        GridPane controls = new GridPane();
        controls.setHgap(10);
        controls.setVgap(10);

        tfSearch = new TextField();
        controls.add(tfSearch,1,1);

        Button buttonSearch = new Button("Search");
        buttonSearch.setOnAction(event -> search());
        controls.add(buttonSearch,2,1);

        Button buttonDict = new Button("Dictionary");
        controls.add(buttonDict,3,1);
        buttonDict.setOnAction(event -> getDictStage());

        controls.setPadding(new Insets(5));
        controls.setAlignment(Pos.CENTER);

        return controls;
    }


    private void loadData() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null)
        {
            Executors.newSingleThreadExecutor().submit(() ->
            {
                if (file.getName().matches(".*\\.dic$"))
                {
                    trie.load(file);
                    dictFile = file;
                }
                else
                    trie.create(file);
            });
        }
    }
    private void getDictStage()
    {

        stageDict = new Stage();
        stageDict.setTitle("Dictionary");
        stageDict.setScene(getDictScene());
        stageDict.show();
    }
    private Scene getDictScene() {
        return new Scene(getDictRoot());
    }

    private Parent getDictRoot(){
        BorderPane rootDictPane = new BorderPane();
        rootDictPane.setCenter(seznam());
        rootDictPane.setBottom(ovladaciPanel());
        rootDictPane.setTop(newMenu());
        return rootDictPane;
    }

    private Node newMenu()
    {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As");

        open.setOnAction(event -> loadData());
        save.setOnAction(event -> save(false));
        saveAs.setOnAction(event -> save(true));

        menuFile.getItems().addAll(open,save,saveAs);
        menuBar.getMenus().addAll(menuFile);

        return menuBar;
    }

    private Node ovladaciPanel() {
        GridPane paneDictControl = new GridPane();
        paneDictControl.setHgap(10);
        paneDictControl.setVgap(10);

        TextField tfAdd = new TextField();
        paneDictControl.add(tfAdd,0,2);

        Button buttonAdd = new Button("Add word");
        buttonAdd.setOnAction(event ->
        {
            if (!tfAdd.getText().isEmpty())
                trie.insert(tfAdd.getText());
        });
        paneDictControl.add(buttonAdd,1,2);

        paneDictControl.setPadding(new Insets(5));
        paneDictControl.setAlignment(Pos.CENTER);
        return paneDictControl;
    }

    private void save(boolean saveAs)
    {
        if (dictFile == null || saveAs)
        {
            FileChooser fileChooser = new FileChooser();
            File dictFile = fileChooser.showOpenDialog(stageDict);
            if (dictFile == null)
                return;
        }
        try
        {
            trie.print(new PrintStream(new FileOutputStream(dictFile)));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private ListView seznam() {
        ListView listView = new ListView<>(trie.getWords());
        listView.setEditable(false);
        BorderPane.setMargin(listView, new Insets(5));

        return listView;
    }

    private void search()
    {
        String string = tfSearch.getText();
        if (string.isEmpty())
            return;

        updateDictionary();

        List<Integer> indexes = trie.find(string);
        taText.clearStyle(0,taText.getLength());
        if (indexes != null)
            for (int index : indexes)
            {
                taText.setStyle(index, index + string.length(), Collections.singletonList("bold"));
            }
        else
            throwAlert(string,findSimiliar(string));
    }

    private void updateDictionary()
    {
        if (!textChanged)
            return;

        textChanged = false;
        String text = taText.getText().replaceAll("[^a-zA-Z ]", " ").toLowerCase();
        int start = -1;
        int end = -1;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == ' ')
            {
                if (end != -1 && end == i-1)
                    trie.insert(text.substring(start,end+1),start);

                start = -1;
                end = -1;
                continue;
            }

            if (start == -1)
                start = i;
            end = i;
        }

        if (end != -1 && end == text.length()-1)
            trie.insert(text.substring(start,end+1),start);
    }

    private ObservableList<String> findSimiliar(String word)
    {
        ObservableList<String> words = trie.getWords();

        ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>(words.size());
        words.stream().parallel().forEach(s -> map.put(s,Levenshtein.distance(s,word)));

        TreeMap<String, Integer> sorted_map = new TreeMap<>((a, b) ->
        {
            if (map.get(a) < map.get(b))
                return -1;
            else
                return 1;
        });
        sorted_map.putAll(map);

        ObservableList<String> simWords = FXCollections.observableArrayList();
        for(Map.Entry<String, Integer> pair: sorted_map.entrySet())
        {
            if(simWords.size() >= 10)
                break;
            simWords.add(pair.getKey());
        }

        return simWords;
    }

    private void throwAlert(String string, ObservableList<String> words)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Word not found in text");
        alert.setHeaderText("World "+string+" was not found in text. Here are most similiar words from dictionary");
        alert.setGraphic(new ListView<>(words));
        alert.showAndWait();
    }
}
