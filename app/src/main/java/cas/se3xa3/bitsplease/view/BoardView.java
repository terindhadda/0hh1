package cas.se3xa3.bitsplease.view;

import cas.se3xa3.bitsplease.controller.TileClickedListener;
import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.Coordinate;
import cas.se3xa3.bitsplease.model.Tile;
import cas.se3xa3.bitsplease.view.themes.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created on 11/11/2015.
 */
public class BoardView extends JPanel implements PropertyChangeListener {
    private Board board;
    private boolean dispLocked = false;
    private Set<Coordinate> highlights;
    private Set<TileClickedListener> listeners;
    private Theme theme;

    public BoardView(Board board, Theme theme) {
        this.setSize(400, 400);
        this.board = board;
        this.theme = theme;
        this.listeners = new HashSet<>();
        this.highlights = new HashSet<>();
        int size = board.getSize();
        this.setLayout(new GridLayout(size, size));
        IntStream.range(0, size * size).forEach(coordData -> {
            final int row = coordData / size;
            final int col = coordData % size;
            TileView view = new TileView(row, col, theme);
            view.setColor(getColorFor(board.getTileAt(row, col)));
            add(view);
            addPropertyChangeListener("board", view);
            addPropertyChangeListener("dispLocked", view);
            addPropertyChangeListener("highlights", view);
            addPropertyChangeListener("theme", view);
            view.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    listeners.forEach(listener -> listener.onTileClick(row, col));
                }
            });
        });
        update();
    }

    private Color getColorFor(Tile tile) {
        switch (tile) {
            case RED: return theme.redTileColor();
            case BLUE: return theme.blueTileColor();
            default: return theme.emptyTileColor();
        }
    }

    /**
     * Ask this view to reevaluate the model it represents.
     */
    public void update() {
        firePropertyChange("board", null, board);
        repaint();
    }

    /**
     * Clear all highlights from this board.
     */
    public void clearHighlights() {
        if (!highlights.isEmpty()) {
            this.highlights.clear();
            firePropertyChange("highlights", null, highlights);
        }
    }

    /**
     * Set the highlight of a specific tile.
     * @param row the index of the row of the tile to highlight.
     * @param col the index of the column of the tile to highlight.
     * @param highlight true to highlight the tile, false otherwise.
     */
    public void highlight(int row, int col, boolean highlight) {
        this.highlights.add(new Coordinate(row, col));
        firePropertyChange("highlights", null, highlights);
    }

    /**
     * Set the coordinates of the tiles to highlight.
     * @param coords the coordinates to highlight.
     */
    public void setHighlights(Set<Coordinate> coords) {
        firePropertyChange("highlights", highlights, coords);
        this.highlights = coords;
    }

    @Override
    public Dimension getPreferredSize() {
        //Force a square
        return this.getWidth() > this.getHeight() ?
                new Dimension(this.getHeight(), this.getHeight()) : new Dimension(this.getWidth(), this.getWidth());
    }

    /**
     * Add a {@link TileClickedListener} to this board.
     * @param listener the listener to be invoked when a tile on the board is clicked.
     */
    public void addTileClickedListener(TileClickedListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove a previously added {@link TileClickedListener}.
     * @param listener the listener to be removed.
     */
    public void removeTileClickedListener(TileClickedListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void setBackground(Color background) {
        super.setBackground(background);
        Arrays.stream(this.getComponents()).forEach(comp -> comp.setBackground(background));
    }

    /**
     * Set the {@code dispLocked} property of this board.
     * @param displayLocked true to visual mark locked tiles.
     */
    public void setDisplayLocked(boolean displayLocked) {
        firePropertyChange("dispLocked", this.dispLocked, displayLocked);
        this.dispLocked = displayLocked;
    }

    /**
     * Toggle the value of the {@code dispLocked} property of this board.<br>
     * This is equivalent to {@code dispLocked = !dispLocked}.
     */
    public void toggleDisplayLocked() {
        if (this.dispLocked) setDisplayLocked(false);
        else                 setDisplayLocked(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("theme"))
            this.theme = ((Theme) propertyChangeEvent.getNewValue());
        //Propagate
        Arrays.stream(getPropertyChangeListeners(propertyChangeEvent.getPropertyName()))
                .forEach(listener -> listener.propertyChange(propertyChangeEvent));
    }
}
