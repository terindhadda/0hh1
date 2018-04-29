package cas.se3xa3.bitsplease.view.themes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The default game theme. Standard colors and no overlays.
 * Created on 25/11/2015.
 */
public class DefaultTheme implements Theme {

    private static DefaultTheme instance;

    public static DefaultTheme getInstance() {
        if (instance == null) instance = new DefaultTheme();
        return instance;
    }

    /**
     * Seal the class.
     * @see DefaultTheme#getInstance()
     */
    private DefaultTheme(){}

    @Override
    public Color redTileColor() {
        return Color.RED;
    }

    @Override
    public Color blueTileColor() {
        return Color.CYAN.darker();
    }

    @Override
    public Color emptyTileColor() {
        return Color.DARK_GRAY;
    }

    @Override
    public Image screenOverlay() {
        return null;
    }

    @Override
    public Image tileOverlay() {
        return null;
    }
}
