package de.tetris.controller.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Lam, Le (msg systems ag) 2017
 */
@Slf4j
public class InfodialogController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void closeDialog(ActionEvent actionEvent) {
        log.debug("Close Infodialog");
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

}
