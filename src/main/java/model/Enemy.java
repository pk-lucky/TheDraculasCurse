package model;

import view.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

/**
 * A class to control the enemy.
 *
 * @author Group 5
 */
public class Enemy extends Character {
    boolean nextMoveLeft = false;
    boolean nextMoveRight = false;
    int currentX = 0;
    int currentY = 0;

    public Enemy(Point pos) {
        loadImage();
        this.pos = pos;
    }

    /**
     * Loads the images for the different enemy direction-faces. Used in constructor.
     */
    private void loadImage() {
        try {
            up = ImageIO.read(getClass().getResource(("/enemy/up.png")));
            down = ImageIO.read(getClass().getResource("/enemy/down.png"));
            left = ImageIO.read(getClass().getResource("/enemy/left.png"));
            right = ImageIO.read((getClass().getResource("/enemy/right.png")));
            extra1 = ImageIO.read((getClass().getResource("/enemy/tile019.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is called in the board repaint method. This repaints the enemy in the direction it is facing
     * @param g Graphics
     * @param observer ImageObserver
     */
    public void draw(Graphics g, ImageObserver observer) {
        BufferedImage sprite = null;

        if (direction == "up") {
            sprite = up;
        } else if (direction == "down") {
            sprite = down;
        } else if (direction == "left") {
            sprite = left;
        } else if (direction == "right") {
            sprite = right;
        } else if (direction == "caught") {
            sprite = extra1;
        }

        g.drawImage(sprite, pos.x, pos.y, Board.TILE_SIZE, Board.TILE_SIZE, observer);
    }

    /**
     * Called in board. Takes the player's position as parameters and checks to see if this entity is
     * in the same tile.
     * @param playerPositionX Player's x coordinate
     * @param playerPositionY Player's y coordinate
     */
    public void tick(int playerPositionX, int playerPositionY) {
        int newX = pos.x / Board.TILE_SIZE;
        int newY = pos.y / Board.TILE_SIZE;

        if (newX == playerPositionX && newY == playerPositionY) {
            Board.caughtUp = true;
            direction = "caught";
        }
    }

    /**
     * Takes the player in as a parameter. Calls moveHelp().
     * @param player Player object.
     */
    public void moveToPlayer(Player player) {

        //if key pressed is an arrow key
        if (player.getKey() == KeyEvent.VK_UP || player.getKey() == KeyEvent.VK_DOWN || player.getKey() == KeyEvent.VK_RIGHT || player.getKey() == KeyEvent.VK_LEFT) {
            int playerPositionX = player.getXposition();
            int playerPositionY = player.getYposition();

            currentX = pos.x / Board.TILE_SIZE;
            currentY = pos.y / Board.TILE_SIZE;


            //POSITIVE MEANS PLAYER IS TO THE RIGHT, A NEGATIVE WILL MEAN PLAYER IS TO THE LEFT
            int distanceX = (playerPositionX - currentX);

            //NEGATIVE Y MEANS PLAYER IS ABOVE ENEMY, POSITIVE Y WILL MEAN PLAYER IS BELOW
            int distanceY = (playerPositionY - currentY);

            moveHelp(distanceX, distanceY);

        }

    }


    /**
     * Takes the player's x and y coordinates on the board as parameters. Calculates the shortest route to take to the
     * player and moves to it.
     * @param x Player x coordinate
     * @param y Player y coordinate
     */
    public void moveHelp(int x, int y) {

        //if enemy is in the same column as player
        //if player is to the right
        if ((x == 0) && (y > 0)) {
            if (!checkWall(pos.x, pos.y + Board.TILE_SIZE)) {
                moveRight();
            } else {
                moveDown();
            }
        }
        if ((x == 0) && (y < 0)) {
            if (!checkWall(pos.x, pos.y - Board.TILE_SIZE)) {
                moveLeft();
            } else {
                moveUp();
            }
        }

        //if enemy is in the same row as player
        if((x < 0) && (y == 0)) {
            //if player is to the left
            if (!checkWall(pos.x - Board.TILE_SIZE, pos.y)) {
                moveDown();
                nextMoveLeft = true;
            } else {
                moveLeft();
            }
        }
        //player is to the right
        if((x > 0) && (y == 0)) {
            if (!checkWall(pos.x + Board.TILE_SIZE, pos.y)) {
                moveDown();
                nextMoveRight = true;
            } else {
                moveRight();
            }
        }

        //stops enemy from getting stuck in a loop
        else if(nextMoveLeft){
            moveLeft();
            nextMoveLeft = false;
        }

        //stops enemy from getting stuck in a loop
        else if(nextMoveRight){
            moveRight();
            nextMoveRight = false;
        }

        //chooses the shortest route if player is neither in the same column or row.
        if ((x < 0) && (y < 0)) {
            //Up Left
            if (!checkWall(pos.x - Board.TILE_SIZE, pos.y) || !checkWall(pos.x - Board.TILE_SIZE, pos.y - Board.TILE_SIZE)) {
                moveUp();
            } else {
                moveLeft();
            }
        }
        //up and right
        if ((x > 0) && (y < 0)) {
            if (!checkWall(pos.x + Board.TILE_SIZE, pos.y) || !checkWall(pos.x + Board.TILE_SIZE, pos.y - Board.TILE_SIZE)) {
                moveUp();
            } else {
                moveRight();
            }
        }

        //Down Left
        if ((x < 0) && (y > 0)) {

            if (!checkWall(pos.x - Board.TILE_SIZE, pos.y) || !checkWall(pos.x - Board.TILE_SIZE, pos.y + Board.TILE_SIZE)) {
                moveDown();
            } else {
                moveLeft();
            }
        }
        //down and right
        if ((x > 0) && (y > 0)) {
            if (!checkWall(pos.x + Board.TILE_SIZE, pos.y) || !checkWall(pos.x + Board.TILE_SIZE, pos.y + Board.TILE_SIZE)) {
                moveDown();
            } else {
                moveRight();
            }
        }
    }

    public int getXPosition() {
        currentX = pos.x / Board.TILE_SIZE;
        return currentX;
    }

    public int getYPosition() {
        currentY = pos.y / Board.TILE_SIZE;
        return currentY;
    }
}