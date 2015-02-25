package dhcoder.tool.javafx.property;

import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static dhcoder.support.text.StringUtils.format;

public final class PropertyUtilsTest {

    public static class SimplePropertyClass {
        public int getValue() { return -1; }
    }

    private static class EditablePropertiesClass {
        private int count;
        private String text;

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }

        public String getText() {
            return text;
        }

        public void setText(final String text) {
            this.text = text;
        }
    }

    @PropertyDefn(name = "UIDisplayName", displayName = "UI Display Name")
    private static class MultiWordPropertiesClass {

        public int getMaxSize() {
            return 0;
        }

        public String getUIDisplayName() {
            return "";
        }
    }

    @PropertyDefn(name = "Width", description = "Width Description")
    @PropertyDefn(name = "Height", description = "Height Description")
    private static class DescriptionPropertiesClass {
        public int getWidth() { return 100; }

        public int getHeight() { return 250; }

    }

    @PropertyDefn(name = "Excluded", exclude = true)
    @PropertyDefn(name = "AlsoExcluded", exclude = true)
    private static class ExcludedPropertiesClass {
        public boolean getIncluded() { return true; }
        public boolean getExcluded() { return false; }
        public boolean getAlsoIncluded() { return true; }
        public boolean getAlsoExcluded() { return false; }
    }

    @PropertyDefn(name = "Width", category = "Size")
    @PropertyDefn(name = "Height", category = "Size")
    @PropertyDefn(name = "Name", category = "Info")
    private static class CategoryPropertiesClass {
        public int getWidth() { return 10; }
        public int getHeight() { return 15; }
        public String getName() { return "Dummy"; }
    }

    @PropertyDefn(name = "Widht")
    private static class UnreferencedPropertyClass {
        public int getWidth() { return 10; }
    }

    @Test
    public void testDefaultPropertyValuesAreSensible() {
        SimplePropertyClass simplePropertyInstance = new SimplePropertyClass();
        ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(simplePropertyInstance);
        PropertySheet.Item propertyValue = assertContains(properties, "Value");
        assertThat(propertyValue.getName().equals("Value"));
        assertThat(propertyValue.getCategory()).isEmpty();
        assertThat(propertyValue.getDescription()).isEmpty();
        assertThat(propertyValue.isEditable()).isFalse();
    }

    @Test
    public void testEditableProperties() {
        EditablePropertiesClass editablePropertiesInstance = new EditablePropertiesClass();
        ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(editablePropertiesInstance);

        assertThat(properties.size()).is(2);

        PropertySheet.Item propertyText = assertContains(properties, "Text");
        assertThat(propertyText.isEditable()).isTrue();

        PropertySheet.Item propertyCount = assertContains(properties, "Count");
        assertThat(propertyCount.isEditable()).isTrue();
    }

    @Test
    public void testMultiWordPrpoerties() {
        MultiWordPropertiesClass multiWordPropertiesInstance = new MultiWordPropertiesClass();
        ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(multiWordPropertiesInstance);

        assertThat(properties.size()).is(2);

        assertContains(properties, "Max Size");
        assertContains(properties, "UI Display Name");
    }

    @Test
    public void testCategoryProperties() {
        CategoryPropertiesClass categoryPropertiesInstance = new CategoryPropertiesClass();
        ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(categoryPropertiesInstance);

        assertThat(properties.size()).is(3);

        PropertySheet.Item propertyWidth = assertContains(properties, "Width");
        PropertySheet.Item propertyHeight = assertContains(properties, "Height");
        PropertySheet.Item propertyName = assertContains(properties, "Name");

        assertThat(propertyWidth.getCategory()).isEqualTo("Size");
        assertThat(propertyHeight.getCategory()).isEqualTo("Size");
        assertThat(propertyName.getCategory()).isEqualTo("Info");
    }


    @Test
    public void testDescriptionProperties() {
        DescriptionPropertiesClass descriptionPropertiesInstance = new DescriptionPropertiesClass();
        ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(descriptionPropertiesInstance);

        assertThat(properties.size()).is(2);

        PropertySheet.Item propertyWidth = assertContains(properties, "Width");
        PropertySheet.Item propertyHeight = assertContains(properties, "Height");

        assertThat(propertyWidth.getDescription()).isNotEmpty();
        assertThat(propertyHeight.getDescription()).isNotEmpty();
        assertThat(propertyWidth.getDescription().equals(propertyHeight.getDescription())).isFalse();
    }

    @Test
    public void testExcludeProperties() {
        ExcludedPropertiesClass excludedPropertiesInstance = new ExcludedPropertiesClass();
        ObservableList<PropertySheet.Item> properties = PropertyUtils.getProperties(excludedPropertiesInstance);

        assertThat(properties.size()).is(2);

        assertContains(properties, "Included");
        assertContains(properties, "Also Included");
    }

    @Test(expected = UnreferencedPropertyException.class)
    public void unreferencedPropertyExceptionThrownIfPropertyNotFound() {
        UnreferencedPropertyClass unreferencedPropertyInstance = new UnreferencedPropertyClass();
        PropertyUtils.getProperties(unreferencedPropertyInstance);
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