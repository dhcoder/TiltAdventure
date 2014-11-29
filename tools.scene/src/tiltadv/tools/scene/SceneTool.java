package tiltadv.tools.scene;

import javax.swing.*;

/**
 * Main class for the scene tool. Acts as a collection of all high level UI elements and components, as well
 * as being the entry point of the application.
 */
public final class SceneTool {

    private JPanel rootPanel;

    public static void main(final String[] args) {
        JFrame frame = new JFrame("SceneTool");
        frame.setContentPane(new SceneTool().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
