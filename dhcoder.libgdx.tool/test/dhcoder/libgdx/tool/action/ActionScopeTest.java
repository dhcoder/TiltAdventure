package dhcoder.libgdx.tool.action;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class ActionScopeTest {
    @Test
    public void nestedScopesHaveExpectedNames() {
        ActionScope fileScope = new ActionScope("File");
        ActionScope fileSettingsScope = new ActionScope("Settings", fileScope);
        ActionScope fileSettingsMiscScope = new ActionScope("Misc", fileSettingsScope);

        assertThat(fileScope.getName(), equalTo("File"));
        assertThat(fileScope.getFullName(), equalTo("File"));
        assertThat(fileSettingsScope.getName(), equalTo("Settings"));
        assertThat(fileSettingsScope.getFullName(), equalTo("File.Settings"));
        assertThat(fileSettingsMiscScope.getName(), equalTo("Misc"));
        assertThat(fileSettingsMiscScope.getFullName(), equalTo("File.Settings.Misc"));
    }
}