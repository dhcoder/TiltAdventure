package dhcoder.tool.javafx.control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

public final class CommandRowController {
    @FXML private Pane pane;

    @FXML private TextFlow flowCommandName;
    @FXML private Label labelShortcut;

    public Pane getPane() {
        return pane;
    }

    public TextFlow getFlowCommandName() {
        return flowCommandName;
    }

    public Label getLabelShortcut() {
        return labelShortcut;
    }

}
