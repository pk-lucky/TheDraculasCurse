package view;

import control.Background;
import model.Enemy;
import model.Player;
import model.Sound;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * A class for the main game loop.
 *
 * @author Group 5
 */
public class Board extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 704;
    public static final int TILE_SIZE = 64;

    /**
     * used for checking if an enemy has caught up to the player
     */
    public static boolean caughtUp = false;

    private static final int ROWS = 10;
    private static final int COLUMNS = 19;

    private boolean ifEnd = false;
    // controls the delay between each tick in ms
    private final int DELAY = 25;

    // keep a reference to the timer object that triggers actionPerformed() in
    // case we need access to it in another method
    private Timer timer;
    private Player player;
    private Background background;

    /**
     * an arraylist of enemies
     */
    public ArrayList<Enemy> enemies;
    private boolean runOnce = false;
    long startTime;

    /**
     * variable for the game timer
     */
    public long elaTime = 0;
    long time = 0;

    private boolean punishmentVisible = false;
    private int punishmentPos = 0;
    private long punishmentSpawnTimer = 0;
    private final Sound soundTrack = new Sound();
    private final Sound backgroundMusic = new Sound();

    /**
     * A constructor for the class that sets the game board and everything in it.
     */
    public Board() {
        Game.background.stop();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        //sets up the boards and players value so that it can be printed once repaint() is called under the gameloop.
        background = new Background();
        player = new Player();
        enemies = new ArrayList<>();
        enemies.add(new Enemy(new Point(TILE_SIZE * 5, TILE_SIZE * 7)));

        backgroundMusic.setFile(0);
        backgroundMusic.loop();

        // this timer will call the actionPerformed() method every DELAY ms,
        timer = new Timer(DELAY, this);
        timer.start();
        startTime = System.nanoTime();

    }


    /**
     * prints the background and the player to the screen
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D G2D = (Graphics2D) g;
        background.draw(G2D);

        if (enemies.size() == 0) {
            player.draw(g, this);
        } else {
            for (Enemy enemy : enemies) {
                if (!caughtUp) {
                    player.draw(g, this);
                }
                enemy.draw(g, this);
            }
        }

        Toolkit.getDefaultToolkit().sync();

        g.setColor(Color.white);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 64));
        g.drawString("Score:" + (player.getScore()), 400, 50);
        g.drawString("Time:" + getTime(), 700, 50);

        time = getTime();
        if (time % 5 == 0 && time - punishmentSpawnTimer >= 5) {
            if (punishmentVisible) {
                punishmentVisible = false;
                punishmentSpawnTimer = time;
                removeRandomPunishments(punishmentPos);
            } else {
                punishmentVisible = true;
                punishmentSpawnTimer = time;
                punishmentPos = randomPunishments();
            }
        }

        //displays endScreen for when the player gets killed by monster
        if (caughtUp) {
            try {
                BufferedImage youDiedLabel = ImageIO.read(getClass().getResource("/game_over.png"));
                Image youDiedImage = youDiedLabel.getScaledInstance(450, 450, Image.SCALE_DEFAULT);

                BufferedImage restartLabel = ImageIO.read(getClass().getResource("/space.png"));
                Image restartImage = restartLabel.getScaledInstance(370, 570, Image.SCALE_DEFAULT);

                g.drawImage(youDiedImage, 415, 87, this);
                g.drawImage(restartImage, 462, 135, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //if the player is at the door and has collected all the keys
        if (player.isPlayerAtDoor()) {
            ifEnd = true;
            String playerTime = String.valueOf(elaTime / 1000000000);
            backgroundMusic.stop();
            Game.endScreen(String.valueOf(player.getScore()), playerTime);
        }
    }


    /**
     * this will essentially call for the repainting or refreshing of the whole game every tick.
     */
    public void actionPerformed(ActionEvent e) {
        elaTime = System.nanoTime() - startTime;

        // prevent the player from disappearing off the board
        // calling repaint() will trigger paintComponent() to run again,
        // which will refresh/redraw the graphics.
        if (!caughtUp && !ifEnd) {
            player.tick();
            checkDoorAndOpen();

            for (Enemy enemy : enemies) {
                enemy.tick(player.getXposition(), player.getYposition());
            }
            repaint();
        }

        // to let the program know the game has ended, so it doesn't repaint.
        for (Enemy enemy : enemies) {
            if (caughtUp && !runOnce) {
                caughtUp = true;
                soundTrack.setFile(2);
                soundTrack.play();
                runOnce = true;
            }
        }
    }

    /**
     *  this is not used but must be defined as part of the KeyListener interface.
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     *  acts as a listener for player inputs.
     *  each time a player presses a key this is executed and performs some action
     */
    public void keyPressed(KeyEvent e) {

        if (caughtUp) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                backgroundMusic.stop();
                resetBoard();
            }
        }
        // react to key down events
        player.keyPressed(e);

        //the enemy should move after the player moves
        for (Enemy enemy : enemies) {
            enemy.moveToPlayer(player);
        }
    }

    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    /**
     * spawns random punishment cells on the board.
     */
    public int randomPunishments() {
        int x = 0;
        int y = 0;
        Random random = new Random();
        while (Background.getStatus(x, y) > 1) {
            x = random.nextInt(Board.COLUMNS);
            y = random.nextInt(Board.ROWS);
        }
        Background.setStatus(x, y, 11);
        return (x * 100) + y;
    }

    /**
     * removes the randomly spawned punishment after a while.
     */
    public void removeRandomPunishments(int pos) {
        int y = pos % 100;
        int x = pos / 100;
        Background.setStatus(x, y, 0);
    }

    /**
     * resets the board for death and end screens.
     */
    public void resetBoard() {
        Game.background.stop();
        background.loadMap();
        player = new Player();
        enemies = new ArrayList<Enemy>();
        enemies.add(new Enemy(new Point(TILE_SIZE * 5, TILE_SIZE * 7)));
        if (caughtUp == true) {
            soundTrack.stop();
        }
        backgroundMusic.setFile(0);
        backgroundMusic.loop();
        caughtUp = false;
        ifEnd = false;
        runOnce = false;
        timer = new Timer(DELAY, this);
        timer.start();
        startTime = System.nanoTime();
    }

    public void checkDoorAndOpen(){
        if (player.getKeyCount() >= Background.totalRewards && Background.getStatus(1,0) != 15){
            Background.setStatus(1,0,15);
            soundTrack.setFile(5);
            soundTrack.play();
        }
    }

    //METHODS USED FOR TESTING

    public long getTime(){
        time = elaTime / 1000000000;
        return time;
    }

    public static int getRows(){
        return ROWS;
    }

    public static int getColumns(){
        return COLUMNS;
    }

    public static int getTileSize() {
        return TILE_SIZE;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean getCaughtUp() {
        return caughtUp;
    }

    public ArrayList getEnemies(){
        return enemies;
    }

    public void setElaTime(long i){
        elaTime = i;
    }

    public void setTime(long i){
        this.time = i;
    }

    public boolean getBonusVisible(){
        return punishmentVisible;
    }
}