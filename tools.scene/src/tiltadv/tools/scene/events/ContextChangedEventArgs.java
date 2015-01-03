package tiltadv.tools.scene.events;

import dhcoder.support.opt.Opt;
import dhcoder.tool.event.ToolEventArgs;
import tiltadv.tools.scene.SceneContext;

/**
 * Event fired when the tool's active scene context changes.
 */
public final class ContextChangedEventArgs extends ToolEventArgs {
    private final Opt<SceneContext> sceneContextOpt = Opt.withNoValue();

    /**
     * Constuctor for when the context changed to a new context.
     */
    public ContextChangedEventArgs(final SceneContext sceneContext) {
        sceneContextOpt.set(sceneContext);
    }

    /**
     * Constructor for when the context changed because it was cleared.
     */
    public ContextChangedEventArgs() {}

    /**
     * Return the context we changed to. If cleared, the returned optional will have no value.
     */
    public Opt<SceneContext> getContextOpt() {
        return sceneContextOpt;
    }
}
