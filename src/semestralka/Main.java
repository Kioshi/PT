package semestralka;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.FileNotFoundException;



public class Main extends Application{

    /**
     * Launch gui
     * @param args argument
     */
    public static void main(String[] args) throws FileNotFoundException
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GUI gui = new GUI(primaryStage);

        primaryStage.setTitle("PT");
        primaryStage.setScene(gui.getScene());

        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(400);
        primaryStage.show();
    }

}