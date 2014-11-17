package tiltadv.tools.scene.action;

import dhcoder.libgdx.tool.action.ActionScope;

/**
 * A list of all scopes used by this tool.
 */
public final class Scopes {

    public static final ActionScope Global = new ActionScope();
    public static final ActionScope Help = new ActionScope("Help", Global);
    public static final ActionScope File = new ActionScope("File", Global);
    public static final ActionScope Edit = new ActionScope("Edit", Global);

    private Scopes() {} // Static class
}
