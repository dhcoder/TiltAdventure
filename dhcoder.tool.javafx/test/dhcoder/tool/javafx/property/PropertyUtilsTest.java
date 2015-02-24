package dhcoder.tool.javafx.property;

import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static dhcoder.support.text.StringUtils.format;

public final class PropertyUtilsTest {

    private static class SimplePropertiesClass {
        private int getCountCalled;
        private int setCountCalled;
        private int setTextCalled;
        private int getTextCalled;
        private int count;
        private String text;

        public int getCount() {
            getCountCalled++;
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
            setCountCalled++;
        }

        public String getText() {
            getTextCalled++;
            return text;
        }

        public void setText(final String text) {
            this.text = text;
            setTextCalled++;
        }
    }

    @PropertyMeta(name="UIDisplayName", displayName = "UI Display Name")
    private static class MultiWordPropertiesClass {

        public int getMaxSize() {
            return 0;
        }

        public String getUIDisplayName() {
            return "";
        }
    }

    @Test public void testSimpleProperties() {
        SimplePropertiesClass simplePropertiesInstance = new SimplePropertiesClass();
        final ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(simplePropertiesInstance);

        assertThat(properties.size()).is(2);

        final PropertySheet.Item propertyText = assertContains(properties, "Text");
        assertThat(propertyText.isEditable()).isTrue();
        assertThat(propertyText.getCategory()).isEmpty();

        final PropertySheet.Item propertyCount = assertContains(properties, "Count");
        assertThat(propertyCount.isEditable()).isTrue();
        assertThat(propertyText.getCategory()).isEmpty();
    }

    @Test public void testMultiWordPrpoerties() {
        MultiWordPropertiesClass multiWordPropertiesInstance = new MultiWordPropertiesClass();
        ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(multiWordPropertiesInstance);

        assertThat(properties.size()).is(2);

        assertContains(properties, "Max Size");
        assertContains(properties, "UI Display Name");

    }

    private PropertySheet.Item assertContains(final List<PropertySheet.Item> properties, final String displayName) {
        for (PropertySheet.Item property : properties) {
            if (property.getName().equals(displayName)) {
                return property;
            }
        }

        throw new RuntimeException(format("Could not find property with display name \"{0}\"", displayName));
    }
}