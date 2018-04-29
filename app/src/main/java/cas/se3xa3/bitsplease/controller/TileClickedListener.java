package cas.se3xa3.bitsplease.controller;

/**
 * Created on 11/11/2015.
 */
@FunctionalInterface
public interface TileClickedListener {
    /**
     * Called when a tile is clicked.
     * @param row the row clicked.
     * @param column the tile clicked.
     */
    void onTileClick(int row, int column);
}
