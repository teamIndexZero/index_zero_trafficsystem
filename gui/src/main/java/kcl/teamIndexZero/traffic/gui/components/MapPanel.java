package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.simulator.data.geo.GeoPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;


/**
 * Main panel of the application - simulation map display. This is where the actual drawing happens. It is a 'view'
 * interface of the application, when compared with {@link MainToolbar} which controls things, that's why controller is
 * not used.
 */
public class MapPanel extends JComponent implements Consumer<BufferedImage>, MouseWheelListener, MouseListener, MouseMotionListener {
    private final GuiModel model;
    private Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    private Cursor MOVING_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    private boolean isMouseMoving = false;
    private BufferedImage image;
    private double mousePressedMetersY = 0;
    private double mousePressedMetersX = 0;

    public MapPanel(GuiModel model) {
        this.model = model;

        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        setCursor(DEFAULT_CURSOR);
    }

    /**
     * Overriding component's paint method. This is in of the out official ways to create own components - to override
     * paint method. The other in is to work with composite components, layout managers, etc - all we don't need.
     * <p>
     * This is where the actual map contents is drawn. By this moment, we already have a {@link java.awt.image.BufferedImage}
     * produced for us by {@link kcl.teamIndexZero.traffic.gui.SimulationImageProducer} and thoroughly put into the
     * {@link GuiModel}. In turn, model fires event to all subscribers, which triggers repaint method of this component.
     * <p>
     * More interestingly, this method may also be invoked not by simulation/events, but also by window/events, i.e. when
     * user resizes frame, or first time the component is due to be shown, or when it shows again after being hidden etc.
     *
     * @param g Graphics to draw component details with.
     */
    @Override
    public void paint(Graphics g) {
        if (image == null) {
            String messageNotRunning = "Simulation not running.";
            g.drawChars(messageNotRunning.toCharArray(), 0, messageNotRunning.length() - 1, 30, 30);
            return;
        }
        g.drawImage(
                image,
                0,
                0,
                image.getWidth(),
                image.getHeight(),
                null);
    }

    /**
     * Accept an image to draw on screen.
     *
     * @param bufferedImage image to display. It will draw 1:1 to original result
     */
    @Override
    public void accept(BufferedImage bufferedImage) {
        this.image = bufferedImage;
        this.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        GeoPoint centerPoint = new GeoPoint(
                model.getViewport().convertXPixelsToMeters(e.getX()),
                model.getViewport().convertYPixelsToMeters(e.getY()));

        if (e.getWheelRotation() > 0) {
            model.getViewport().zoomOut(centerPoint);
        } else {
            model.getViewport().zoomIn(centerPoint);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        isMouseMoving = true;

        mousePressedMetersX = model.getViewport().convertXPixelsToMeters(e.getX());
        mousePressedMetersY = model.getViewport().convertYPixelsToMeters(e.getY());

        setCursor(MOVING_CURSOR);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseMoving = false;
        setCursor(Cursor.getDefaultCursor());
        setCursor(DEFAULT_CURSOR);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isMouseMoving) {
            return;
        }

        double offsetMetersX = model.getViewport().convertXPixelsToMeters(e.getX()) - mousePressedMetersX;
        double offsetMetersY = model.getViewport().convertYPixelsToMeters(e.getY()) - mousePressedMetersY;

        model.getViewport().addMetersOffset(offsetMetersX, offsetMetersY);

        mousePressedMetersX = model.getViewport().convertXPixelsToMeters(e.getX());
        mousePressedMetersY = model.getViewport().convertYPixelsToMeters(e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
