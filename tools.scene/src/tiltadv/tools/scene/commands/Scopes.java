package tiltadv.tools.scene.commands;

import dhcoder.libgdx.tool.command.CommandScope;

/**
 * A list of all commands used by this tool.
 */
public final class Scopes {

    public static final CommandScope Global = new CommandScope();
    public static final CommandScope Help = new CommandScope("Help", Global);
    public static final CommandScope File = new CommandScope("File", Global);

    private Scopes() {} // Static class
}
