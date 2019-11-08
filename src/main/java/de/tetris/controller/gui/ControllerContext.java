package de.tetris.controller.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * @author Lam
 */
public class ControllerContext {

    private static MainController mainController;

    private static InfodialogController infodialogController;

    public static void loadInfoDialogController() {
        FXMLLoader fxmlLoader = new FXMLLoader(ControllerContext.class.getResource("/fxml/infodialog.fxml"));
        try {
            Parent root = fxmlLoader.load();
            if (infodialogController == null) {
                infodialogController = fxmlLoader.getController();
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("About");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setMainController(MainController controller) {
        mainController = controller;
    }

    public static MainController getMainController() {
        return mainController;
    }
}
