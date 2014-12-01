package dhcoder.tool.command;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class CommandScopeTest {
    @Test
    public void nestedScopesHaveExpectedNames() {
        CommandScope fileScope = new CommandScope("File");
        CommandScope fileSettingsScope = new CommandScope("Settings", fileScope);
        CommandScope fileSettingsMiscScope = new CommandScope("Misc", fileSettingsScope);

        assertThat(fileScope.getName(), equalTo("File"));
        assertThat(fileScope.getFullName(), equalTo("File"));
        assertThat(fileSettingsScope.getName(), equalTo("Settings"));
        assertThat(fileSettingsScope.getFullName(), equalTo("File.Settings"));
        assertThat(fileSettingsMiscScope.getName(), equalTo("Misc"));
        assertThat(fileSettingsMiscScope.getFullName(), equalTo("File.Settings.Misc"));
    }
}