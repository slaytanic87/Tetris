package de.tetris;

import de.tetris.controller.game.MainGameLoop;
import de.tetris.controller.gui.GlobalController;
import de.tetris.controller.gui.MainController;
import de.tetris.controller.interfaces.DataModelVerticle;
import de.tetris.controller.interfaces.GameInputBusEventVerticle;
import de.tetris.controller.interfaces.GameKeyEvent;
import de.tetris.model.TetrisField;
import de.tetris.service.RestVerticle;
import de.tetris.utils.MessageEventUtils;
import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.dropwizard.MatchType;
import io.vertx.rxjava.core.Vertx;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Lam
 */
@Slf4j
public class MainApp extends Application {

    private volatile TetrisField field;

    private final static int DEFAULT_NUMBER_OF_COLS = 20;
    private final static int DEFAULT_NUMBER_OF_ROWS = 40;
    private final static int GAME_DURATION_MILLIS = 500;

    private Vertx vertx;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.calcCellSize(DEFAULT_NUMBER_OF_COLS, DEFAULT_NUMBER_OF_ROWS);
        GlobalController.setMainController(controller);

        vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
                new DropwizardMetricsOptions()
                        .setRegistryName("vertx")
                        .addMonitoredHttpClientEndpoint(
                                new Match().setValue(".*").setType(MatchType.REGEX))
                        .setEnabled(true)
        ));
        MessageEventUtils.getInstance().setEventBus(vertx.eventBus());

        field = new TetrisField(DEFAULT_NUMBER_OF_COLS, DEFAULT_NUMBER_OF_ROWS);
        MainGameLoop gameLoop = new MainGameLoop(field);
        Scene scene = new Scene(root);

        scene.addEventHandler(KeyEvent.ANY, new GameKeyEvent(gameLoop));
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        gameLoop.setDuration(Duration.millis(GAME_DURATION_MILLIS));


        GameInputBusEventVerticle gameInputBusVerticle = new GameInputBusEventVerticle(gameLoop);
        DataModelVerticle dataModelVerticle = new DataModelVerticle(field);
        RestVerticle restVerticle = new RestVerticle();

        ((io.vertx.core.Vertx) vertx.getDelegate()).deployVerticle(dataModelVerticle);
        ((io.vertx.core.Vertx) vertx.getDelegate()).deployVerticle(gameInputBusVerticle);
        ((io.vertx.core.Vertx) vertx.getDelegate()).deployVerticle(restVerticle);

        primaryStage.show();
    }

    private void loadProperties() {
        Properties applicationProperties = new Properties();
        try {
            applicationProperties.load(this.getClass().getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException("unable to read application.properties included in file");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}