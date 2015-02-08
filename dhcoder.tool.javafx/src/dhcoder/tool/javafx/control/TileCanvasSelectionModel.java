package dhcoder.tool.javafx.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A selection model for selected tiles in a {@link TileCanvas}
 */
public final class TileCanvasSelectionModel extends MultipleSelectionModel<TileCanvas.Tile> {

    private final TileCanvas tileCanvas;
    private final List<Integer> selectedIndices = new ArrayList<>();
    private final List<TileCanvas.Tile> selectedTiles = new ArrayList<>();
    private final ObservableList<Integer> observedIndices = FXCollections.observableArrayList();
    private final ObservableList<TileCanvas.Tile> observedTiles = FXCollections.observableArrayList();

    private boolean ignoreUpdates;

    public TileCanvasSelectionModel(final TileCanvas tileCanvas) {
        this.tileCanvas = tileCanvas;
    }

    @Override
    public void clearAndSelect(final int index) {
        clearAndSelect(tileCanvas.getTile(index));
    }

    public void clearAndSelect(final TileCanvas.Tile tile) {
        if (Objects.equals(getSelectedItem(), tile)) {
            return;
        }

        selectedIndices.clear();
        selectedTiles.clear();
        select(tile);
    }

    public void toggle(final int index) {
        if (isSelected(index)) {
            clearSelection(index);
        }
        else {
            select(index);
        }
    }

    public void toggle(final TileCanvas.Tile tile) {
        if (isSelected(tile)) {
            clearSelection(tile);
        }
        else {
            select(tile);
        }
    }

    @Override
    public void select(final int index) {
        select(tileCanvas.getTile(index));
    }

    @Override
    public void select(final TileCanvas.Tile tile) {
        if (Objects.equals(getSelectedItem(), tile)) {return;}

        setSelectedItem(tile);
        int index = tileCanvas.getIndex(tile);
        setSelectedIndex(index);

        if (getSelectionMode() == SelectionMode.SINGLE) {
            selectedTiles.clear();
            selectedIndices.clear();
        }

        if (!selectedTiles.contains(tile)) {
            selectedTiles.add(tile);
            selectedIndices.add(index);
            updateObservedLists();
        }
    }

    @Override
    public void clearSelection(final int index) {
        clearSelection(tileCanvas.getTile(index));
    }

    public void clearSelection(final TileCanvas.Tile tile) {
        if (selectedTiles.contains(tile)) {

            final int index = tileCanvas.getIndex(tile);
            selectedIndices.remove(Integer.valueOf(index));
            selectedTiles.remove(tile);

            if (getSelectedIndex() == index) {
                if (selectedIndices.size() == 0) {
                    setSelectedIndex(-1);
                    setSelectedItem(null);
                }
                else {
                    ignoreUpdates = true;
                    select(selectedIndices.get(selectedIndices.size() - 1));
                    ignoreUpdates = false;
                }

            }

            updateObservedLists();
        }
    }

    @Override
    public void clearSelection() {
        if (isEmpty()) {return;}

        selectedIndices.clear();
        selectedTiles.clear();
        setSelectedIndex(-1);
        setSelectedItem(null);
        updateObservedLists();
    }

    @Override
    public boolean isSelected(final int index) {
        return selectedIndices.contains(index);
    }

    public boolean isSelected(final TileCanvas.Tile tile) { return selectedTiles.contains(tile); }

    @Override
    public boolean isEmpty() {
        return selectedIndices.isEmpty();
    }

    @Override
    public void selectPrevious() {
        if (getSelectedIndex() >= 0) {
            select(tileCanvas.getIndexBefore(getSelectedIndex()));
        }
    }

    @Override
    public void selectNext() {
        if (getSelectedIndex() >= 0) {
            select(tileCanvas.getIndexAfter(getSelectedIndex()));
        }
    }

    public boolean isAnchor(final int index) {
        return selectedIndices.size() > 0 && index == 0;
    }

    public boolean isAnchor(final TileCanvas.Tile tile) {
        return selectedTiles.size() > 0 && selectedTiles.get(0).equals(tile);
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return observedIndices;
    }

    @Override
    public ObservableList<TileCanvas.Tile> getSelectedItems() {
        return observedTiles;
    }

    @Override
    public void selectIndices(final int index, final int... indices) {
        ignoreUpdates = true;
        for (int tailIndex : indices) {
            select(tailIndex);
        }

        select(index);
        ignoreUpdates = false;

        updateObservedLists();
    }

    @Override
    public void selectAll() {
        int[] tailIndices = new int[tileCanvas.getLastIndex()];
        for (int i = 1; i <= tileCanvas.getLastIndex(); i++) {
            tailIndices[i - 1] = i;
        }
        selectIndices(0, tailIndices);
    }

    @Override
    public void selectFirst() {
        select(0);
    }

    @Override
    public void selectLast() {
        select(tileCanvas.getLastIndex());
    }

    public void rangeSelect(final TileCanvas.Tile tile) {
        if (selectedTiles.size() == 0) {
            select(tile);
            return;
        }

        TileCanvas.Tile anchor = selectedTiles.get(0);

        ignoreUpdates = true;
        clearSelection();
        select(anchor); // Make sure anchor stays the anchor by selecting it first
        int xStart = Math.min(anchor.getX(), tile.getX());
        int xEnd = Math.max(anchor.getX(), tile.getX());
        int yStart = Math.min(anchor.getY(), tile.getY());
        int yEnd = Math.max(anchor.getY(), tile.getY());

        for (int x = xStart; x <= xEnd; ++x) {
            for (int y = yStart; y <= yEnd; ++y) {
                select(new TileCanvas.Tile(x, y));
            }
        }
        ignoreUpdates = false;
        updateObservedLists();
    }

    private void updateObservedLists() {
        if (ignoreUpdates) {
            return;

        }

        observedIndices.setAll(selectedIndices);
        observedTiles.setAll(selectedTiles);
    }
}
