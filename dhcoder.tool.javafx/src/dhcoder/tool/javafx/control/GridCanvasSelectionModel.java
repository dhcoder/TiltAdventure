package dhcoder.tool.javafx.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A selection model for selected tiles in a {@link GridCanvas}
 */
public final class GridCanvasSelectionModel extends MultipleSelectionModel<GridCanvas.GridCoord> {

    private final GridCanvas gridCanvas;
    private List<Integer> selectedIndices = new ArrayList<>();
    private List<GridCanvas.GridCoord> selectedTiles = new ArrayList<>();
    private ObservableList<Integer> observedIndices = FXCollections.observableArrayList();
    private ObservableList<GridCanvas.GridCoord> observedTiles = FXCollections.observableArrayList();

    private boolean isUpdating;

    public GridCanvasSelectionModel(final GridCanvas gridCanvas) {
        this.gridCanvas = gridCanvas;
    }

    @Override
    public void clearAndSelect(final int index) {
        if (getSelectedIndex() == index) {
            return;
        }

        selectedIndices.clear();
        selectedTiles.clear();
        select(index);
    }

    @Override
    public void select(final int index) {
        select(gridCanvas.getCoord(index));
    }

    @Override
    public void select(final GridCanvas.GridCoord gridCoord) {
        if (Objects.equals(getSelectedItem(), gridCoord)) {return;}

        setSelectedItem(gridCoord);
        int index = gridCanvas.getIndex(gridCoord);
        setSelectedIndex(index);

        if (getSelectionMode() == SelectionMode.SINGLE) {
            selectedTiles.clear();
            selectedIndices.clear();
        }

        if (!selectedTiles.contains(gridCoord)) {
            selectedTiles.add(gridCoord);
            selectedIndices.add(index);
            updateObservedLists();
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

            updateObservedLists();
        }
    }

    @Override
    public void clearSelection() {
        selectedIndices.clear();
        selectedTiles.clear();
        updateObservedLists();
    }

    @Override
    public boolean isSelected(final int index) {
        return selectedIndices.contains(index);
    }

    @Override
    public boolean isEmpty() {
        return selectedIndices.isEmpty();
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

    public boolean isAnchor(final int index) {
        return selectedIndices.size() > 0 && index == 0;
    }

    public boolean isAnchor(final GridCanvas.GridCoord gridCoord) {
        return selectedTiles.size() > 0 && selectedTiles.get(0).equals(gridCoord);
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return observedIndices;
    }

    @Override
    public ObservableList<GridCanvas.GridCoord> getSelectedItems() {
        return observedTiles;
    }

    @Override
    public void selectIndices(final int index, final int... indices) {
        isUpdating = true;
        for (int tailIndex : indices) {
            select(tailIndex);
        }

        select(index);
        isUpdating = false;

        updateObservedLists();
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

    private void updateObservedLists() {
        if (isUpdating) {
            return;

        }

        observedIndices.setAll(selectedIndices);
        observedTiles.setAll(selectedTiles);
    }
}
