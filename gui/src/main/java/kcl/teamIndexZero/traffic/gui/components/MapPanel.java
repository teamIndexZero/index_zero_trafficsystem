package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * Main panel of the application - simulation map display. This is where the actual drawing happens. It is a 'view'
 * interface of the application, when compared with {@link MainToolbar} which controls things, that's why controller is
 * not used.
 */
public class MapPanel extends JComponent implements Consumer<BufferedImage> {
    private final GuiModel model;
    private BufferedImage image;

    public MapPanel(GuiModel model) {
        this.model = model;
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

    @Override
    public void accept(BufferedImage bufferedImage) {
        this.image = bufferedImage;
        this.repaint();
    }
}
