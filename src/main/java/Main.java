import view.Game;

import javax.swing.*;
/**
 * A class for the main method.
 *
 * @author Group 5
 */
public class Main {
    /**
     * main method, entry point of the program.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Game();
            }
        });
    }
}
