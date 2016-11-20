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

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kalivoda on 19.10.2016.
 *
 */
public class GUI {
    /**
     * Primary stage for alert dialog
     */
    private Stage primaryStage;
    /**
     * Dictionary window stage
     */
    private Stage stageDict;
    /**
     * StyleClassedTextArea node reference
     */
    private static StyleClassedTextArea taText;
    /**
     * Search textfield reference
     */
    private TextField tfSearch;
    /**
     * Dictionary file referance for saving
     */
    private File dictFile;
    /**
     * Indicator if text was changed, to prevent unnecessary saving to dictionary on multiple searches
     */
    private boolean textChanged = false;
    /**
     * Trie used for dictionary and searching
     */
    private final Trie trie = new Trie();

    /**
     * Constructor for retrieving primaryStage
     * @param primaryStage Primary stage
     */
    public GUI(final Stage primaryStage)
    {
        primaryStage.setOnCloseRequest(we ->
        {
            if (stageDict != null)
                stageDict.close();
        });
    }

    /**
     * Builds primaryStage scene
     * @return Scene
     */
    Scene getScene() {
        Scene scene = new Scene(getRoot());
        scene.getStylesheets().add("semestralka/style.css");
        return scene;
    }

    /**
     * Creates root for scene
     * @return BorderPane root
     */
    private Parent getRoot() {
        BorderPane rootPaneBP = new BorderPane();

        rootPaneBP.setCenter(getText());
        rootPaneBP.setBottom(getControl());
        return rootPaneBP;
    }

    /**
     * Creates main text area
     * @return StyleClassedTextArea
     */
    private Node getText() {
        taText = new StyleClassedTextArea();
        taText.setOnKeyTyped(event -> textChanged = true);
        taText.maxWidth(100);
        return taText;
    }

    /**
     * Creates bottom controll panel
     * @return GridPane
     */
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

        Button buttonOpen = new Button("Open");
        buttonOpen.setOnAction(event -> loadFile());
        controls.add(buttonOpen,4,1);

        controls.setPadding(new Insets(5));
        controls.setAlignment(Pos.CENTER);

        return controls;
    }


    /**
     * Show file chooser and tells trie to create or load dictionary according to file type
     */
    private void loadFile(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null)
        {
            ObservableList<String> data = readFile(file);
            if ((data != null) && (data.size() > 0))
            {
                String text = data.toString();
                taText.setWrapText(true);
                taText.insertText(0,text);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Nepodarilo se nacist zadna data ze souboru!");
                alert.setTitle("Loading error");
                alert.setHeaderText("Chyba souboru!");
                alert.showAndWait();
                }
            }
        }


    private ObservableList<String> readFile(File file) {
        ObservableList<String> newData = FXCollections.observableArrayList();

        if (file == null) {
            return null;
        } else {
            FileReader reader;
            BufferedReader input;
            try {
                reader = new FileReader(file);
                input = new BufferedReader(reader);

                for (String line = input.readLine(); line != null; line = input.readLine()) {
                    newData.add(line);
                }

                input.close();
                reader.close();
            } catch (IOException e) {
                return null;
            }

            return newData;
        }

    }

    /**
     * Show file chooser and tells trie to create or load dictionary according to file type
     */
    private void loadData() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null)
        {
            //Executors.newSingleThreadExecutor().submit(() ->
            {
                if (file.getName().matches(".*\\.dic$"))
                {
                    trie.load(file);
                    dictFile = file;
                }
                else
                    trie.create(file);
                
                textChanged = true;
            }//);
        }
    }

    /**
     * Create dictionary stage
     */
    private void getDictStage() {
        if (stageDict != null)
            return;
        stageDict = new Stage();
        stageDict.setTitle("Dictionary");
        stageDict.setScene(getDictScene());
        stageDict.show();
        stageDict.setOnCloseRequest(we -> stageDict = null);
    }
     /**
     * Create dictionary scene
     * @return Scene
     */
    private Scene getDictScene() {
        return new Scene(getDictRoot());
    }

    /**
     * Create dictionary root
     * @return BorderPane
     */
    private Parent getDictRoot(){
        BorderPane rootDictPane = new BorderPane();
        rootDictPane.setCenter(getListView());
        rootDictPane.setBottom(dicControlPanel());
        rootDictPane.setTop(newMenu());
        return rootDictPane;
    }

    /**
     * Create dictionary menu for open, save and save as options
     * @return MenuBar
     */
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

    /**
     * Create dictioanry bottom control panel
     * @return GridPane
     */
    private Node dicControlPanel() {
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

    /**
     * Saves dictionary intro file
     * @param saveAs determines if uses dictFile or show file chooser
     */
    private void save(boolean saveAs)
    {
        try
        {
            if (dictFile == null || saveAs)
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Trie dictionary file (*.dic)","*.dic"));
                File dictFile = fileChooser.showSaveDialog(primaryStage);
                if (dictFile != null)
                    trie.print(new PrintStream(new FileOutputStream(dictFile)));
                return;
            }
            trie.print(new PrintStream(new FileOutputStream(dictFile)));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Creates listView for center of dictionary window
     * @return ListView
     */
    private ListView getListView() {
        ListView listView = new ListView<>(trie.getWords());
        listView.setEditable(false);
        BorderPane.setMargin(listView, new Insets(5));

        return listView;
    }

    /**
     * Update dictionary then search for specified word and highligh them or throw alert with similiar words
     */
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

    /**
     * Updates dictionary with text if textChanged is true
     */
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

    /**
     * Find up to 10 similiar words with Levenstein distance
     * @param word target word
     * @return list of similiar words
     */
    private ObservableList<String> findSimiliar(String word)
    {
        ObservableList<String> words = trie.getWords();

        ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>(words.size());
        words.stream().parallel().forEach(s -> map.put(s, Levenstein.distance(s,word)));

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

    /**
     * Show alert dialog when word wasnt in text, with list of similiar words in dictionary
     * @param string Word user was looking for
     * @param words List of similiar words
     */
    private void throwAlert(String string, ObservableList<String> words)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Word not found in text");
        alert.setHeaderText("World "+string+" was not found in text. Here are most similiar words from dictionary");
        alert.setGraphic(new ListView<>(words));
        alert.showAndWait();
    }
}
