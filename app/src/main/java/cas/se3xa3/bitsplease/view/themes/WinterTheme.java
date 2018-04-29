package cas.se3xa3.bitsplease.view.themes;

import javax.swing.*;
import java.awt.*;

/**
 * Created on 25/11/2015.
 */
public class WinterTheme implements Theme {
    private static WinterTheme instance;

    public static WinterTheme getInstance() {
        if (instance == null) instance = new WinterTheme();
        return instance;
    }

    private Image tileOverlay;
    private Image screenOverlay;

    /**
     * Seal the class.
     * @see DefaultTheme#getInstance()
     */
    private WinterTheme(){
        tileOverlay = new ImageIcon(Theme.class.getClassLoader().getResource("textures/IceTileOverlay.png")).getImage();
        screenOverlay = new ImageIcon(Theme.class.getClassLoader().getResource("textures/SnowScreenOverlay.gif")).getImage();
    }

    @Override
    public Color redTileColor() {
        return Color.RED;
    }

    @Override
    public Color blueTileColor() {
        return Color.GREEN.darker().darker();
    }

    @Override
    public Color emptyTileColor() {
        return Color.DARK_GRAY.brighter().brighter();
    }

    @Override
    public Image screenOverlay() {
        return screenOverlay;
    }

    @Override
    public Image tileOverlay() {
        return tileOverlay;
    }
}
