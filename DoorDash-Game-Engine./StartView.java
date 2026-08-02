package game.ui.view;

import java.io.IOException;

import game.ui.Main;
import game.ui.controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StartView {

    private Stage stage;
    private String selectedRole = null;
	private StackPane sceneRoot;

    public StartView(Stage stage, StackPane sceneRoot) {
        this.stage = stage;
        this.sceneRoot = sceneRoot;
    }

    public void show() {
        VBox root = new VBox(50);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0a0a1a;");
        // ── Make root fill the whole screen ───────────
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.prefWidthProperty().bind(Main.getMainScene().widthProperty());
        root.prefHeightProperty().bind(Main.getMainScene().heightProperty());
        // ── Title ──────────────────────────────────────
        Text title = new Text("DOOR DASH");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 100));
        title.setFill(Color.web("#ff6b35"));

        Text subtitle = new Text("Scare vs Laugh Touchdown");
        subtitle.setFont(Font.font("Courier New", FontWeight.BOLD, 50));
        subtitle.setFill(Color.web("#7b2fff"));

        // ── Choose Role Label ──────────────────────────
        Text chooseText = new Text("CHOOSE YOUR SIDE");
        chooseText.setFont(Font.font("Courier New", FontWeight.BOLD, 40));
        chooseText.setFill(Color.web("#ffffff"));

        // ── Role Buttons ───────────────────────────────
        Button scarerBtn = new Button("👻 SCARER");
        scarerBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 32));
        scarerBtn.setStyle(
            "-fx-background-color: #ff6b35;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 12 30;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );

        Button laugherBtn = new Button("😂 LAUGHER");
        laugherBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 32));
        laugherBtn.setStyle(
            "-fx-background-color: #7b2fff;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 12 30;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );

        // ── Selection Feedback Label ───────────────────
        Text selectedText = new Text("");
        selectedText.setFont(Font.font("Courier New", FontWeight.BOLD, 32));
        selectedText.setFill(Color.web("#ffdd44"));

        // ── Button Actions ─────────────────────────────
        scarerBtn.setOnAction(e -> {
            selectedRole = "SCARER";
            selectedText.setText("✅ You selected: SCARER");
        });

        laugherBtn.setOnAction(e -> {
            selectedRole = "LAUGHER";
            selectedText.setText("✅ You selected: LAUGHER");
        });

        // ── Start Button ───────────────────────────────
        Button startBtn = new Button("▶  START GAME");
        startBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 30));
        startBtn.setStyle(
            "-fx-background-color: #22aa44;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 15 50;" +
            "-fx-background-radius: 30;" +
            "-fx-cursor: hand;"
        );

        startBtn.setOnAction(e -> {
            if (selectedRole == null) {
                selectedText.setFill(Color.web("#ff4444"));
                selectedText.setText("⚠ Please select a role first!");
            } else {
                try {
                    GameController controller = new GameController(selectedRole);

                    // ── Go to instructions first ───────────
                    InstructionsView instructionsView =
                        new InstructionsView(stage, controller);
                    instructionsView.show();

                } catch (IOException ex) {
                    selectedText.setFill(Color.web("#ff4444"));
                    selectedText.setText("⚠ Error: " + ex.getMessage());
                }
            }
        });

        // ── HBox for the two role buttons side by side ─
        HBox roleButtons = new HBox(30);
        roleButtons.setAlignment(Pos.CENTER);
        roleButtons.getChildren().addAll(scarerBtn, laugherBtn);

        // ── Add everything to root ─────────────────────
        root.getChildren().addAll(
            title,
            subtitle,
            chooseText,
            roleButtons,
            selectedText,
            startBtn
        );
        Main.getSceneRoot().getChildren().setAll(root);
        
    }
}