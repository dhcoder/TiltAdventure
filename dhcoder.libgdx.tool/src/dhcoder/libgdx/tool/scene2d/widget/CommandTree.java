package dhcoder.libgdx.tool.scene2d.widget;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.libgdx.tool.command.CommandScope;

/**
 * A floating window which shows a list of commands in an easily navigatable tree
 * format.
 */
public final class CommandTree extends Window {

    public CommandTree(final CommandManager commandManager, final Skin skin) {
        super("Commands", skin);
        setResizable(true);

        setWidth(500f);
        setHeight(300f);

        Tree tree = new Tree(skin);
        ScrollPane treePane = new ScrollPane(tree, skin);
        treePane.setFadeScrollBars(false);
        add(treePane).expand().fill();

        TreeNodeAdder treeNodeAdder = new TreeNodeAdder(tree);
        for (CommandScope commandScope : commandManager.getCommandScopes()) {
            addScopeToTree(treeNodeAdder, commandScope, skin);
        }

//        tree.add(new Tree.Node(new Label("TEST", skin)));
//        Tree.Node node = new Tree.Node(new Label("PARENT", skin));
//        tree.add(node);
//        node.add(new Tree.Node(new Label("TEST2", skin)));
//        node.add(new Tree.Node(new Label("TEST3", skin)));
//        tree.add(new Tree.Node(new Label("TEST4", skin)));
//        tree.add(new Tree.Node(new Label("TEST5", skin)));


    }

    private static interface NodeAdder {
        public void add(final Node node);
    }

    private static final class TreeNodeAdder implements NodeAdder {
        private final Tree tree;

        public TreeNodeAdder(final Tree tree) {
            this.tree = tree;
        }

        @Override
        public void add(final Node node) {
            tree.add(node);
        }
    }

    private static final class ChildNodeAdder implements NodeAdder {
        private final Node parentNode;

        public ChildNodeAdder(final Node parentNode) {
            this.parentNode = parentNode;
        }

        @Override
        public void add(final Node node) {
            parentNode.add(node);
        }
    }

    private void addScopeToTree(final NodeAdder nodeAdder, final CommandScope commandScope, final Skin skin) {
        Node scopeNode = createNodeFor(commandScope, skin);
        nodeAdder.add(scopeNode);

        ChildNodeAdder childAdder = new ChildNodeAdder(scopeNode);
        for (Command command : commandScope.getCommands()) {
            Node commandNode = createNodeFor(command, skin);
            childAdder.add(commandNode);
        }

        for (CommandScope childScope : commandScope.getChildren()) {
            addScopeToTree(childAdder, childScope, skin);
        }
    }

    private Node createNodeFor(final CommandScope commandScope, final Skin skin) {
        return new Node(new Label(commandScope.getName(), skin));
    }

    private Node createNodeFor(final Command command, final Skin skin) {
        return new Node(new Label(command.getName(), skin));
    }
}
