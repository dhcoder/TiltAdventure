package dhcoder.tool.javafx.control;

import dhcoder.tool.javafx.utils.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

public final class ActionRowController extends FxController {
    @FXML private Pane rootPane;
    @FXML private TextFlow flowCommandName;
    @FXML private Label labelShortcut;

    public Pane getPane() { return rootPane; }

    public TextFlow getFlowCommandName() {
        return flowCommandName;
    }

    public Label getLabelShortcut() {
        return labelShortcut;
    }
}
