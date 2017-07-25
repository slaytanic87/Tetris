package de.tetris;

import de.tetris.controller.game.MainGameLoop;
import de.tetris.controller.gui.GlobalController;
import de.tetris.controller.gui.MainController;
import de.tetris.controller.interfaces.GameInputBusEvent;
import de.tetris.controller.interfaces.GameKeyEvent;
import de.tetris.model.TetrisField;
import de.tetris.service.CommunicationServer;
import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.dropwizard.MatchType;
import io.vertx.rxjava.core.Vertx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lam
 */
@Slf4j
public class MainApp extends Application {

    private Scene scene;
    private MainGameLoop gameLoop;
    private TetrisField field;

    public final static int DEFAULT_NUMBER_OF_COLS = 20;
    public final static int DEFAULT_NUMBER_OF_ROWS = 40;

    private final static int GAME_DURATION_MILLIS = 500;

    private Vertx vertx;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.calcCellSize(DEFAULT_NUMBER_OF_COLS, DEFAULT_NUMBER_OF_ROWS);
        GlobalController.setMainController(controller);

        field = new TetrisField(DEFAULT_NUMBER_OF_COLS, DEFAULT_NUMBER_OF_ROWS);
        gameLoop = new MainGameLoop(field);
        scene = new Scene(root);

        scene.addEventHandler(KeyEvent.ANY, new GameKeyEvent(gameLoop));
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        gameLoop.setDuration(Duration.millis(GAME_DURATION_MILLIS));

        vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
                new DropwizardMetricsOptions()
                        .setRegistryName("vertx")
                        .addMonitoredHttpClientEndpoint(
                                new Match().setValue(".*").setType(MatchType.REGEX))
                        .setEnabled(true)
        ));

        GameInputBusEvent gameInputBusVerticle = new GameInputBusEvent(gameLoop);
        CommunicationServer communicationServerVerticle = new CommunicationServer();

        ((io.vertx.core.Vertx) vertx.getDelegate()).deployVerticle(gameInputBusVerticle);
        ((io.vertx.core.Vertx) vertx.getDelegate()).deployVerticle(communicationServerVerticle);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
