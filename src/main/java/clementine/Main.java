package clementine;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The Main class is the primary JavaFX Application class for the Clementine GUI.
 * This class extends javafx.application.Application and is responsible for setting up
 * the main application window, loading the FXML layout, and initializing the GUI components.
 * It creates the Clementine backend instance and connects it to the UI controller.
 *
 * @author zhiyu
 */
public class Main extends Application {

    private Clementine clementine = new Clementine("./data/clementine.txt");

    /**
     * Starts the JavaFX application by setting up the primary stage and loading the main window.
     * This method loads the FXML layout file, creates the scene, configures the stage,
     * and injects the Clementine instance into the controller for communication between
     * the GUI and the backend logic.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("clementine(•ө•)❀");
            fxmlLoader.<MainWindow>getController().setClementine(clementine); // inject the Clementine instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

