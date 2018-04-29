package cas.se3xa3.bitsplease.view;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.model.Coordinate;
import cas.se3xa3.bitsplease.view.themes.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

/**
 * Created on 11/11/2015.
 */
public class TileView extends JPanel implements PropertyChangeListener {
    private Coordinate location;
    private Color color;
    private boolean highlighted = false;
    private boolean locked = false;
    private boolean dispLocked = false;
    private Theme theme;

    public TileView(int row, int col, Theme theme) {
        this.location = new Coordinate(row, col);
        this.color = theme.emptyTileColor();
        this.theme = theme;
    }

    public int row() { return this.location.getRow(); }
    public int col() { return this.location.getColumn(); }
    public Color color() { return this.color; }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);

        //Keep it square
        int size = (this.getWidth() > this.getHeight()) ? this.getHeight() : this.getWidth();
        int gap = size / 30;
        g2d.fillRoundRect(gap, gap, size - (2 * gap), size - (2 * gap), size / 3, size / 3);

        if (theme.tileOverlay() != null) {
            graphics.setClip(new RoundRectangle2D.Double(gap, gap, size - (2 * gap), size - (2 * gap), size / 3, size / 3));
            graphics.drawImage(theme.tileOverlay(), 0, 0, size, size, this);
            graphics.setClip(null);
        }

        if (locked && dispLocked) {
            g2d.setColor(Color.BLACK);
            g2d.drawString("X", this.getWidth() / 2, this.getHeight() / 2);
        }
        if (highlighted) {
            g2d.setStroke(new BasicStroke(this.getWidth() / 30, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(0, 0, size, size, size / 3, size / 3);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case "board":
                Board board = (Board) event.getNewValue();
                switch (board.getTileAt(row(), col())) {
                    case RED: this.color = theme.redTileColor();
                        break;
                    case BLUE: this.color = theme.blueTileColor();
                        break;
                    case EMPTY: this.color = theme.emptyTileColor();
                        break;
                }
                if (board.isLocked(row(), col())) this.locked = true;
                break;
            case "dispLocked":
                this.dispLocked = (boolean) event.getNewValue();
                break;
            case "highlights":
                this.highlighted = ((Set) event.getNewValue()).contains(this.location);
                break;
            case "theme":
                Theme newTheme = (Theme) event.getNewValue();
                //Transform the colors
                if (this.color.equals(theme.blueTileColor())) this.color = newTheme.blueTileColor();
                else if (this.color.equals(theme.redTileColor())) this.color = newTheme.redTileColor();
                else this.color = newTheme.emptyTileColor();
                //Set the theme
                this.theme = newTheme;
                break;
        }
        repaint();
    }
}
