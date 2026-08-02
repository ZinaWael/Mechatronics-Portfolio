package game.ui.view;

import com.sun.javafx.scene.control.skin.LabelSkin;

import game.engine.Constants;
import game.engine.Role;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Monster;
import game.ui.Main;
import game.ui.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane;

public class GameView {

    private Stage stage;
    private GameController controller;
 // cell size in pixels
    private static final int CELL_SIZE = 58;

    // we store each cell pane so we can update them later
    private StackPane[] cellPanes = new StackPane[100];
    private Circle playerToken;
    private Circle opponentToken;
    private Text turnText;
    private Text diceText;
    private Text eventText;
 // player panel labels
    private Text playerNameText;
    private Text playerTypeText;
    private Text playerRoleText;
    private Text playerEnergyText;
    private Text playerPositionText;
    private Text playerStatusText;
    private Button rollBtn;
    private Button powerupBtn;
    // opponent panel labels
    private Text opponentNameText;
    private Text opponentTypeText;
    private Text opponentRoleText;
    private Text opponentEnergyText;
    private Text opponentPositionText;
    private Text opponentStatusText;
    private VBox logEntries;
    private ScrollPane logScroll;
    private GridPane boardGrid;
    private VBox playerPanel;
    private VBox opponentPanel;
    private Button drawCardBtn;
    private Button reloadDeckBtn;
    private StackPane deckVisual;
    private Text deckCountText;
    private boolean cardCellLanded = false;
    private VBox playerTeamBox;
    private VBox opponentTeamBox;
    // ── Constructor ────────────────────────────────────
    public GameView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    // ── Show ───────────────────────────────────────────
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0a1a;");

        root.prefWidthProperty().bind(Main.getMainScene().widthProperty());
        root.prefHeightProperty().bind(Main.getMainScene().heightProperty());
        root.maxWidthProperty().bind(Main.getMainScene().widthProperty());
        root.maxHeightProperty().bind(Main.getMainScene().heightProperty());

        root.setLeft(buildPlayerPanel());
        root.setRight(buildOpponentPanel());

        GridPane board = buildBoard();
        Pane tokenLayer = buildTokenLayer();
        StackPane boardStack = new StackPane(board, tokenLayer);

