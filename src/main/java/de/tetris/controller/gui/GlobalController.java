package de.tetris.controller.gui;

import de.tetris.controller.gui.MainController;

/**
 * @author Lam
 */
public class GlobalController {

    private static MainController mainController;

    public static void setMainController(MainController controller) {
        mainController = controller;
    }

    public static MainController getMainController() {
        return mainController;
    }
}
