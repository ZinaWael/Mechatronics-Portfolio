package game.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import game.ui.view.StartView;

public class Main extends Application {

    // ── shared scene and root ──────────────────────
    private static StackPane sceneRoot;
    private static Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DoorDash: Scare vs Laugh Touchdown");
        primaryStage.setResizable(true);

        sceneRoot = new StackPane();
        mainScene = new Scene(sceneRoot, 1280, 820);

        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setMaximized(true);

        StartView startView = new StartView(primaryStage, sceneRoot);
        startView.show();
    }
    // ── static getters ─────────────────────────────
    public static StackPane getSceneRoot() {
        return sceneRoot;
    }

    public static Scene getMainScene() {
        return mainScene;
    }


    public static void main(String[] args) {
        launch(args);
    }
}