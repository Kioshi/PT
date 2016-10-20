import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by user on 19.10.2016.
 */
public class GUI {
    public static Stage primaryStage;
    public static TextArea taText;
    public ListView listView;
    public TextField tfAdd;

    public Scene getScene() {
        Scene scene = new Scene(getRoot());
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
        taText = new TextArea("You can write here ...");
        taText.setPrefColumnCount(30);
        taText.setPrefRowCount(30);

        taText.maxWidth(100);
        return taText;
    }

    private Node getControl(){
        GridPane controls = new GridPane();
        controls.setHgap(10);
        controls.setVgap(10);


        Button buttonOpen = new Button("Open...");
        controls.add(buttonOpen,1,2);
        buttonOpen.setOnAction(event -> loadData());

        Button buttonDict = new Button("Dictionary");
        controls.add(buttonDict,2,2);
        buttonDict.setOnAction(event -> getDictStage());

        controls.setPadding(new Insets(5));
        controls.setAlignment(Pos.CENTER);

        return controls;
    }


    private void loadData() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            String data = loadFile(file);
            if ((data != null) && (data.length() > 0)) {
                taText.setText(data);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Can't load any data from the file!");
                alert.setTitle("Loading error");
                alert.setHeaderText("ERROR!");
                alert.showAndWait();
            }
        }
    }

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

    private void getDictStage()
    {

        Stage stageDict = new Stage();
        stageDict.setTitle("Dictionary");
        stageDict.setScene(getDictScene());
        stageDict.show();
    }
    public Scene getDictScene() {
        Scene sceneDict = new Scene(getDictRoot());
        return sceneDict;
    }

    public Parent getDictRoot(){
        BorderPane rootDictPane = new BorderPane();
        rootDictPane.setCenter(seznam());
        rootDictPane.setBottom(ovladaciPanel());
        return rootDictPane;
    }

    private Node ovladaciPanel() {
        GridPane paneDictControl = new GridPane();
        paneDictControl.setHgap(10);
        paneDictControl.setVgap(10);

        Text labelDescSearch = new Text("Search: ");
        labelDescSearch.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        paneDictControl.add(labelDescSearch,1,1);

        TextField tfSearch = new TextField();
        paneDictControl.add(tfSearch,1,2);

        Button buttonSearch = new Button("Search");

        paneDictControl.add(buttonSearch,2,2);

        Text labelDescAdd = new Text("Add word: ");
        labelDescAdd.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        paneDictControl.add(labelDescAdd,3,1);

        tfAdd = new TextField();
        paneDictControl.add(tfAdd,3,2);

        Button buttonAdd = new Button("Add word");
        buttonAdd.setOnAction(event -> listView.getItems().add(tfAdd.getText()));
        paneDictControl.add(buttonAdd,4,2);

        Button buttonSave = new Button("Save");

        paneDictControl.add(buttonSave,5,2);

        paneDictControl.setPadding(new Insets(5));
        paneDictControl.setAlignment(Pos.CENTER);
        return paneDictControl;
    }

    private ListView seznam() {
        listView = new ListView<String>();
        listView.setEditable(false);
        //listView.setCellFactory(TextFieldListCell.forListView());
        BorderPane.setMargin(listView, new Insets(5));

        return listView;
    }

 
}