        HBox center = new HBox(boardStack);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(10));
        center.setStyle("-fx-background-color: #0a0a1a;");
        root.setCenter(center);

        root.setBottom(buildBottomBar());

        Main.getSceneRoot().getChildren().setAll(root);
        Scene scene = Main.getMainScene();

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            resizeBoard(newVal.doubleValue(), scene.getHeight());
            scaleButtons();
            scalePanelFonts();
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            resizeBoard(scene.getWidth(), newVal.doubleValue());
            scaleButtons();
            scalePanelFonts();
        });

        updateAll();
        addLog("🎮 Game started! " + controller.getCurrent().getName() + " goes first.", "#aaffaa");

        javafx.application.Platform.runLater(() -> {
            resizeBoard(scene.getWidth(), scene.getHeight());
            scaleButtons();
            scalePanelFonts();
        });

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    showWinScreen(controller.getPlayer());
                    break;
                case O:
                    showWinScreen(controller.getOpponent());
                    break;
                case E:
                    controller.getPlayer().setEnergy(
                        controller.getPlayer().getEnergy() + 100
                    );
                    addLog("🔧 DEBUG: Player energy +100 → " +
                        controller.getPlayer().getEnergy(), "#ffaa00");
                    updateAll();
                    break;
                default:
                    break;
            }
        });
    }

    // ── Player Panel (Left) ────────────────────────────
    private VBox buildPlayerPanel() {
        playerPanel = new VBox(8);
        VBox panel = playerPanel;
        panel.setPadding(new Insets(15));
        panel.setStyle(
            "-fx-background-color: #0e0e28;" +
            "-fx-border-color: #ff6b35;" +
            "-fx-border-width: 0 2 0 0;"
        );

        // ── Bind width to 15% of scene ─────────────────
        panel.prefWidthProperty().bind(
            Main.getMainScene().widthProperty().multiply(0.15)
        );

        Text header = new Text("🎮 YOU");
        header.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        header.setFill(Color.web("#ff6b35"));

        playerNameText     = makeInfoText("—");
        playerTypeText     = makeInfoText("—");
        playerRoleText     = makeInfoText("—");
        playerEnergyText   = makeInfoText("—");
        playerPositionText = makeInfoText("—");
        playerStatusText   = makeInfoText("—");

        panel.getChildren().addAll(
            header,
            makeLabel("Name:"),     playerNameText,
            makeLabel("Type:"),     playerTypeText,
            makeLabel("Role:"),     playerRoleText,
            makeLabel("Energy:"),   playerEnergyText,
            makeLabel("Position:"), playerPositionText,
            makeLabel("Status:"),   playerStatusText
        );
     // ── Scarer team (player is scarer) ────────────
        playerTeamBox = buildTeamSection(
        	    controller.getPlayer().getOriginalRole()
        	);
        	panel.getChildren().add(new Separator());
        	panel.getChildren().add(playerTeamBox);

        return panel;
    }
    // ── Opponent Panel (Right) ─────────────────────────
    private VBox buildOpponentPanel() {
        opponentPanel = new VBox(8);
        VBox panel = opponentPanel;
        panel.setPadding(new Insets(15));
        panel.setStyle(
            "-fx-background-color: #0e0e28;" +
            "-fx-border-color: #7b2fff;" +
            "-fx-border-width: 0 0 0 2;"
        );

        // ── Bind width to 15% of scene ─────────────────
        panel.prefWidthProperty().bind(
            Main.getMainScene().widthProperty().multiply(0.15)
        );

        Text header = new Text("🤖 OPPONENT");
        header.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        header.setFill(Color.web("#7b2fff"));

        opponentNameText     = makeInfoText("—");
        opponentTypeText     = makeInfoText("—");
        opponentRoleText     = makeInfoText("—");
        opponentEnergyText   = makeInfoText("—");
        opponentPositionText = makeInfoText("—");
        opponentStatusText   = makeInfoText("—");

        panel.getChildren().addAll(
            header,
            makeLabel("Name:"),     opponentNameText,
            makeLabel("Type:"),     opponentTypeText,
            makeLabel("Role:"),     opponentRoleText,
            makeLabel("Energy:"),   opponentEnergyText,
            makeLabel("Position:"), opponentPositionText,
            makeLabel("Status:"),   opponentStatusText
        );
     // ── Opponent's team ────────────────────────────
        opponentTeamBox = buildTeamSection(
        	    controller.getOpponent().getOriginalRole()
        	);
        	panel.getChildren().add(new Separator());
        	panel.getChildren().add(opponentTeamBox);
        return panel;
    }

    // ── Bottom Bar ─────────────────────────────────────
    private HBox buildBottomBar() {
    	 HBox bar = new HBox(20);
    	    bar.setAlignment(Pos.CENTER_LEFT);
    	    bar.setPadding(new Insets(10, 20, 10, 20));
    	    bar.setStyle(
    	        "-fx-background-color: #0e0e28;" +
    	        "-fx-border-color: #7b2fff;" +
    	        "-fx-border-width: 2 0 0 0;"
    	    );

    	    // ── Bind height to 15% of scene ────────────────
    	    bar.prefHeightProperty().bind(
    	        Main.getMainScene().heightProperty().multiply(0.15)
    	    );

        // ── Turn indicator ─────────────────────────────
        turnText = new Text("TURN: —");
        turnText.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        turnText.setFill(Color.web("#ffdd44"));

        // ── Dice result ────────────────────────────────
        diceText = new Text("🎲 —");
        diceText.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        diceText.setFill(Color.web("#ffdd44"));

        // ── Event message ──────────────────────────────
        eventText = new Text("");
        eventText.setFont(Font.font("Courier New", 13));
        eventText.setFill(Color.web("#aaffaa"));
        eventText.setWrappingWidth(300);

        // ── Powerup button ─────────────────────────────
        powerupBtn = new Button(
        	    "⚡ POWERUP (" + Constants.POWERUP_COST + ")"
        	);
        powerupBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        powerupBtn.setStyle(
            "-fx-background-color: #3a1a6a;" +
            "-fx-text-fill: #dd88ff;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        powerupBtn.setOnAction(e -> handlePowerup());

        // ── Roll button ────────────────────────────────
        rollBtn = new Button("🎲 ROLL DICE");
        rollBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        rollBtn.setStyle(
            "-fx-background-color: #1a4a1a;" +
            "-fx-text-fill: #88ffaa;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        rollBtn.setOnAction(e -> handleRoll());

        // ── Separator ──────────────────────────────────
        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);

        Separator sep2 = new Separator();
        sep2.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // ── Left side: turn + buttons ──────────────────
        VBox leftSide = new VBox(8);
        leftSide.setAlignment(Pos.CENTER_LEFT);
        leftSide.getChildren().addAll(turnText, powerupBtn, rollBtn);

        // ── Middle side: dice + event ──────────────────
        VBox middleSide = new VBox(5);
        middleSide.setAlignment(Pos.CENTER_LEFT);
        middleSide.getChildren().addAll(diceText, eventText);

        // ── Right side: game log ───────────────────────
        VBox logSection = new VBox(4);
        HBox.setHgrow(logSection, javafx.scene.layout.Priority.ALWAYS);

        Text logHeader = new Text("GAME LOG");
        logHeader.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        logHeader.setFill(Color.web("#666688"));

        // the list of log entries
        logEntries = new VBox(3);
        logEntries.setPadding(new Insets(4));

        // scrollable container
        logScroll = new ScrollPane(logEntries);
        logScroll.setStyle(
            "-fx-background-color: #0a0a18;" +
            "-fx-background: #0a0a18;"
        );
        logScroll.setMinHeight(90);
        logScroll.setMaxHeight(110);
        logScroll.setFitToWidth(true);
        logScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        logScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        logSection.getChildren().addAll(logHeader, logScroll);

     // ── Deck section ───────────────────────────────
        VBox deckSection = new VBox(8);
        deckSection.setAlignment(Pos.CENTER);
        deckSection.setPadding(new Insets(0, 10, 0, 10));

        // deck visual stack
        deckVisual = buildDeckVisual();

        // deck count
        deckCountText = new Text("Cards: " + game.engine.Board.getCards().size());
        deckCountText.setFont(Font.font("Courier New", 11));
        deckCountText.setFill(Color.web("#aaaacc"));

        // draw card button
        drawCardBtn = new Button("🃏 DRAW CARD");
        drawCardBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        drawCardBtn.setStyle(
            "-fx-background-color: #1a3a1a;" +
            "-fx-text-fill: #88ffaa;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        drawCardBtn.setDisable(true); // disabled until landing on card cell
        drawCardBtn.setOnAction(e -> handleDrawCard());

        // reload button
        reloadDeckBtn = new Button("↺ RELOAD DECK");
        reloadDeckBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
        reloadDeckBtn.setStyle(
            "-fx-background-color: #3a1a1a;" +
            "-fx-text-fill: #ff8888;" +
            "-fx-padding: 6 12;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        reloadDeckBtn.setDisable(true); // only enabled when deck empty
        reloadDeckBtn.setOnAction(e -> handleReloadDeck());

        deckSection.getChildren().addAll(deckVisual, deckCountText, drawCardBtn, reloadDeckBtn);

        // separator
        Separator sep3 = new Separator();
        sep3.setOrientation(javafx.geometry.Orientation.VERTICAL);

        bar.getChildren().addAll(leftSide, sep1, middleSide, sep2, logSection, sep3, deckSection);
        return bar;
    }
    private GridPane buildBoard() {
    	boardGrid = new GridPane();;
    	boardGrid.setHgap(2);
    	boardGrid.setVgap(2);
    	boardGrid.setPadding(new Insets(5));

        for (int index = 0; index < 100; index++) {

            // ── Create the cell pane ───────────────────────
            StackPane cellPane = new StackPane();
            cellPane.setPrefSize(CELL_SIZE, CELL_SIZE);
            cellPane.setMinSize(CELL_SIZE, CELL_SIZE);
            cellPane.setMaxSize(CELL_SIZE, CELL_SIZE);

            // ── Create the cell background rectangle ───────
            Rectangle rect = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
            rect.setArcWidth(6);
            rect.setArcHeight(6);

            // ── Cell index number ──────────────────────────
            Text indexText = new Text(String.valueOf(index));
            indexText.setFont(Font.font("Courier New", 20));
            indexText.setFill(Color.web("#666688"));
            StackPane.setAlignment(indexText, Pos.TOP_LEFT);
            StackPane.setMargin(indexText, new Insets(2, 0, 0, 3));

            // ── Color the cell based on its type ───────────
            Cell[][] boardCells = controller.getGame().getBoard().getBoardCells();
            Cell cell = getCellAtIndex(boardCells, index);

            String label = "";
            Color color;

            if (cell instanceof MonsterCell) {
                color = Color.web("#3a1a5a");
                Monster cm = ((MonsterCell) cell).getCellMonster();
                if (cm != null) {
                    label = "👾\n"+
                            cm.getOriginalRole() + "\n" +
                            cm.getEnergy();
                } else {
                    label = "👾";
                }
            } else if (cell instanceof CardCell) {
                color = Color.web("#1a3a1a");
                label = "🃏\n"+"Card";
            } else if (cell instanceof ConveyorBelt) {
                color = Color.web("#1a2a4a");
                label = "🚀+"+"Belt\n" + ((ConveyorBelt) cell).getEffect();
            } else if (cell instanceof ContaminationSock) {
                color = Color.web("#4a1a1a");
                label = "▼\n" +"Sock\n"+ ((ContaminationSock) cell).getEffect();
            } else if (cell instanceof DoorCell) {
                DoorCell door = (DoorCell) cell;
                if (door.getRole() == Role.SCARER) {
                    color = Color.web("#5a3010");
                    label = "👻\n"+"Door\n" + door.getEnergy();
                } else {
                    color = Color.web("#103050");
                    label = "😂\n"+"Door\n" + door.getEnergy();
                }
            } else {
                // normal cell
                if (index == 0) {
                    color = Color.web("#104010");
                    label = "🏁";
                } else if (index == 99) {
                    color = Color.web("#403000");
                    label = "🏆";
                } else {
                    color = Color.web("#1a1a2e");
                }
            }

            rect.setFill(color);
            rect.setStroke(Color.web("#333355"));
            rect.setStrokeWidth(1);

            // ── Cell label ─────────────────────────────────
           Text labelText = new Text(label);
            labelText.setFont(Font.font("Courier New",cellPane.getPrefWidth() * 0.25 ));
            labelText.setFill(Color.web("#ccccee"));
            labelText.setTextAlignment(TextAlignment.CENTER);


            // ── Add everything to the cell pane ───────────
            cellPane.getChildren().addAll(rect, indexText, labelText);
            cellPanes[index] = cellPane;

            // ── Place cell in grid using zigzag ───────────
            int[] rc = indexToRowCol(index);
            boardGrid.add(cellPane, rc[1], rc[0]);
        }

        return boardGrid;
    }
    private void resizeBoard(double sceneWidth, double sceneHeight) {
        // panels are 15% each side
        double panelWidth = sceneWidth * 0.15;
        // bottom bar is 15% height, top bar is about 50px
        double bottomHeight = sceneHeight * 0.15;
        double topHeight = 50;

        double boardWidth  = sceneWidth  - (panelWidth * 2) - 40;
        double boardHeight = sceneHeight - bottomHeight - topHeight - 20;

        double newCellSize = Math.min(boardWidth / 10, boardHeight / 10);
        newCellSize = Math.max(newCellSize, 30);

        for (int i = 0; i < 100; i++) {
            StackPane cellPane = cellPanes[i];
            cellPane.setPrefSize(newCellSize, newCellSize);
            cellPane.setMinSize(newCellSize, newCellSize);
            cellPane.setMaxSize(newCellSize, newCellSize);
            if (cellPane.getChildren().size() > 2) {
                Text labelText = (Text) cellPane.getChildren().get(2);
                labelText.setFont(Font.font("Courier New", newCellSize * 0.25));
            }
            if (cellPane.getChildren().size() > 0) {
                if (cellPane.getChildren().get(0) instanceof Rectangle) {
                    Rectangle rect = (Rectangle) cellPane.getChildren().get(0);
                    rect.setWidth(newCellSize - 2);
                    rect.setHeight(newCellSize - 2);
                }
            }
        }

        boardGrid.setHgap(2);
        boardGrid.setVgap(2);
        updateTokenPositionsWithSize(newCellSize);
    }
    private void scalePanelFonts() {
        double width = Main.getMainScene().getWidth();

        // scale factor based on screen width
        double scale = width / 1280.0;
        double fontSize = Math.max(10, 12 * scale);

        // update player panel texts
        playerNameText.setFont(Font.font("Courier New", fontSize));
        playerTypeText.setFont(Font.font("Courier New", fontSize));
        playerRoleText.setFont(Font.font("Courier New", fontSize));
        playerEnergyText.setFont(Font.font("Courier New", fontSize));
        playerPositionText.setFont(Font.font("Courier New", fontSize));
        playerStatusText.setFont(Font.font("Courier New", fontSize));

        // update opponent panel texts
        opponentNameText.setFont(Font.font("Courier New", fontSize));
        opponentTypeText.setFont(Font.font("Courier New", fontSize));
        opponentRoleText.setFont(Font.font("Courier New", fontSize));
        opponentEnergyText.setFont(Font.font("Courier New", fontSize));
        opponentPositionText.setFont(Font.font("Courier New", fontSize));
        opponentStatusText.setFont(Font.font("Courier New", fontSize));
    }
    private void scaleButtons() {
        double width = Main.getMainScene().getWidth();
        double scale = width / 1280.0;
        double fontSize = Math.max(10, 14 * scale);
        double padding = Math.max(6, 10 * scale);

        String paddingStr = padding + " " + (padding * 2);

        rollBtn.setFont(Font.font("Courier New", FontWeight.BOLD, fontSize));
        rollBtn.setStyle(
            "-fx-background-color: #1a4a1a;" +
            "-fx-text-fill: #88ffaa;" +
            "-fx-padding: " + paddingStr + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        powerupBtn.setFont(Font.font("Courier New", FontWeight.BOLD, fontSize));
        powerupBtn.setStyle(
            "-fx-background-color: #3a1a6a;" +
            "-fx-text-fill: #dd88ff;" +
            "-fx-padding: " + paddingStr + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        diceText.setFont(Font.font("Courier New", FontWeight.BOLD,
            Math.max(18, 28 * scale)));
        turnText.setFont(Font.font("Courier New", FontWeight.BOLD,
            Math.max(10, 14 * scale)));
        eventText.setFont(Font.font("Courier New",
            Math.max(10, 13 * scale)));
    }
    private void updateTokenPositionsWithSize(double cellSize) {
        placeTokenWithSize(playerToken,   controller.getPlayer().getPosition(),   -6, cellSize);
        placeTokenWithSize(opponentToken, controller.getOpponent().getPosition(),  6, cellSize);
    }

    private void placeTokenWithSize(Circle token, int index, double xOffset, double cellSize) {
        int[] rc = indexToRowCol(index);
        double x = rc[1] * (cellSize + 2) + cellSize / 2.0 + 5 + xOffset;
        double y = rc[0] * (cellSize + 2) + cellSize / 2.0 + 5;
        token.setCenterX(x);
        token.setCenterY(y);
    }
    private void updateTokenPositions() {
        if (cellPanes[0] != null) {
            double cellSize = cellPanes[0].getPrefWidth();
            updateTokenPositionsWithSize(cellSize);
        }
    }
    private int[] indexToRowCol(int index) {
        int row = index / 10;
        int col;

        // even rows go left to right
        // odd rows go right to left
        if (row % 2 == 0) {
            col = index % 10;
        } else {
            col = 9 - (index % 10);
        }

        // flip vertically so row 0 is at the bottom
        int displayRow = 9 - row;

        return new int[]{displayRow, col};
    }
    private Cell getCellAtIndex(Cell[][] boardCells, int index) {
        int row = index / 10;
        int col;
        if (row % 2 == 0) {
            col = index % 10;
        } else {
            col = 9 - (index % 10);
        }
        return boardCells[row][col];
    }
    private Pane buildTokenLayer() {

        // ── Create player token (orange) ───────────────
        playerToken = new Circle(10);
        playerToken.setFill(Color.web("#ff6b35"));
        playerToken.setStroke(Color.WHITE);
        playerToken.setStrokeWidth(2);

        // glow effect
        DropShadow playerGlow = new DropShadow();
        playerGlow.setColor(Color.web("#ff6b35"));
        playerGlow.setRadius(10);
        playerToken.setEffect(playerGlow);

        // ── Create opponent token (purple) ─────────────
        opponentToken = new Circle(10);
        opponentToken.setFill(Color.web("#7b2fff"));
        opponentToken.setStroke(Color.WHITE);
        opponentToken.setStrokeWidth(2);

        // glow effect
        DropShadow opponentGlow = new DropShadow();
        opponentGlow.setColor(Color.web("#7b2fff"));
        opponentGlow.setRadius(10);
        opponentToken.setEffect(opponentGlow);

        // ── Create a Pane to hold both tokens ──────────
        Pane tokenLayer = new Pane();
        tokenLayer.setPickOnBounds(false);
        tokenLayer.getChildren().addAll(playerToken, opponentToken);

        // ── Place tokens at starting positions ─────────
        placeToken(playerToken, controller.getPlayer().getPosition(), -6);
        placeToken(opponentToken, controller.getOpponent().getPosition(), 6);

        return tokenLayer;
    }
    private void placeToken(Circle token, int index, double xOffset) {

        // get the row and col of this index
        int[] rc = indexToRowCol(index);

        // calculate pixel position
        // 2 is the gap between cells (setHgap and setVgap)
        // 5 is the grid padding
        double x = rc[1] * (CELL_SIZE + 2) + CELL_SIZE / 2.0 + 5 + xOffset;
        double y = rc[0] * (CELL_SIZE + 2) + CELL_SIZE / 2.0 + 5;

        token.setCenterX(x);
        token.setCenterY(y);
    }

    private void handleRoll() {
        if (!controller.isPlayerTurn()) {
            showDialog("Not Your Turn", "Wait for your turn!");
            return;
        }

        rollBtn.setDisable(true);
        powerupBtn.setDisable(true);
        drawCardBtn.setDisable(true);

        int playerEnergyBefore = controller.getPlayer().getEnergy();
        int opponentEnergyBefore = controller.getOpponent().getEnergy();
        boolean playerWasShielded = controller.getPlayer().isShielded();
        boolean opponentWasShielded = controller.getOpponent().isShielded();
        boolean wasFrozen = controller.getPlayer().isFrozen();
        int beforePos = controller.getPlayer().getPosition();
        Cell[][] boardCells = controller.getGame().getBoard().getBoardCells();
        boolean[] wasActivated = saveDoorStates(boardCells);

        diceText.setFill(Color.web("#ffdd44"));
        diceText.setText("⚀");

        try {
            controller.playTurn();
            int roll = controller.getLastRoll();
            int afterPos = controller.getPlayer().getPosition();

            animateDice(roll, () -> {

                if (playerWasShielded && !controller.getPlayer().isShielded()) {
                    if (controller.getPlayer().getEnergy() == playerEnergyBefore) {
                        showShieldBlockedPopup(controller.getPlayer(),
                            "Energy loss was blocked!");
                    }
                }
                if (opponentWasShielded && !controller.getOpponent().isShielded()) {
                    if (controller.getOpponent().getEnergy() == opponentEnergyBefore) {
                        showShieldBlockedPopup(controller.getOpponent(),
                            "Energy loss was blocked!");
                    }
                }

                int playerEnergyAfter  = controller.getPlayer().getEnergy();
                int opponentEnergyAfter = controller.getOpponent().getEnergy();
                int playerDelta  = playerEnergyAfter  - playerEnergyBefore;
                int opponentDelta = opponentEnergyAfter - opponentEnergyBefore;

                Cell[][] cellsCheck = controller.getGame().getBoard().getBoardCells();
                Cell landedCellCheck = getCellAtIndex(cellsCheck, afterPos);
                if (landedCellCheck instanceof MonsterCell) {
                    MonsterCell mc = (MonsterCell) landedCellCheck;
                    Monster stationed = mc.getCellMonster();
                    if (stationed != null) {
                        showPowerupEffectPopup(controller.getPlayer(),
                            stationed,
                            stationed.getRole() == controller.getPlayer().getRole());
                    }
                }

                Cell[][] cells = controller.getGame().getBoard().getBoardCells();
                Cell landedCell = getCellAtIndex(cells, afterPos);
                if (landedCell instanceof CardCell) {
                    cardCellLanded = true;
                    drawCardBtn.setDisable(false);
                    addLog("🃏 You landed on a Card Cell! Press DRAW CARD.", "#88ffaa");
                }

                if (wasFrozen) {
                    diceText.setFill(Color.web("#88ddff"));
                    eventText.setFill(Color.web("#88ddff"));
                    eventText.setText("🧊 " + controller.getPlayer().getName() +
                        " is FROZEN! Turn skipped.");
                    addLog("🧊 " + controller.getPlayer().getName() +
                        " was FROZEN — turn skipped!", "#88ddff");
                } else {
                    eventText.setFill(Color.web("#aaffaa"));
                    eventText.setText(controller.getPlayer().getName() +
                        " moved from " + beforePos + " to " + afterPos);
                    addLog(controller.getPlayer().getName() +
                        " moved from " + beforePos + " to " + afterPos +
                        " | Energy: " + playerEnergyAfter +
                        " (" + (playerDelta >= 0 ? "+" : "") + playerDelta + ")",
                        "#aaffaa");
                    addLog(controller.getOpponent().getName() +
                        " Energy: " + opponentEnergyAfter +
                        " (" + (opponentDelta >= 0 ? "+" : "") + opponentDelta + ")" +
                        "\n_______________________________",
                        "#ffaaaa");
                }

                updateAll();
                highlightCell(afterPos);
                flashEnergyChange(playerEnergyText,
                    controller.getPlayer().getEnergy() - playerEnergyBefore);
                flashEnergyChange(opponentEnergyText,
                    controller.getOpponent().getEnergy() - opponentEnergyBefore);

                Monster winner = controller.getWinner();
                if (winner != null) {
                    showWinScreen(winner);
                    return;
                }

                // Show the landing popup; opponent turn starts only after OK is clicked
                showCellLandingPopup(afterPos, boardCells, wasActivated, () -> {
                    if (!cardCellLanded) {
                        playOpponentTurn(rollBtn);
                    }
                    // If cardCellLanded, opponent turn starts inside handleDrawCard() callback
                });
            });

        } catch (game.engine.exceptions.InvalidMoveException ex) {
            eventText.setFill(Color.web("#ff6666"));
            eventText.setText("❌ " + ex.getMessage());
            addLog("❌ " + ex.getMessage(), "#ff6666");
            showDialog("❌ Invalid Move",
                ex.getMessage() +
                "\nYou cannot move to a cell occupied by your opponent." +
                "\nTry rolling again!");
            updateAll();
            rollBtn.setDisable(false);
            powerupBtn.setDisable(false);
        }
    }
    private void handlePowerup() {

        // ── Check it is player's turn ──────────────────
        if (!controller.isPlayerTurn()) {
            showDialog("You cannot play during the opponents turn", "Wait for your turn!");
            return;
        }

        try {
            controller.usePowerup();

            // ── Show success message ───────────────────
            eventText.setFill(Color.web("#ffdd44"));
            eventText.setText(
                "⚡ " + controller.getPlayer().getName() +
                " used their powerup!"
            );
            addLog("⚡ " + controller.getPlayer().getName() +
            	    " used their powerup!", "#ffdd44");
            updateAll();

        } catch (game.engine.exceptions.OutOfEnergyException ex) {
            showDialog(
                "Not Enough Energy",
                "You need " + Constants.POWERUP_COST +
                " energy to use your powerup!\n" +
                "Current energy: " + controller.getPlayer().getEnergy()
            );
        }
    }
    private void playOpponentTurn(Button rollBtn) {
        int playerEnergyBefore = controller.getPlayer().getEnergy();
        int opponentEnergyBefore = controller.getOpponent().getEnergy();
        boolean playerWasShielded = controller.getPlayer().isShielded();
        boolean opponentWasShielded = controller.getOpponent().isShielded();

        javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(2)
            );

        pause.setOnFinished(e -> {
            try {
                boolean wasFrozen = controller.getOpponent().isFrozen();
                int beforePos = controller.getOpponent().getPosition();
                Cell[][] opponentBoardCells = controller.getGame().getBoard().getBoardCells();
                boolean[] wasActivated = saveDoorStates(opponentBoardCells);

                diceText.setFill(Color.web("#7b2fff"));
                diceText.setText("⚀");

                controller.playTurn();
                int roll = controller.getLastRoll();
                int afterPos = controller.getOpponent().getPosition();

                animateDice(roll, () -> {

                    int playerEnergyAfter  = controller.getPlayer().getEnergy();
                    int opponentEnergyAfter = controller.getOpponent().getEnergy();
                    int playerDelta  = playerEnergyAfter  - playerEnergyBefore;
                    int opponentDelta = opponentEnergyAfter - opponentEnergyBefore;

                    if (playerWasShielded && !controller.getPlayer().isShielded()) {
                        if (controller.getPlayer().getEnergy() == playerEnergyBefore) {
                            showShieldBlockedPopup(controller.getPlayer(),
                                "Energy loss was blocked!");
                        }
                    }
                    if (opponentWasShielded && !controller.getOpponent().isShielded()) {
                        if (controller.getOpponent().getEnergy() == opponentEnergyBefore) {
                            showShieldBlockedPopup(controller.getOpponent(),
                                "Energy loss was blocked!");
                        }
                    }

                    Cell[][] cellsCheck = controller.getGame().getBoard().getBoardCells();
                    Cell landedCellCheck = getCellAtIndex(cellsCheck, afterPos);
                    if (landedCellCheck instanceof MonsterCell) {
                        MonsterCell mc = (MonsterCell) landedCellCheck;
                        Monster stationed = mc.getCellMonster();
                        if (stationed != null) {
                            showPowerupEffectPopup(controller.getOpponent(),
                                stationed,
                                stationed.getRole() == controller.getOpponent().getRole());
                        }
                    }

                    Cell[][] cells = controller.getGame().getBoard().getBoardCells();
                    Cell landedCell = getCellAtIndex(cells, afterPos);
                    if (landedCell instanceof CardCell) {
                        cardCellLanded = true;
                        addLog("🃏 Opponent landed on a Card Cell! Drawing card...", "#88ffaa");

                        javafx.animation.PauseTransition autoDraw =
                            new javafx.animation.PauseTransition(
                                javafx.util.Duration.seconds(1));
                        autoDraw.setOnFinished(ev -> {
                            handleDrawCardForOpponent(rollBtn);
                        });
                        autoDraw.play();
                        return;
                    }

                    if (wasFrozen) {
                        diceText.setFill(Color.web("#88ddff"));
                        eventText.setFill(Color.web("#88ddff"));
                        eventText.setText("🧊 " + controller.getOpponent().getName() +
                            " is FROZEN! Turn skipped.");
                        addLog("[Opponent] 🧊 " + controller.getOpponent().getName() +
                            " was FROZEN — turn skipped!", "#88ddff");
                    } else {
                        diceText.setFill(Color.web("#7b2fff"));
                        eventText.setFill(Color.web("#cc88ff"));
                        eventText.setText("[Opponent] " +
                            controller.getOpponent().getName() +
                            " moved from " + beforePos + " to " + afterPos);
                        addLog(controller.getOpponent().getName() +
                            " moved from " + beforePos + " to " + afterPos +
                            " | Energy: " + opponentEnergyAfter +
                            " (" + (opponentDelta >= 0 ? "+" : "") + opponentDelta + ")",
                            "#cc88ff");
                        addLog(controller.getPlayer().getName() +
                            " Energy: " + playerEnergyAfter +
                            " (" + (playerDelta >= 0 ? "+" : "") + playerDelta + ")" +
                            "\n________________________________",
                            "#ffaaaa");
                    }

                    updateAll();
                    highlightCell(afterPos);
                    flashEnergyChange(playerEnergyText,
                        controller.getPlayer().getEnergy() - playerEnergyBefore);
                    flashEnergyChange(opponentEnergyText,
                        controller.getOpponent().getEnergy() - opponentEnergyBefore);

                    Monster winner = controller.getWinner();
                    if (winner != null) {
                        showWinScreen(winner);
                        return;
                    }

                    // Show landing popup; give turn back to player only after OK is clicked
                    showCellLandingPopup(afterPos, opponentBoardCells, wasActivated, () -> {
                        rollBtn.setDisable(false);
                        powerupBtn.setDisable(false);
                        eventText.setFill(Color.web("#ffdd44"));
                        eventText.setText("Your turn! Roll the dice.");
                        addLog("↩ Your turn now!", "#ffdd44");
                    });
                });

            } catch (game.engine.exceptions.InvalidMoveException ex) {
                eventText.setFill(Color.web("#ff6666"));
                eventText.setText("[Opponent] ❌ " + ex.getMessage());
                addLog("[Opponent] ❌ " + ex.getMessage(), "#ff6666");
                showDialog("❌ Invalid Move",
                    ex.getMessage() +
                    "\nYou cannot move to a cell occupied by your opponent." +
                    "\nTry rolling again!");
                updateAll();

                if (controller.isPlayerTurn()) {
                    rollBtn.setDisable(false);
                    powerupBtn.setDisable(false);
                    eventText.setFill(Color.web("#ffdd44"));
                    eventText.setText("Your turn! Roll the dice.");
                    addLog("↩ Your turn now!", "#ffdd44");
                } else {
                    playOpponentTurn(rollBtn);
                }
            }
        });

        pause.play();
    }
    private void updateAll() {
        updateTokenPositions();
        updateTurnText();
        updatePanels();
        updateTeams();
        refreshBoardCells();
        scalePanelFonts(); 
        scaleButtons();
        
    }

    private void updateTurnText() {
        boolean isPlayer = controller.isPlayerTurn();
        turnText.setText(
            "TURN: " + (isPlayer ? "YOU" : "OPPONENT") +
            " (" + controller.getCurrent().getName() + ")"
        );
        turnText.setFill(
            isPlayer ? Color.web("#ff6b35") : Color.web("#7b2fff")
        );
        // ── Highlight active panel ─────────────────────
        if (isPlayer) {
            // player panel glows orange
            playerPanel.setStyle(
                "-fx-background-color: #1a0e28;" +
                "-fx-border-color: #ff6b35;" +
                "-fx-border-width: 0 4 0 0;"
            );
            // opponent panel normal
            opponentPanel.setStyle(
                "-fx-background-color: #0e0e28;" +
                "-fx-border-color: #7b2fff;" +
                "-fx-border-width: 0 0 0 2;"
            );
        }else {
            // opponent panel glows purple
            opponentPanel.setStyle(
                "-fx-background-color: #1a0e28;" +
                "-fx-border-color: #7b2fff;" +
                "-fx-border-width: 0 0 0 4;"
            );
            // player panel normal
            playerPanel.setStyle(
                "-fx-background-color: #0e0e28;" +
                "-fx-border-color: #ff6b35;" +
                "-fx-border-width: 0 2 0 0;"
            );
        }
        if (isPlayer) {
            pulsePanel(playerPanel, "#ff6b35");
        } else {
            pulsePanel(opponentPanel, "#7b2fff");
        }
    }
    private void showDialog(String title, String message) {
        Stage dialog = new Stage();
        dialog.setTitle(title);
        dialog.initOwner(stage);

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1a1a2e;");

        Text titleText = new Text(title);
        titleText.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        titleText.setFill(Color.web("#ffdd44"));

        Text msgText = new Text(message);
        msgText.setFont(Font.font("Courier New", 13));
        msgText.setFill(Color.web("#ccccee"));
        msgText.setWrappingWidth(260);
        msgText.setTextAlignment(TextAlignment.CENTER);

        Button okBtn = new Button("OK");
        okBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        okBtn.setStyle(
            "-fx-background-color: #7b2fff;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 24;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        okBtn.setOnAction(ev -> dialog.close());

        box.getChildren().addAll(titleText, msgText, okBtn);

        dialog.setScene(new Scene(box, 320, 180));
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    
    private Text makeLabel(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Courier New", FontWeight.BOLD, 10));
        t.setFill(Color.web("#666688"));
        return t;
    }

    private Text makeInfoText(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Courier New", 16));
        t.setFill(Color.web("#ccccee"));
        t.setWrappingWidth(160);
        return t;
    }
    private void updatePanels() {
        Monster player   = controller.getPlayer();
        Monster opponent = controller.getOpponent();

        // ── Update player panel ────────────────────────
        playerNameText.setText(player.getName());
        playerTypeText.setText(player.getClass().getSimpleName());
        playerRoleText.setText(player.getOriginalRole().toString());
        playerEnergyText.setText(player.getEnergy() + " / " + Constants.WINNING_ENERGY);
        playerPositionText.setText("Cell " + player.getPosition());
        playerStatusText.setText(buildStatusText(player));
        // ── Show current role ──────────────────────────
        if (player.isConfused()) {
            playerRoleText.setText(
                player.getOriginalRole() + " → 🌀 " + player.getRole()
            );
            playerRoleText.setFill(Color.web("#ff88ff"));
        } else {
            playerRoleText.setText(player.getOriginalRole().toString());
            playerRoleText.setFill(Color.web("#ccccee"));
        }
        // ── Update opponent panel ──────────────────────
        opponentNameText.setText(opponent.getName());
        opponentTypeText.setText(opponent.getClass().getSimpleName());
        opponentRoleText.setText(opponent.getOriginalRole().toString());
        opponentEnergyText.setText(opponent.getEnergy() + " / " + Constants.WINNING_ENERGY);
        opponentPositionText.setText("Cell " + opponent.getPosition());
        opponentStatusText.setText(buildStatusText(opponent));
        // ── Show current role ──────────────────────────
        if (opponent.isConfused()) {
            opponentRoleText.setText(
                opponent.getOriginalRole() + " → 🌀 " + opponent.getRole()
            );
            opponentRoleText.setFill(Color.web("#ff88ff"));
        } else {
            opponentRoleText.setText(opponent.getOriginalRole().toString());
            opponentRoleText.setFill(Color.web("#ccccee"));
        }
        // ── Color energy text based on value ──────────
        colorEnergyText(playerEnergyText,   player.getEnergy());
        colorEnergyText(opponentEnergyText, opponent.getEnergy());
    }
    private String buildStatusText(Monster m) {
        String status = "";

        if (m.isFrozen()) {
            status += "🧊 Frozen\n";
        }
        if (m.isShielded()) {
            status += "🛡 Shielded\n";
        }
        if (m.isConfused()) {
            status += "🌀 Confused (" + m.getConfusionTurns() + " turns)\n";
        }

        // ── Check Dasher momentum ──────────────────────
        if (m instanceof game.engine.monsters.Dasher) {
            game.engine.monsters.Dasher d = (game.engine.monsters.Dasher) m;
            if (d.getMomentumTurns() > 0) {
                status += "🚀 Momentum (" + d.getMomentumTurns() + " turns)\n";
            }
        }

        // ── Check MultiTasker focus ────────────────────
        if (m instanceof game.engine.monsters.MultiTasker) {
            game.engine.monsters.MultiTasker mt = (game.engine.monsters.MultiTasker) m;
            if (mt.getNormalSpeedTurns() > 0) {
                status += "🎯 Focus (" + mt.getNormalSpeedTurns() + " turns)\n";
            }
        }

        if (status.isEmpty()) {
            status = "None";
        }

        return status.trim();
    }
    private void colorEnergyText(Text energyText, int energy) {
        if (energy >= 700) {
            // high energy → green
            energyText.setFill(Color.web("#44ff88"));
        } else if (energy >= 300) {
            // medium energy → yellow
            energyText.setFill(Color.web("#ffdd44"));
        } else {
            // low energy → red
            energyText.setFill(Color.web("#ff4444"));
        }
    }
    private void showWinScreen(Monster winner) {

        // ── Disable buttons ────────────────────────────
        rollBtn.setDisable(true);
        powerupBtn.setDisable(true);

        boolean playerWon = winner == controller.getPlayer();

        Stage winStage = new Stage();
        winStage.setTitle("Game Over!");
        winStage.initOwner(stage);

        VBox box = new VBox(20);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #0a0a1a;");

        // ── Headline ───────────────────────────────────
        Text headline = new Text(playerWon ? "🏆 YOU WIN!" : "💀 GAME OVER");
        headline.setFont(Font.font("Georgia", FontWeight.BOLD, 42));
        headline.setFill(playerWon ? Color.web("#ffd700") : Color.web("#ff4444"));

        // ── Winner info ────────────────────────────────
        Text winnerInfo = new Text(
            winner.getName() + " (" + winner.getOriginalRole() + ") wins!"
        );
        winnerInfo.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        winnerInfo.setFill(Color.WHITE);

        // ── Final energies ─────────────────────────────
        Text energyInfo = new Text(
            "Final Energy:\n" +
            controller.getPlayer().getName() + ": " +
            controller.getPlayer().getEnergy() + "\n" +
            controller.getOpponent().getName() + ": " +
            controller.getOpponent().getEnergy()
        );
        energyInfo.setFont(Font.font("Courier New", 14));
        energyInfo.setFill(Color.web("#aaaacc"));
        energyInfo.setTextAlignment(TextAlignment.CENTER);

        // ── Return button ──────────────────────────────
        Button returnBtn = new Button("🏠 Return to Start");
        returnBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        returnBtn.setStyle(
            "-fx-background-color: #7b2fff;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 12 30;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;"
        );
        returnBtn.setOnAction(e -> {
            winStage.close();
            StartView startView = new StartView(stage, Main.getSceneRoot());
            startView.show();
        });

        box.getChildren().addAll(headline, winnerInfo, energyInfo, returnBtn);

        winStage.setScene(new Scene(box, 400, 320));
        winStage.setResizable(false);
        winStage.show();
    }
    private void addLog(String message, String colorHex) {

        // ── Create log entry ───────────────────────────
        Text entry = new Text("▸ " + message);
        entry.setFont(Font.font("Courier New", 20));
        entry.setFill(Color.web(colorHex));
        entry.setWrappingWidth(380);

        // ── Add to log ─────────────────────────────────
        logEntries.getChildren().add(entry);

        // ── Auto scroll to bottom ──────────────────────
        javafx.application.Platform.runLater(() -> {
            logScroll.setVvalue(1.0);
        });

        // ── Keep max 30 entries ────────────────────────
        if (logEntries.getChildren().size() > 30) {
            logEntries.getChildren().remove(0);
        }
    }
    private void refreshBoardCells() {
        Cell[][] boardCells = controller.getGame().getBoard().getBoardCells();

        for (int index = 0; index < 100; index++) {
            StackPane cellPane = cellPanes[index];
            Cell cell = getCellAtIndex(boardCells, index);

            // ── Get current cell size ──────────────────
            double cellSize = cellPane.getPrefWidth();

            // ── Remove old label (keep rect and index) ─
            cellPane.getChildren().removeIf(n ->
                n instanceof Text &&
                n != cellPane.getChildren().get(1)
            );

            // ── Get the rectangle ──────────────────────
            Rectangle rect = (Rectangle) cellPane.getChildren().get(0);

            // ── Update color and label ─────────────────
            String label = "";
            Color color;

            if (cell instanceof MonsterCell) {
                color = Color.web("#3a1a5a");
                Monster cm = ((MonsterCell) cell).getCellMonster();
                if (cm != null) {
                    label = "👾\n" +
                            cm.getOriginalRole() + "\n" +
                            cm.getEnergy();
                } else {
                    label = "👾";
                }
            } else if (cell instanceof CardCell) {
                color = Color.web("#1a3a1a");
                label = "🃏\n"+"Card";

            } else if (cell instanceof ConveyorBelt) {
                color = Color.web("#1a2a4a");
                label = "🚀+" +"Belt\n"+ ((ConveyorBelt) cell).getEffect();

            } else if (cell instanceof ContaminationSock) {
                color = Color.web("#4a1a1a");
                label = "▼\n" +"Sock\n"+ ((ContaminationSock) cell).getEffect();

            } else if (cell instanceof DoorCell) {
                DoorCell door = (DoorCell) cell;
                boolean activated = door.isActivated();

                if (door.getRole() == Role.SCARER) {
                    // activated door is dimmed
                    color = activated ?
                        Color.web("#2a1808") :
                        Color.web("#5a3010");
                    label = (activated ? "💀" : "👻") +
                        "\n" +"Door\n"+ door.getEnergy();
                } else {
                    color = activated ?
                        Color.web("#081018") :
                        Color.web("#103050");
                    label = (activated ? "💀" : "😂") +
                        "\n" + "Door\n"+door.getEnergy();
                }

            } else {
                if (index == 0) {
                    color = Color.web("#104010");
                    label = "🏁";
                } else if (index == 99) {
                    color = Color.web("#403000");
                    label = "🏆";
                } else {
                    color = Color.web("#1a1a2e");
                }
            }

            // ── Apply color ────────────────────────────
            rect.setFill(color);

            // ── Apply label ────────────────────────────
            if (!label.isEmpty()) {
                Text labelText = new Text(label);
                double labelFont = cellSize * 0.25;  // 25% of cell size
                labelFont = Math.max(8, labelFont);  // minimum 8
                labelText.setFont(Font.font("Courier New", labelFont));
                labelText.setFill(Color.web("#ccccee"));
                labelText.setTextAlignment(TextAlignment.CENTER);
                cellPane.getChildren().add(labelText);
            }
        }
    }
    private void showCellLandingPopup(int position, Cell[][] boardCells,
            boolean[] wasActivated, Runnable onClose) {
        Cell cell = getCellAtIndex(boardCells, position);

        if (!(cell instanceof DoorCell) && !(cell instanceof CardCell)) {
            // No popup needed — fire callback immediately
            if (onClose != null) onClose.run();
            return;
        }

        Stage popup = new Stage();
        popup.initOwner(stage);

        VBox box = new VBox(12);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #0e0e28;");

        Text icon = new Text();
        Text title = new Text();
        Text info = new Text();

        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            boolean alreadyActivated = wasActivated[position];

            if (alreadyActivated) {
                icon.setText("💀");
                title.setText("DOOR — EXHAUSTED");
                title.setFill(Color.web("#888888"));
                info.setText(
                    "Role: " + door.getRole() + "\n" +
                    "Energy: " + door.getEnergy() + "\n" +
                    "Status: Already activated\n" +
                    "Effect: None"
                );
                info.setFill(Color.web("#888888"));
                popup.setTitle("Exhausted Door");
            } else {
                boolean sameRole = controller.getCurrent().getRole() == door.getRole();
                if (door.getRole() == Role.SCARER) {
                    icon.setText("👻");
                    popup.setTitle("SCARER Door");
                } else {
                    icon.setText("😂");
                    popup.setTitle("LAUGHER Door");
                }
                title.setText(door.getRole() + " DOOR");
                title.setFill(door.getRole() == Role.SCARER
                    ? Color.web("#ff6b35") : Color.web("#44aaff"));
                info.setText(
                    "Role: " + door.getRole() + "\n" +
                    "Energy Value: " + door.getEnergy() + "\n" +
                    "Your Role: " + controller.getCurrent().getRole() + "\n" +
                    "Effect: " + (sameRole ?
                        "✅ +" + door.getEnergy() + " energy gained!" :
                        "❌ -" + door.getEnergy() + " energy lost!")
                );
                info.setFill(sameRole
                    ? Color.web("#aaffaa") : Color.web("#ff6666"));
            }
        } else if (cell instanceof CardCell) {
            icon.setText("🃏");
            popup.setTitle("Card Cell");
            title.setText("CARD CELL");
            title.setFill(Color.web("#88ffaa"));
            info.setText("You landed on a Card Cell!\nA card will be drawn\nfrom the deck.");
            info.setFill(Color.web("#88ffaa"));
        }

        icon.setFont(Font.font(40));
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        info.setFont(Font.font("Courier New", 13));
        info.setTextAlignment(TextAlignment.CENTER);
        info.setWrappingWidth(240);

        final boolean[] called = {false};

        Button okBtn = new Button("OK");
        okBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        okBtn.setStyle(
            "-fx-background-color: #7b2fff;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 30;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        okBtn.setOnAction(e -> {
            popup.close();
            if (!called[0]) {
                called[0] = true;
                if (onClose != null) onClose.run();
            }
        });

        Rectangle border = new Rectangle(260, 2);
        border.setFill(Color.web("#7b2fff"));
        box.getChildren().addAll(icon, title, border, info, okBtn);
        popup.setScene(new Scene(box, 300, 280));
        popup.setResizable(false);
        popup.show();
    }
    private boolean[] saveDoorStates(Cell[][] boardCells) {
        boolean[] states = new boolean[100];
        for (int i = 0; i < 100; i++) {
            Cell cell = getCellAtIndex(boardCells, i);
            if (cell instanceof DoorCell) {
                states[i] = ((DoorCell) cell).isActivated();
            }
        }
        return states;
    }
    private StackPane buildDeckVisual() {
        StackPane deck = new StackPane();
        deck.setPrefSize(60, 80);

        int cardCount = game.engine.Board.getCards().size();
        int layers = Math.min(cardCount, 5);

        for (int i = 0; i < layers; i++) {
            Rectangle card = new Rectangle(60, 80);
            card.setArcWidth(8);
            card.setArcHeight(8);
            card.setFill(Color.web("#1a3a5a"));
            card.setStroke(Color.web("#7b2fff"));
            card.setStrokeWidth(1.5);
            card.setTranslateX(i * -1.5);
            card.setTranslateY(i * -1.5);
            deck.getChildren().add(card);
        }

        if (cardCount == 0) {
            Text emptyText = new Text("EMPTY");
            emptyText.setFont(Font.font("Courier New", FontWeight.BOLD, 10));
            emptyText.setFill(Color.web("#ff4444"));
            deck.getChildren().add(emptyText);
        }

        return deck;
    }
    private void handleDrawCard() {
        if (!cardCellLanded) return;

        game.engine.cards.Card card = game.engine.Board.drawCard();
        card.performAction(controller.getPlayer(), controller.getOpponent());

        addLog("🃏 Card drawn: " + card.getName() +
            " — " + card.getDescription(), "#88ffaa");

        refreshDeckVisual();

        cardCellLanded = false;
        drawCardBtn.setDisable(true);

        updateAll();

        if (game.engine.Board.getCards().isEmpty()) {
            reloadDeckBtn.setDisable(false);
            addLog("🃏 Deck is empty! Reload to continue.", "#ff8888");
        }

        // Show card popup; opponent turn starts only after popup is closed
        showCardPopup(card.getName(), card.getDescription(),
            card.getClass().getSimpleName(), () -> {
                Monster winner = controller.getWinner();
                if (winner != null) {
                    showWinScreen(winner);
                    return;
                }
                playOpponentTurn(rollBtn);
            });
    }
    private void handleReloadDeck() {
        game.engine.Board.reloadCards();
        refreshDeckVisual();
        reloadDeckBtn.setDisable(true);
        addLog("↺ Deck reloaded and shuffled!", "#aaaacc");
    }
    private void refreshDeckVisual() {
        // rebuild the deck stack visual
        StackPane newDeck = buildDeckVisual();
        deckVisual.getChildren().setAll(newDeck.getChildren());

        // update count text
        int count = game.engine.Board.getCards().size();
        deckCountText.setText("Cards: " + count);
    }
    private void showCardPopup(String cardName, String cardDescription,
            String cardType, Runnable onClose) {

        Stage cardStage = new Stage();
        cardStage.initOwner(stage);
        cardStage.setTitle("Card Drawn!");

        AnchorPane canvas = new AnchorPane();
        canvas.setStyle("-fx-background-color: #1a1a24;");

        StackPane cardNode = new StackPane();
        cardNode.setPrefSize(200, 280);

        Rectangle cardBody = new Rectangle(200, 280);
        cardBody.setArcWidth(18);
        cardBody.setArcHeight(18);
        cardBody.setStroke(Color.WHITE);
        cardBody.setStrokeWidth(2.5);
        cardBody.setFill(getCardColor(cardType));

        VBox cardContent = new VBox(12);
        cardContent.setAlignment(Pos.TOP_CENTER);
        cardContent.setPadding(new Insets(20, 12, 12, 12));

        Text typeText = new Text(cardType.toUpperCase());
        typeText.setFont(Font.font("Courier New", FontWeight.BLACK, 12));
        typeText.setFill(Color.web("#111111"));

        Rectangle divider = new Rectangle(160, 2);
        divider.setFill(Color.web("#ffffff44"));

        Text nameText = new Text(cardName);
        nameText.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        nameText.setFill(Color.WHITE);
        nameText.setWrappingWidth(170);
        nameText.setTextAlignment(TextAlignment.CENTER);

        Text descText = new Text(cardDescription);
        descText.setFont(Font.font("Courier New", 12));
        descText.setFill(Color.web("#f1f1f1"));
        descText.setWrappingWidth(170);
        descText.setTextAlignment(TextAlignment.CENTER);

        Text iconText = new Text(getCardIcon(cardType));
        iconText.setFont(Font.font(36));

        cardContent.getChildren().addAll(typeText, divider, iconText, nameText, descText);
        cardNode.getChildren().addAll(cardBody, cardContent);

        cardNode.setLayoutX(75);
        cardNode.setLayoutY(-280);
        canvas.getChildren().add(cardNode);

        // Guard so callback only fires once (OK or auto-close, whichever comes first)
        final boolean[] called = {false};

        Button okBtn = new Button("OK");
        okBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        okBtn.setStyle(
            "-fx-background-color: #7b2fff;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        okBtn.setLayoutX(140);
        okBtn.setLayoutY(320);
        okBtn.setOnAction(e -> {
            cardStage.close();
            if (!called[0]) {
                called[0] = true;
                if (onClose != null) onClose.run();
            }
        });
        canvas.getChildren().add(okBtn);

        Text countdown = new Text("Closing in 5...");
        countdown.setFont(Font.font("Courier New", 11));
        countdown.setFill(Color.web("#666688"));
        countdown.setLayoutX(140);
        countdown.setLayoutY(370);
        canvas.getChildren().add(countdown);

        Scene scene = new Scene(canvas, 350, 400);
        scene.setFill(Color.web("#1a1a24"));
        cardStage.setScene(scene);
        cardStage.setResizable(false);
        cardStage.show();

        javafx.animation.TranslateTransition slideIn =
            new javafx.animation.TranslateTransition(
                javafx.util.Duration.seconds(0.4), cardNode);
        slideIn.setToY(280);
        slideIn.play();

        javafx.animation.PauseTransition autoClose =
            new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(5));
        autoClose.setOnFinished(e -> {
            cardStage.close();
            if (!called[0]) {
                called[0] = true;
                if (onClose != null) onClose.run();
            }
        });
        autoClose.play();

        javafx.animation.Timeline countdownAnim =
            new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(1),
                    e -> countdown.setText("Closing in 4...")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(2),
                    e -> countdown.setText("Closing in 3...")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(3),
                    e -> countdown.setText("Closing in 2...")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(4),
                    e -> countdown.setText("Closing in 1..."))
            );
        countdownAnim.play();
    }
    private Color getCardColor(String cardType) {
        if (cardType == null) return Color.web("#7f8c8d");
        switch (cardType.toUpperCase()) {
            case "SWAPPERCARD":   return Color.web("#9b59b6");
            case "SHIELDCARD":    return Color.web("#2980b9");
            case "ENERGYSTEALCARD": return Color.web("#2ecc71");
            case "STARTOVERCARD": return Color.web("#e67e22");
            case "CONFUSIONCARD": return Color.web("#c0392b");
            default:              return Color.web("#7f8c8d");
        }
    }

    private String getCardIcon(String cardType) {
        if (cardType == null) return "🃏";
        switch (cardType.toUpperCase()) {
            case "SWAPPERCARD":     return "↔";
            case "SHIELDCARD":      return "★";
            case "ENERGYSTEALCARD": return "⚡";
            case "STARTOVERCARD":   return "↩";
            case "CONFUSIONCARD":   return "?";
            default:                return "🃏";
        }
    }
    private void handleDrawCardForOpponent(Button rollBtn) {
        game.engine.cards.Card card = game.engine.Board.drawCard();
        card.performAction(controller.getOpponent(), controller.getPlayer());

        addLog("[Opponent] 🃏 Drew: " + card.getName() +
            " — " + card.getDescription(), "#cc88ff");

        refreshDeckVisual();

        cardCellLanded = false;
        drawCardBtn.setDisable(true);

        updateAll();

        if (game.engine.Board.getCards().isEmpty()) {
            reloadDeckBtn.setDisable(false);
            addLog("🃏 Deck is empty! Reload to continue.", "#ff8888");
        }

        // Show card popup; give turn back to player only after popup is closed
        showCardPopup(card.getName(), card.getDescription(),
            card.getClass().getSimpleName(), () -> {
                Monster winner = controller.getWinner();
                if (winner != null) {
                    showWinScreen(winner);
                    return;
                }
                rollBtn.setDisable(false);
                powerupBtn.setDisable(false);
                eventText.setFill(Color.web("#ffdd44"));
                eventText.setText("Your turn! Roll the dice.");
                addLog("↩ Your turn now!", "#ffdd44");
            });
    }
    private VBox buildTeamSection(Role role) {
        VBox teamBox = new VBox(4);
        teamBox.setStyle(
            "-fx-background-color: rgba(255,255,255,0.03);" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: rgba(123,47,255,0.3);" +
            "-fx-border-radius: 6;" +
            "-fx-border-width: 1;"
        );
        teamBox.setPadding(new Insets(10));
        teamBox.setMinHeight(200); 
        Text header = new Text(role == Role.SCARER ? "👻 SCARER TEAM" : "😂 LAUGHER TEAM");
        header.setFont(Font.font("Courier New", FontWeight.BOLD, 32));
        header.setFill(role == Role.SCARER ? Color.web("#ff6b35") : Color.web("#44aaff"));
        teamBox.getChildren().add(header);

        // ── Get stationed monsters of this role ────────
        for (Monster m : game.engine.Board.getStationedMonsters()) {
            if (m.getOriginalRole() == role) {
                VBox monsterBox = new VBox(2);
                monsterBox.setPadding(new Insets(3, 0, 3, 5));
                monsterBox.setStyle(
                    "-fx-border-color: rgba(255,255,255,0.05);" +
                    "-fx-border-width: 0 0 1 0;"
                );

                Text nameText = new Text(m.getName());
                nameText.setFont(Font.font("Courier New", FontWeight.BOLD, 25));
                nameText.setFill(Color.web("#ddddff"));

                Text typeText = new Text("Type: " + m.getClass().getSimpleName());
                typeText.setFont(Font.font("Courier New", 25));
                typeText.setFill(Color.web("#888899"));

                Text posText = new Text("Pos: " + m.getPosition());
                posText.setFont(Font.font("Courier New", 25));
                posText.setFill(Color.web("#888899"));

                Text energyText = new Text("Energy: " + m.getEnergy());
                energyText.setFont(Font.font("Courier New", 25));
                energyText.setFill(Color.web("#44ff88"));

                monsterBox.getChildren().addAll(nameText, typeText, posText, energyText);
                teamBox.getChildren().add(monsterBox);
            }
        }

        return teamBox;
    }
    private void updateTeams() {
        // ── Rebuild team sections with updated data ────
        Role playerRole = controller.getPlayer().getOriginalRole();
        Role opponentRole = controller.getOpponent().getOriginalRole();

        VBox newPlayerTeam = buildTeamSection(playerRole);
        VBox newOpponentTeam = buildTeamSection(opponentRole);

        // ── Replace old team box with new one ──────────
        int playerIdx = playerPanel.getChildren().indexOf(playerTeamBox);
        if (playerIdx >= 0) {
            playerPanel.getChildren().set(playerIdx, newPlayerTeam);
            playerTeamBox = newPlayerTeam;
        }

        int opponentIdx = opponentPanel.getChildren().indexOf(opponentTeamBox);
        if (opponentIdx >= 0) {
            opponentPanel.getChildren().set(opponentIdx, newOpponentTeam);
            opponentTeamBox = newOpponentTeam;
        }
    }
    private void showPowerupEffectPopup(Monster lander,
        Monster stationed,
        boolean sameRole) {
		Stage popup = new Stage();
		popup.initOwner(stage);
		
		VBox box = new VBox(12);
		box.setPadding(new Insets(25));
		box.setAlignment(Pos.CENTER);
		box.setStyle("-fx-background-color: #0e0e28;");
		
		// ── Icon ───────────────────────────────────────
		Text icon = new Text(sameRole ? "⚡" : "⚔");
		icon.setFont(Font.font(40));
		
		// ── Title ──────────────────────────────────────
		Text title = new Text(sameRole ? "POWERUP ACTIVATED!" : "ENERGY SWAP!");
		title.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
		title.setFill(sameRole ? Color.web("#ffdd44") : Color.web("#ff4444"));
		popup.setTitle(sameRole ? "Powerup Effect!" : "Energy Swap!");
		
		// ── Divider ────────────────────────────────────
		Rectangle border = new Rectangle(260, 2);
		border.setFill(Color.web("#7b2fff"));
		
		// ── Info ───────────────────────────────────────
		Text info = new Text();
		info.setFont(Font.font("Courier New", 13));
		info.setTextAlignment(TextAlignment.CENTER);
		info.setWrappingWidth(240);
		
		if (sameRole) {
		info.setText(
		lander.getName() + " landed on\n" +
		stationed.getName() + "'s cell!\n\n" +
		"Same role: " + stationed.getRole() + "\n\n" +
		"⚡ FREE POWERUP triggered!\n" +
		lander.getName() + " activates\n" +
		lander.getClass().getSimpleName() + " ability!"
		);
		info.setFill(Color.web("#ffdd44"));
		
		addLog("⚡ " + lander.getName() +
		" triggered FREE POWERUP on " +
		stationed.getName() + "'s cell!", "#ffdd44");
		} else {
		info.setText(
		lander.getName() + " landed on\n" +
		stationed.getName() + "'s cell!\n\n" +
		"Different roles!\n\n" +
		"⚔ Energy compared:\n" +
		lander.getName() + ": " + lander.getEnergy() + "\n" +
		stationed.getName() + ": " + stationed.getEnergy() + "\n\n" +
		(lander.getEnergy() > stationed.getEnergy() ?
		lander.getName() + " wins the swap!" :
		stationed.getName() + " keeps their energy!")
		);
		info.setFill(Color.web("#ff8888"));
		
		addLog("⚔ " + lander.getName() +
		" vs " + stationed.getName() +
		" energy swap!", "#ff8888");
		}
		
		// ── OK button ──────────────────────────────────
		Button okBtn = new Button("OK");
		okBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
		okBtn.setStyle(
		"-fx-background-color: #7b2fff;" +
		"-fx-text-fill: white;" +
		"-fx-padding: 8 30;" +
		"-fx-background-radius: 8;" +
		"-fx-cursor: hand;"
		);
		okBtn.setOnAction(e -> popup.close());
		box.getChildren().addAll(icon, title, border, info, okBtn);
		popup.setScene(new Scene(box, 300, 320));
		popup.setResizable(false);
		popup.show();
/*		// ── Countdown ──────────────────────────────────
		Text countdownText = new Text("Closing in 5...");
		countdownText.setFont(Font.font("Courier New", 11));
		countdownText.setFill(Color.web("#666688"));
		
		box.getChildren().addAll(icon, title, border, info, okBtn, countdownText);
		
		javafx.animation.PauseTransition autoClose =
		new javafx.animation.PauseTransition(
		javafx.util.Duration.seconds(5));
		autoClose.setOnFinished(ev -> popup.close());
		autoClose.play();
		
		popup.setScene(new Scene(box, 300, 320));
		popup.setResizable(false);
		popup.show();*/
		
/*		// ── Countdown animation ────────────────────────
		javafx.animation.Timeline countdown =
		new javafx.animation.Timeline(
		new javafx.animation.KeyFrame(
		javafx.util.Duration.seconds(1),
		e -> countdownText.setText("Closing in 4...")),
		new javafx.animation.KeyFrame(
		javafx.util.Duration.seconds(2),
		e -> countdownText.setText("Closing in 3...")),
		new javafx.animation.KeyFrame(
		javafx.util.Duration.seconds(3),
		e -> countdownText.setText("Closing in 2...")),
		new javafx.animation.KeyFrame(
		javafx.util.Duration.seconds(4),
		e -> countdownText.setText("Closing in 1..."))
		);
		countdown.play();*/
		}
    private void showShieldBlockedPopup(Monster monster, String actionBlocked) {
        Stage popup = new Stage();
        popup.initOwner(stage);
        popup.setTitle("Shield Activated!");

        VBox box = new VBox(12);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #0e0e28;");

        // ── Icon ───────────────────────────────────────
        Text icon = new Text("🛡");
        icon.setFont(Font.font(40));

        // ── Title ──────────────────────────────────────
        Text title = new Text("SHIELD ACTIVATED!");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        title.setFill(Color.web("#44aaff"));

        // ── Divider ────────────────────────────────────
        Rectangle border = new Rectangle(260, 2);
        border.setFill(Color.web("#44aaff"));

        // ── Info ───────────────────────────────────────
        Text info = new Text(
            monster.getName() + "'s shield\n" +
            "blocked the energy loss!\n\n" +
            "Action blocked:\n" +
            actionBlocked + "\n\n" +
            "🛡 Shield consumed!\n" +
            monster.getName() + " is no longer shielded."
        );
        info.setFont(Font.font("Courier New", 13));
        info.setFill(Color.web("#88ccff"));
        info.setTextAlignment(TextAlignment.CENTER);
        info.setWrappingWidth(240);

        // ── OK button ──────────────────────────────────
        Button okBtn = new Button("OK");
        okBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        okBtn.setStyle(
            "-fx-background-color: #2980b9;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 30;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        okBtn.setOnAction(e -> popup.close());

        // ── Countdown ──────────────────────────────────
        Text countdownText = new Text("Closing in 5...");
        countdownText.setFont(Font.font("Courier New", 11));
        countdownText.setFill(Color.web("#666688"));

        box.getChildren().addAll(icon, title, border, info, okBtn, countdownText);

        javafx.animation.PauseTransition autoClose =
            new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(5));
        autoClose.setOnFinished(ev -> popup.close());
        autoClose.play();

        popup.setScene(new Scene(box, 300, 320));
        popup.setResizable(false);
        popup.show();

        javafx.animation.Timeline countdown =
            new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(1),
                    e -> countdownText.setText("Closing in 4...")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(2),
                    e -> countdownText.setText("Closing in 3...")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(3),
                    e -> countdownText.setText("Closing in 2...")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(4),
                    e -> countdownText.setText("Closing in 1..."))
            );
        countdown.play();

        addLog("🛡 " + monster.getName() +
            " shield blocked: " + actionBlocked, "#44aaff");
    }
    private void animateDice(int finalValue, Runnable afterAnimation) {
    	javafx.animation.Timeline shake =
    		    new javafx.animation.Timeline(
    		        new javafx.animation.KeyFrame(
    		            javafx.util.Duration.millis(0),
    		            new javafx.animation.KeyValue(
    		                diceText.translateXProperty(), 0)),
    		        new javafx.animation.KeyFrame(
    		            javafx.util.Duration.millis(50),
    		            new javafx.animation.KeyValue(
    		                diceText.translateXProperty(), -8)),
    		        new javafx.animation.KeyFrame(
    		            javafx.util.Duration.millis(100),
    		            new javafx.animation.KeyValue(
    		                diceText.translateXProperty(), 8)),
    		        new javafx.animation.KeyFrame(
    		            javafx.util.Duration.millis(150),
    		            new javafx.animation.KeyValue(
    		                diceText.translateXProperty(), -8)),
    		        new javafx.animation.KeyFrame(
    		            javafx.util.Duration.millis(200),
    		            new javafx.animation.KeyValue(
    		                diceText.translateXProperty(), 8)),
    		        new javafx.animation.KeyFrame(
    		            javafx.util.Duration.millis(250),
    		            new javafx.animation.KeyValue(
    		                diceText.translateXProperty(), 0))
    		    );
    		shake.setCycleCount(4);
    		shake.play();
        // ── Dice faces using unicode ───────────────────
        String[] faces = {"⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};

        // ── Create timeline that flips faces fast ──────
        javafx.animation.Timeline diceAnim =
            new javafx.animation.Timeline();

        // ── Add 12 random frames ───────────────────────
        for (int i = 0; i < 12; i++) {
            final int frame = i;
            javafx.animation.KeyFrame kf =
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(i * 80),
                    e -> {
                        // show random face
                        int randomFace = (int)(Math.random() * 6);
                        diceText.setText(faces[randomFace]);

                        // scale effect — grows and shrinks
                        double scale = 1.0 + (frame % 2 == 0 ? 0.2 : 0.0);
                        diceText.setScaleX(scale);
                        diceText.setScaleY(scale);
                    }
                );
            diceAnim.getKeyFrames().add(kf);
        }

        // ── Final frame shows the actual result ────────
        javafx.animation.KeyFrame finalFrame =
            new javafx.animation.KeyFrame(
                javafx.util.Duration.millis(12 * 80 + 100),
                e -> {
                    // show final value
                    diceText.setText(faces[finalValue - 1]);
                    diceText.setFill(Color.web("#ffdd44"));
                    diceText.setScaleX(1.3);
                    diceText.setScaleY(1.3);

                    // bounce back to normal size
                    javafx.animation.Timeline bounce =
                        new javafx.animation.Timeline(
                            new javafx.animation.KeyFrame(
                                javafx.util.Duration.millis(150),
                                e2 -> {
                                    diceText.setScaleX(1.0);
                                    diceText.setScaleY(1.0);
                                }
                            )
                        );
                    bounce.play();

                    // ── Run the actual game logic after ────
                    afterAnimation.run();
                }
            );
        diceAnim.getKeyFrames().add(finalFrame);
        diceAnim.play();
    }
    private void animateTokenMove(Circle token, int fromIndex, 
        int toIndex, double xOffset,
        Runnable afterAnimation) {
		// ── Get start position ─────────────────────────
		int[] fromRc = indexToRowCol(fromIndex);
		double cellSize = cellPanes[0].getPrefWidth();
		double startX = fromRc[1] * (cellSize + 2) + cellSize / 2.0 + 5 + xOffset;
		double startY = fromRc[0] * (cellSize + 2) + cellSize / 2.0 + 5;
		
		// ── Get end position ───────────────────────────
		int[] toRc = indexToRowCol(toIndex);
		double endX = toRc[1] * (cellSize + 2) + cellSize / 2.0 + 5 + xOffset;
		double endY = toRc[0] * (cellSize + 2) + cellSize / 2.0 + 5;
		
		// ── Animate move ───────────────────────────────
		javafx.animation.Timeline moveAnim = new javafx.animation.Timeline(
		new javafx.animation.KeyFrame(
		javafx.util.Duration.ZERO,
		new javafx.animation.KeyValue(token.centerXProperty(), startX),
		new javafx.animation.KeyValue(token.centerYProperty(), startY)
		),
		new javafx.animation.KeyFrame(
		javafx.util.Duration.millis(500),
		new javafx.animation.KeyValue(
		token.centerXProperty(), endX,
		javafx.animation.Interpolator.EASE_BOTH),
		new javafx.animation.KeyValue(
		token.centerYProperty(), endY,
		javafx.animation.Interpolator.EASE_BOTH)
		)
		);
		
		// ── Bounce effect at end ───────────────────────
		moveAnim.setOnFinished(e -> {
		javafx.animation.Timeline bounce =
		new javafx.animation.Timeline(
		new javafx.animation.KeyFrame(
		 javafx.util.Duration.millis(100),
		 new javafx.animation.KeyValue(
		     token.scaleXProperty(), 1.4),
		 new javafx.animation.KeyValue(
		     token.scaleYProperty(), 1.4)
		),
		new javafx.animation.KeyFrame(
		 javafx.util.Duration.millis(200),
		 new javafx.animation.KeyValue(
		     token.scaleXProperty(), 1.0),
		 new javafx.animation.KeyValue(
		     token.scaleYProperty(), 1.0)
		)
		);
		bounce.setOnFinished(ev -> {
		if (afterAnimation != null) afterAnimation.run();
		});
		bounce.play();
		});
		
		moveAnim.play();
		}
    private void flashEnergyChange(Text energyText, int delta) {
        // ── Choose color based on gain or loss ─────────
        String flashColor = delta >= 0 ? "#44ff88" : "#ff4444";
        String symbol = delta >= 0 ? "▲" : "▼";

        // ── Flash the energy text ──────────────────────
        javafx.animation.Timeline flash =
            new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(0),
                    e -> energyText.setStyle(
                        "-fx-effect: dropshadow(gaussian, " +
                        flashColor + ", 20, 0.8, 0, 0);")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(200),
                    e -> energyText.setStyle("")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(400),
                    e -> energyText.setStyle(
                        "-fx-effect: dropshadow(gaussian, " +
                        flashColor + ", 20, 0.8, 0, 0);")),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(600),
                    e -> energyText.setStyle(""))
            );
        flash.play();

        // ── Show floating delta text ───────────────────
        Text deltaText = new Text(symbol + " " +
            Math.abs(delta));
        deltaText.setFont(Font.font("Courier New",
            FontWeight.BOLD, 16));
        deltaText.setFill(Color.web(flashColor));
        deltaText.setOpacity(1.0);

        // ── Position near energy text ──────────────────
        double x = energyText.getLayoutX() + 60;
        double y = energyText.getLayoutY();
        deltaText.setTranslateX(x);
        deltaText.setTranslateY(y);

        // ── Add to scene root temporarily ─────────────
        Main.getSceneRoot().getChildren().add(deltaText);

        // ── Float upward and fade out ──────────────────
        javafx.animation.Timeline floatUp =
            new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(0),
                    new javafx.animation.KeyValue(
                        deltaText.translateYProperty(), y),
                    new javafx.animation.KeyValue(
                        deltaText.opacityProperty(), 1.0)
                ),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(800),
                    new javafx.animation.KeyValue(
                        deltaText.translateYProperty(), y - 40),
                    new javafx.animation.KeyValue(
                        deltaText.opacityProperty(), 0.0)
                )
            );
        floatUp.setOnFinished(e ->
            Main.getSceneRoot().getChildren().remove(deltaText));
        floatUp.play();
    }
    private void highlightCell(int index) {
        StackPane cellPane = cellPanes[index];
        Rectangle rect = (Rectangle) cellPane.getChildren().get(0);

        Color original = (Color) rect.getFill();

        javafx.animation.Timeline highlight =
            new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(0),
                    new javafx.animation.KeyValue(
                        rect.strokeWidthProperty(), 1.0),
                    new javafx.animation.KeyValue(
                        rect.strokeProperty(), Color.WHITE)
                ),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(200),
                    new javafx.animation.KeyValue(
                        rect.strokeWidthProperty(), 4.0),
                    new javafx.animation.KeyValue(
                        rect.strokeProperty(), Color.web("#ffdd44"))
                ),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(400),
                    new javafx.animation.KeyValue(
                        rect.strokeWidthProperty(), 4.0),
                    new javafx.animation.KeyValue(
                        rect.strokeProperty(), Color.web("#ffdd44"))
                ),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(600),
                    new javafx.animation.KeyValue(
                        rect.strokeWidthProperty(), 1.0),
                    new javafx.animation.KeyValue(
                        rect.strokeProperty(), Color.web("#333355"))
                )
            );
        highlight.play();
    }
    private void pulsePanel(VBox panel, String color) {
        javafx.animation.Timeline pulse =
            new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(0),
                    e -> panel.setStyle(
                        panel.getStyle() +
                        "-fx-effect: dropshadow(gaussian, " +
                        color + ", 0, 0, 0, 0);"
                    )
                ),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(300),
                    e -> panel.setStyle(
                        panel.getStyle() +
                        "-fx-effect: dropshadow(gaussian, " +
                        color + ", 30, 0.8, 0, 0);"
                    )
                ),
                new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(600),
                    e -> panel.setStyle(
                        panel.getStyle() +
                        "-fx-effect: dropshadow(gaussian, " +
                        color + ", 0, 0, 0, 0);"
                    )
                )
            );
        pulse.setCycleCount(2);
        pulse.play();
    }
}