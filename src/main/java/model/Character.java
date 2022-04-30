package model;

import java.awt.*;
import java.awt.image.BufferedImage;

import control.Background;
import view.Board;

/**
 * A super class to help with the enemy and player class.
 *
 * @author Group 5
 */
public class Character {
    public Point pos;
    public BufferedImage up, down, left, right, extra1;
    public String direction = "down";
    int tileSize = Board.getTileSize();

    /**
     * Moves the entity up the baord.
     */
    public void moveUp() {
        if (checkWall(pos.x, pos.y - tileSize)) {
            direction = "up";
            pos.translate(0, -tileSize);
        }
    }

    /**
     * Moves the entity down the baord.
     */
    public void moveDown() {
        if (checkWall(pos.x, pos.y + tileSize)) {
            direction = "down";
            pos.translate(0, tileSize);
        }
    }

    /**
     * Moves the entity right
     */
    public void moveRight() {
        if (checkWall(pos.x + tileSize, pos.y)) {
            direction = "right";
            pos.translate(tileSize, 0);
        }

    }

    /**
     * Moves the entity left
     */
    public void moveLeft() {
        if (checkWall(pos.x - tileSize, pos.y)) {
            direction = "left";
            pos.translate(-tileSize, 0);
        }
    }

    /**
     * Checks the coordinates of the parameters to see if a wall is there. Used in move() methods.
     * @param currX Current X coordinate of the entity
     * @param currY Current Y coordinate of the entity
     * @return false if wall isn't there, true if wall is there.
     */
    public boolean checkWall(int currX, int currY) {
        int newX = currX / tileSize;
        int newY = currY / tileSize;

        if (newX < 0 || newY < 0 || newX >= Board.getColumns() || newY >= Board.getRows()) {
            return false;
        }

        return (Background.getStatus(newX, newY) < 2 || (Background.getStatus(newX, newY) > 9 &&
                Background.getStatus(newX, newY) < 16) && Background.getStatus(newX, newY) != 14);
    }
}
