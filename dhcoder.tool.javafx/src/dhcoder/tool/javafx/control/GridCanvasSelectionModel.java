package dhcoder.tool.javafx.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import java.util.Objects;

/**
 * A selection model for selected tiles in a {@link GridCanvas}
 */
public final class GridCanvasSelectionModel extends MultipleSelectionModel<GridCanvas.GridCoord> {

    private final GridCanvas gridCanvas;
    private ObservableList<Integer> selectedIndices = FXCollections.observableArrayList();
    private ObservableList<GridCanvas.GridCoord> selectedTiles = FXCollections.observableArrayList();

    public GridCanvasSelectionModel(final GridCanvas gridCanvas) {
        this.gridCanvas = gridCanvas;
    }

    @Override
    public void clearAndSelect(final int index) {
        selectedTiles.clear();
        select(index);
    }

    @Override
    public void select(final int index) {
        if (getSelectedIndex() == index) {return;}

        setSelectedIndex(index);
        GridCanvas.GridCoord gridCoord = gridCanvas.getCoord(index);
        setSelectedItem(gridCoord);

        if (!selectedIndices.contains(index)) {
            selectedIndices.add(index);
            selectedTiles.add(gridCoord);
        }
    }

    @Override
    public void select(final GridCanvas.GridCoord gridCoord) {
        if (Objects.equals(getSelectedItem(), gridCoord)) {return;}

        setSelectedItem(gridCoord);
        int index = gridCanvas.getIndex(gridCoord);
        setSelectedIndex(index);

        if (!selectedTiles.contains(gridCoord)) {
            selectedTiles.add(gridCoord);
            selectedIndices.add(index);
        }
    }

    @Override
    public void clearSelection(final int index) {

        if (selectedIndices.contains(index)) {
            selectedIndices.remove(index);
            selectedTiles.remove(gridCanvas.getCoord(index));

            if (getSelectedIndex() == index) {
                setSelectedIndex(-1);
                setSelectedItem(null);
            }
        }
    }

    @Override
    public void clearSelection() {
        selectedIndices.clear();
        selectedTiles.clear();
    }

    @Override
    public boolean isSelected(final int index) {
        return selectedTiles.contains(gridCanvas.getCoord(index));
    }

    @Override
    public boolean isEmpty() {
        return selectedTiles.isEmpty();
    }

    @Override
    public void selectPrevious() {
        if (getSelectedIndex() >= 0) {
            select(gridCanvas.getIndexBefore(getSelectedIndex()));
        }
    }

    @Override
    public void selectNext() {
        if (getSelectedIndex() >= 0) {
            select(gridCanvas.getIndexAfter(getSelectedIndex()));
        }
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return selectedIndices;
    }

    @Override
    public ObservableList<GridCanvas.GridCoord> getSelectedItems() {
        return selectedTiles;
    }

    @Override
    public void selectIndices(final int index, final int... indices) {
        for (int tailIndex : indices) {
            select(tailIndex);
        }

        select(index);
    }

    @Override
    public void selectAll() {
        int[] tailIndices = new int[gridCanvas.getLastIndex()];
        for (int i = 1; i <= gridCanvas.getLastIndex(); i++) {
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
        select(gridCanvas.getLastIndex());
    }
}
