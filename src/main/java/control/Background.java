package control;

import model.Tiles;
import view.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Random;

/**
 * A class to load and set the underlying map/game background
 *
 * @author Group 5
 */
public class Background {

    /**
     * holds all the tile images for the game
     */
    public Tiles[] tile;

    /**
     * represents the total number of rewards
     */
    public static int totalRewards;

    /**
     * represents the actual map in a 2D array where each digit corresponds to a specific tile image, 0 for grass e.t.c.
     */
    public static int[][] Boardstatus;

    /**
     * represents the actual map in a 2D array where each digit corresponds to a specific tile image, 0 for grass e.t.c. (not subject to change.)
     */
    public int[][] MapTileNum;
    private static Point bonus = new Point(1, 1);

    /**
     * A constructor for the class that builds the map.
     */
    public Background() {
        MapTileNum = new int[Board.TILE_SIZE * 20][Board.TILE_SIZE * 11];
        tile = new Tiles[20];
        loadMap();
        setTileImage();
    }

    /**
     * Sets the tile images to corresponding digits for the underlying map.txt and loadMap to work
     */
    public void setTileImage() {
        try {
            //Free model.Tiles
            tile[0] = new Tiles();
            tile[0].image = ImageIO.read(getClass().getResource("/dungeon/tiles/newTile1.png"));
            tile[1] = new Tiles();
            tile[1].image = ImageIO.read(getClass().getResource("/dungeon/tiles/newTile2.png"));

            //Wall model.Tiles
            tile[2] = new Tiles();
            tile[2].image = ImageIO.read(getClass().getResource("/dungeon/tiles/wall3.png"));
            tile[3] = new Tiles();
            tile[3].image = ImageIO.read(getClass().getResource("/dungeon/tiles/cornerTopLeft.png"));
            tile[4] = new Tiles();
            tile[4].image = ImageIO.read(getClass().getResource("/dungeon/tiles/cornerTopRight.png"));
            tile[5] = new Tiles();
            tile[5].image = ImageIO.read(getClass().getResource("/dungeon/tiles/right2.png"));
            tile[6] = new Tiles();
            tile[6].image = ImageIO.read(getClass().getResource("/dungeon/tiles/left2.png"));
            tile[7] = new Tiles();
            tile[7].image = ImageIO.read(getClass().getResource("/dungeon/tiles/cornerBottomLeft.png"));
            tile[8] = new Tiles();
            tile[8].image = ImageIO.read(getClass().getResource("/dungeon/tiles/cornerBottomRight.png"));
            tile[9] = new Tiles();
            tile[9].image = ImageIO.read(getClass().getResource("/dungeon/tiles/bottomWall.png"));

            //Rewards,Punishments and Bonuses
            tile[10] = new Tiles();
            tile[10].image = ImageIO.read(getClass().getResource("/dungeon/tiles/reward.png"));
            tile[11] = new Tiles();
            tile[11].image = ImageIO.read(getClass().getResource("/dungeon/tiles/newTrap.png"));
            tile[12] = new Tiles();
            tile[12].image = ImageIO.read(getClass().getResource("/dungeon/tiles/trapSet.png"));
            tile[13] = new Tiles();
            tile[13].image = ImageIO.read(getClass().getResource("/dungeon/tiles/bonus.png"));

            //Door
            tile[14] = new Tiles();
            tile[14].image = ImageIO.read(getClass().getResource("/dungeon/tiles/closedDoor.png"));
            tile[15] = new Tiles();
            tile[15].image = ImageIO.read(getClass().getResource("/dungeon/tiles/openDoor2.png"));

            //Obstacles
            tile[16] = new Tiles();
            tile[16].image = ImageIO.read(getClass().getResource("/dungeon/tiles/bones.png"));
            tile[17] = new Tiles();
            tile[17].image = ImageIO.read(getClass().getResource("/dungeon/tiles/skulls.png"));
            tile[18] = new Tiles();
            tile[18].image = ImageIO.read(getClass().getResource("/dungeon/tiles/barrier.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * draws the board with the helps of Graphics2D, simply iterates over the map 2d array
     * and prints at corresponding x and y locations
     * @param G2D to render 2D images (tiles)
     */
    public void draw(Graphics2D G2D) {
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;
        while (col < 20 && row < 11) {
            int tileNum = MapTileNum[col][row];
            G2D.drawImage(tile[tileNum].image, x, y, Board.TILE_SIZE, Board.TILE_SIZE, null);
            col++;

            x += Board.TILE_SIZE;

            if (col == 20) {
                col = 0;
                x = 0;
                row++;
                y += Board.TILE_SIZE;
            }
        }
    }

    /**
     * reads the map.txt file to load the mapTileNum 2D array that represents rows and columns
     * with corresponding tile numbers.
     */
    public void loadMap() {
        totalRewards = 0;
        try {
            InputStream is = (getClass().getResourceAsStream("/map.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < 20 && row < 11) {
                String line = br.readLine();

                while (col < 20) {
                    String[] numbers = line.split(" ");
                    MapTileNum[col][row] = Integer.parseInt(numbers[col]);
                    if (MapTileNum[col][row] == 10) {
                        totalRewards++;
                    }
                    col++;
                }
                if (col == 20) {
                    col = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Boardstatus = MapTileNum;
    }


    /**
     * spawn bonus (used in Player.java)
     */
    public static void bonusSpawn(){
        setStatus(bonus.x, bonus.y, 0);
        bonus = randomPoint();
        setStatus(bonus.x, bonus.y, 13);
    }

    //produces a random bonus on the board
    private static Point randomPoint() {
        Random rand = new Random();
        Point bonus = new Point();
        int check;
        do {
            bonus.x = rand.nextInt(20);
            bonus.y = rand.nextInt(11);
            check = Background.getStatus(bonus.x, bonus.y);
        } while (check >= 2);

        return bonus;
    }

    /**
     * gets the tile at the location pointed by the parameters
     * @param x,y integers that represent coordinates of the map
     * @return the tile number at the requested coordinate.
     */
    public static int getStatus(int x, int y) {
        return Boardstatus[x][y];
    }
    /**
     * sets the tile at the index pointed by the parameters
     * @param x,y integer indexes that represent coordinates of the map
     * @param value integer value to set at the index
     */
    public static void setStatus(int x, int y, int value) {
        Boardstatus[x][y] = value;
    }

    /**
     * @return a bonus point
     */
    public static Point getBonus(){
        return bonus;
    }
}
