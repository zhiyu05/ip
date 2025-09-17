package clementine;

import javafx.application.Application;

/**
 * The Launcher class serves as the entry point for the JavaFX GUI version of the Clementine application.
 * This class is responsible for bootstrapping the JavaFX application by launching the Main class
 * through the JavaFX Application framework. It acts as a wrapper to ensure proper JavaFX initialization.
 *
 * @author zhiyu
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
