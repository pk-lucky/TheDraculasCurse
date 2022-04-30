package model;

import control.Background;
import view.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Random;

/**
 * A class to control the protagonist.
 *
 * @author Group 5
 */
public class Player extends Character {

    private int score;
    private int keyCount;

    private int key;
    boolean check = false;
    private int moves = 8;
    Point trapPos = new Point();
    Sound effect = new Sound();

    public Player() {
        loadImage();
        pos = new Point(tileSize, tileSize);
        score = 0;
    }

    /**
     * Called by the constructor. Loads the images for every direction that the player faces
     */
    private void loadImage() {
        try {
            up = ImageIO.read(getClass().getResource("/player/idleup.png"));
            down = ImageIO.read(getClass().getResource("/player/idledown.png"));
            left = ImageIO.read(getClass().getResource("/player/idleleft.png"));
            right = ImageIO.read(getClass().getResource("/player/idleright.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This is called in the board repaint method. This repaints the player in the direction it is facing
     * @param g Graphics
     * @param observer ImageObserver
     */
    public void draw(Graphics g, ImageObserver observer) {
        BufferedImage sprite = null;

        if (direction.equals("up")) {
            sprite = up;
        } else if (direction.equals("down")) {
            sprite = down;
        } else if (direction.equals("left")) {
            sprite = left;
        } else if (direction.equals("right")) {
            sprite = right;
        }
        g.drawImage(sprite, pos.x, pos.y, tileSize - 15, tileSize - 15, observer);
    }

    /**
     *
     * This is how the player receives the keyboard inputs to perform movement. It calls uses the inherited methods
     * from Character.java to move.
     * @param e Key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        key = e.getKeyCode();

        if (key == KeyEvent.VK_UP) {
            direction = "up";
            moveUp();
            moves++;
        }
        if (key == KeyEvent.VK_RIGHT) {
            direction = "right";
            moveRight();
            moves++;
        }
        if (key == KeyEvent.VK_DOWN) {
            direction = "down";
            moveDown();
            moves++;
        }
        if (key == KeyEvent.VK_LEFT) {
            direction = "left";
            moveLeft();
            moves++;
        }
    }

    /**
     * Checks the player's position to see if
     * 1. If a bonus should spawn (according to the player's movement)
     * 2. To reset the trap (according to player's position)
     * 3. IF a reward is collected
     * 4. If the player is punished
     * 5. If a bonus is collected
     */
    public void tick() {
        int newX = getXposition();
        int newY = getYposition();

        //bonus spawn
        if (moves == 8) {
            moves = 0;
            Background.bonusSpawn();
        }
        // Reset spikes
        if (check && Background.getStatus(newX, newY) != 12) {
            Background.setStatus(trapPos.x, trapPos.y, 11);
            check = false;
        }
        //reward points
        if (Background.getStatus(newX, newY) == 10) {
            score++;
            keyCount++;
            Background.setStatus(newX, newY, 0);
            effect.setFile(3);
            effect.play();
        } else if (Background.getStatus(newX, newY) == 11) {
            //punish the player
            effect.setFile(4);
            effect.play();

            if (score == 0) {
                Board.caughtUp = true;
                Background.setStatus(newX,newY,12);
            } else {
                score--;
                Background.setStatus(newX,newY,12);
                check = true;
                trapPos.x = newX;
                trapPos.y = newY;
            }
            //Bonus points
        } else if (Background.getStatus(newX, newY) == 13) {
            score += 2;
            Background.setStatus(newX, newY, 0);
            effect.setFile(3);
            effect.play();
        }
    }

    /**
     * checks to see if the player is at the door and is open. Basically if the player has
     * completed the game
     * @return true if player is at door and door is open, false if not
     */
    public boolean isPlayerAtDoor() {
        if (getXposition() == 1 && getYposition() == 0 && Background.getStatus(1, 0) == 15) {
            {
                return true;
            }
        } else {
            return false;
        }
    }


    public int getScore() {
        return score;
    }

    public int getXposition() {
        return pos.x / tileSize;
    }

    public int getYposition() {
        return pos.y / tileSize;
    }

    public int getKey() {
        return key;
    }

    public int getKeyCount(){
        return keyCount;
    }

    //for testing
    public void setScore(int score) {
        this.score = score;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public void setPosition(int x, int y) {
        Point pt = new Point(x * 64, y * 64);
        this.pos = pt;
    }

}
