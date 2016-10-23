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
 * Created by user on 19.10.2016.
 */
public class GUI {
    private static Stage primaryStage;
    private Stage stageDict;
    private static StyleClassedTextArea taText;
    private TextField tfSearch;
    private File dictFile;
    Trie trie = new Trie();


    public Scene getScene() {
        Scene scene = new Scene(getRoot());
        scene.getStylesheets().add("semestralka/style.css");
        return scene;
    }

    private Parent getRoot() {
        BorderPane rootPaneBP = new BorderPane();

        // adding child elements to the BorderPane, on the specific locations
        rootPaneBP.setCenter(getText());
        //rootPaneBP.setRight(getButtonPane());
       // rootPaneBP.setLeft(getTogglePane());
        rootPaneBP.setBottom(getControl());
       // rootPaneBP.setTop(getMenu());

        return rootPaneBP;
    }

    private Node getText() {
        taText = new StyleClassedTextArea();
        //taText.setParagraphGraphicFactory(LineNumberFactory);
  /*      taText.setPrefColumnCount(30);
        taText.setPrefRowCount(30);
*/
        taText.maxWidth(100);
        return taText;
    }

    private Node getControl(){
        GridPane controls = new GridPane();
        controls.setHgap(10);
        controls.setVgap(10);

        /*
        Text labelDescSearch = new Text("Search: ");
        labelDescSearch.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        controls.add(labelDescSearch,1,1);
        */
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
            /*
            String data = loadFile(file);
            if ((data != null) && (data.length() > 0)) {
                taText.setText(data);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Can't load any data from the file!");
                alert.setTitle("Loading error");
                alert.setHeaderText("ERROR!");
                alert.showAndWait();
            }*/
        }
    }
/*
    private String loadFile(File file) {
        ObservableList<String> newData = FXCollections.observableArrayList();
        String data;
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
            data = newData.toString();
            return data;
        }

    }
*/
    private void getDictStage()
    {

        stageDict = new Stage();
        stageDict.setTitle("Dictionary");
        stageDict.setScene(getDictScene());
        stageDict.show();
    }
    private Scene getDictScene() {
        Scene sceneDict = new Scene(getDictRoot());
        return sceneDict;
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

        //Button buttonOpen = new Button("Open...");
        //paneDictControl.add(buttonOpen,0,1);
        open.setOnAction(event -> loadData());


        //Button buttonSave = new Button("Save");
        //buttonSave.add(buttonSave,1,1);
        save.setOnAction(event -> save(false));

        //Button buttonSaveAs = new Button("Save As");
        //paneDictControl.add(buttonSaveAs,2,1);
        saveAs.setOnAction(event -> save(true));

        menuFile.getItems().addAll(open,save,saveAs);
        menuBar.getMenus().addAll(menuFile);

        return menuBar;
    }

    private Node ovladaciPanel() {
        GridPane paneDictControl = new GridPane();
        paneDictControl.setHgap(10);
        paneDictControl.setVgap(10);

        /*Text labelDescAdd = new Text("Add word: ");
        labelDescAdd.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        paneDictControl.add(labelDescAdd,3,1);*/

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
        ListView listView = new ListView<String>(trie.getWords());
        listView.setEditable(false);
        //listView.setCellFactory(TextFieldListCell.forListView());
        BorderPane.setMargin(listView, new Insets(5));

        return listView;
    }

    private void search()
    {
        String string = tfSearch.getText();
        if (string.isEmpty())
            return;

        String text = taText.getText().replaceAll("[^a-zA-Z ]", " ").toLowerCase();
        int start = -1;
        int end = -1;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == ' ')
            {
                if (end != -1 && end == i-1)
                {
                    //System.out.println(text.substring(start,end+1) +" "+start);
                    trie.insert(text.substring(start,end+1),start);
                }

                start = -1;
                end = -1;
                continue;
            }

            if (start == -1)
                start = i;
            end = i;
        }

        if (end != -1 && end == text.length()-1)
        {
            //System.out.println(text.substring(start,end+1) +" "+start);
            trie.insert(text.substring(start,end+1),start);
        }

        List<Integer> indexes = trie.find(string);
        taText.clearStyle(0,taText.getLength());
        //ArrayList<String> style = new ArrayList<>();
        //style.add("-fx-fill: \"red\"");
        if (indexes != null)
        {
            //hajlajt
            for (int index : indexes)
            {
                //System.out.println(index);
                taText.setStyle(index, index+string.length(), Arrays.asList("bold"));
            }
        }
        else
            throwAlert(string,findSimiliar(string));
    }

    ObservableList<String> findSimiliar(String word)
    {
        ObservableList<String> words = trie.getWords();

        ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>(words.size());
        words.stream().parallel().forEach(s -> map.put(s,Levenshtein.distance(s,word)));

        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>((a, b) ->
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

    void throwAlert(String string,ObservableList<String> words)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Word not found in text");
        alert.setHeaderText("World "+string+" was not found in text. Here are most similiar words from dictionary");
        alert.setGraphic(new ListView<>(words));
        alert.showAndWait();
    }
}
