package dhcoder.tool.javafx.control;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;

import java.io.IOException;

/**
 * A dialog which can search through all registered, named actions.
 */
public final class CommandWindow extends PopupWindow {

    public CommandWindow() {
        autoHideProperty().set(true);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CommandWindow.class.getResource("CommandWindowView.fxml"));
        try {
            Pane commandWindowView = loader.load();
            getScene().setRoot(commandWindowView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
