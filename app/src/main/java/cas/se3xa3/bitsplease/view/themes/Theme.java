package cas.se3xa3.bitsplease.view.themes;

import java.awt.*;

/**
 * Created on 25/11/2015.
 */
public interface Theme {

    /**
     * @return the color of the red tiles
     */
    Color redTileColor();

    /**
     * @return the color of the blue tiles
     */
    Color blueTileColor();

    /**
     * @return the color of the empty tiles
     */
    Color emptyTileColor();

    /**
     * @return an image to draw on top of the screen. Can be null.
     */
    Image screenOverlay();

    /**
     * @return an image to draw on top of each tile. Can be null.
     */
    Image tileOverlay();
}
