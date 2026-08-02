package game.ui.view;

import game.ui.Main;
import game.ui.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class InstructionsView {

    private Stage stage;
    private GameController controller;

    public InstructionsView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void show() {
        // ── This is exactly your original VBox, renamed to "content" ──
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30, 60, 30, 60));
        content.setStyle("-fx-background-color: #0a0a1a;");

        // ── Title ──────────────────────────────────────
        Text title = new Text("📜 HOW TO PLAY");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 64));
        title.setFill(Color.web("#ff6b35"));

        Text subtitle = new Text("Read carefully before starting!");
        subtitle.setFont(Font.font("Courier New", 32));
        subtitle.setFill(Color.web("#666688"));

        // ── Divider ────────────────────────────────────
        Rectangle divider = new Rectangle(600, 2);
        divider.setFill(Color.web("#7b2fff"));

        // ── Instructions sections ──────────────────────
        VBox instructionsBox = new VBox(15);
        instructionsBox.setAlignment(Pos.CENTER_LEFT);

        instructionsBox.getChildren().addAll(
            makeSection("🎯 GOAL",
                "Be the first monster to reach cell 99 with at least 1000 energy to win!"),
            makeSection("🎲 TAKING A TURN",
                "1. Optionally activate your powerup before rolling.\n" +
                "2. Click ROLL DICE to move your monster.\n" +
                "3. Your monster moves and cell effects apply automatically."),
            makeSection("🚪 DOOR CELLS",
                "• SCARER doors (👻): Give energy to SCARER monsters, take from LAUGHER.\n" +
                "• LAUGHER doors (😂): Give energy to LAUGHER monsters, take from SCARER.\n" +
                "• Each door can only be used ONCE — it becomes exhausted (💀) after."),
            makeSection("🃏 CARD CELLS",
                "• Landing here draws a random card from the deck.\n" +
                "• Cards can swap positions, steal energy, freeze, confuse, or shield."),
            makeSection("🚀 CONVEYOR BELTS",
                "• Moves your monster FORWARD by the belt's value."),
            makeSection("🧦 CONTAMINATION SOCKS",
                "• Moves your monster BACKWARD and costs you 100 energy!"),
            makeSection("👾 MONSTER CELLS",
                "• Same role: triggers your powerup for FREE!\n" +
                "• Different role: the monster with more energy takes the other's energy."),
            makeSection("⚡ MONSTER TYPES",
                "• Dasher: Moves at 2x speed. Powerup: 3x speed for 3 turns.\n" +
                "• Dynamo: Doubles all energy changes. Powerup: Freezes opponent.\n" +
                "• MultiTasker: Moves at 0.5x speed, gains +200 on energy changes. Powerup: Normal speed for 2 turns.\n" +
                "• Schemer: Gains +10 on energy changes. Powerup: Steals energy from all.")
        );

        // ── Buttons ────────────────────────────────────
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button backBtn = new Button("← Back");
        backBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        backBtn.setStyle(
            "-fx-background-color: #3a1a1a;" +
            "-fx-text-fill: #ff6b35;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> new StartView(stage, Main.getSceneRoot()).show());

        Button startBtn = new Button("▶ START GAME");
        startBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        startBtn.setStyle(
            "-fx-background-color: #22aa44;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 12 40;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        startBtn.setOnAction(e -> new GameView(stage, controller).show());

        buttons.getChildren().addAll(backBtn, startBtn);

        content.getChildren().addAll(
            title,
            subtitle,
            divider,
            instructionsBox,
            buttons
        );

        // ── NEW: wrap content in a ScrollPane ──────────
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setStyle(
            "-fx-background-color: #0a0a1a;" +
            "-fx-background: #0a0a1a;"
        );

        // ── NEW: root just holds the scroll pane ───────
        VBox root = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.prefWidthProperty().bind(Main.getMainScene().widthProperty());
        root.prefHeightProperty().bind(Main.getMainScene().heightProperty());

        Main.getSceneRoot().getChildren().setAll(root);

        javafx.animation.PauseTransition resetScroll = 
            new javafx.animation.PauseTransition(
                javafx.util.Duration.millis(100));
        resetScroll.setOnFinished(e -> scrollPane.setVvalue(0.0));
        resetScroll.play();
    }

    // ── Unchanged ──────────────────────────────────────
    private VBox makeSection(String header, String body) {
        VBox section = new VBox(4);
        section.setPadding(new Insets(8, 15, 8, 15));
        section.setStyle(
            "-fx-background-color: rgba(255,255,255,0.03);" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: rgba(123,47,255,0.3);" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Text headerText = new Text(header);
        headerText.setFont(Font.font("Courier New", FontWeight.BOLD, 25));
        headerText.setFill(Color.web("#ffdd44"));

        Text bodyText = new Text(body);
        bodyText.setFont(Font.font("Courier New", 20));
        bodyText.setFill(Color.web("#aaaacc"));
        bodyText.setWrappingWidth(760);
        bodyText.setTextAlignment(TextAlignment.LEFT);

        section.getChildren().addAll(headerText, bodyText);
        return section;
    }
}