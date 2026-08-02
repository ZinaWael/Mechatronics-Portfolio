package game.ui.controller;

import game.engine.Game;
import game.engine.Role;
import game.engine.monsters.Monster;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;

import java.io.IOException;

public class GameController {

    private Game game;

    // ── Constructor ────────────────────────────────────
    public GameController(String role) throws IOException {
        if (role.equals("SCARER")) {
            game = new Game(Role.SCARER);
        } else {
            game = new Game(Role.LAUGHER);
        }
    }

    // ── Getters ────────────────────────────────────────
    public Game getGame() {
        return game;
    }

    public Monster getPlayer() {
        return game.getPlayer();
    }

    public Monster getOpponent() {
        return game.getOpponent();
    }

    public Monster getCurrent() {
        return game.getCurrent();
    }

    public boolean isPlayerTurn() {
        return game.getCurrent() == game.getPlayer();
    }
    public int getLastRoll() {
        return game.getLastRoll();
    }

    // ── Actions ────────────────────────────────────────
    public void playTurn() throws InvalidMoveException {
        game.playTurn();
    }

    public void usePowerup() throws OutOfEnergyException {
        game.usePowerup();
    }

    public Monster getWinner() {
        return game.getWinner();
    }
}