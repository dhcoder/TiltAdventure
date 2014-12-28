package dhcoder.tool.javafx.fxutils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

import static dhcoder.support.text.StringUtils.format;

/**
 * Base class for tightly coupled controller / FX node components. Also provides a helper load method.
 * <p/>
 * The pattern here is the controller should expose the root node it controls, and you use {@link FxController#load
 * (Class,
 * String)} to load both classes at once.
 * <p/>
 * For example, say you have a calculator UI. A good way to represent this would be:
 * <pre>
 *     some.parent.package/
 *        CalculatorController.java
 *        CalculatorView.fxml
 * </pre>
 * where {@code Calculator extends FxUi}
 * <p/>
 * Then, later:
 * <pre>
 *   Calculator calculator = FxUi.load(CalculatorController.class, "CalculatorView.fxml");
 *   // or if fxml naming is consistent:
 *   // Calculator calculator = FxUi.load(Calculator.class);
 *   scene.setRoot(calculator.getRoot());
 * </pre>
 */
public abstract class FxController {

    private static final String CONTROLLER_NAMING_CONVENTION = "Controller";

    /**
     * Loads an FXML file whose controller inherits from {@link FxController}.
     */
    public static <C extends FxController> C load(final Class<C> controllerClass, final String fxmlFile) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(controllerClass.getResource(fxmlFile));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(
                format("Can't load {0}/{1}, detail: {2}", controllerClass.getSimpleName(), fxmlFile, e));
        }

        C controller = loader.getController();
        controller.setRoot(loader.getRoot());

        return controller;
    }

    /**
     * Helper method which calls {@link #load(Class, String)} assuming a "XxxController" -> "XxxView.fxml" naming
     * relationship.
     */
    public static <C extends FxController> C load(final Class<C> controllerClass) {
        String simpleName = controllerClass.getSimpleName();
        if (!simpleName.endsWith(CONTROLLER_NAMING_CONVENTION)) {
            throw new IllegalArgumentException(
                format("Invalid class name {0}, must end with \"Controller\"", simpleName));
        }

        StringBuilder fxmlFileBuilder = new StringBuilder(simpleName);
        fxmlFileBuilder
            .replace(simpleName.length() - CONTROLLER_NAMING_CONVENTION.length(), simpleName.length(), "View.fxml");

        return load(controllerClass, fxmlFileBuilder.toString());
    }

    private Parent root;

    /**
     * Returns the node associated with this controller.
     */
    public Parent getRoot() {
        return root;
    }

    void setRoot(final Parent root) {
        this.root = root;
    }
}
