package dhcoder.tool.javafx.control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * A dialog which can search through all registered, named actions.
 */
public final class CommandWindow extends Stage {

    public CommandWindow() {
        super(StageStyle.UNDECORATED);
        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
                final Boolean newValue) {
                if (newValue == false) {
                    hide();
                }
            }
        });

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CommandWindow.class.getResource("CommandWindowView.fxml"));
        try {
            Pane commandWindowView = loader.load();
            setScene(new Scene(commandWindowView));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
