package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.osm.MapParseException;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParseResult;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A map chooser dialog. Here, user can decide which one of the provided files to use as a map base for simulation.
 * <p>
 * Files are packaged within the application for now, should add an option to read from outside.
 */
public class ChooserDialog extends JFrame {

    private static final Map<String, String> filesAvailable =
            Arrays.stream(new String[][]{
                    {"Strand area", "/sampleData/strand.osm"},
                    {"Elephant and Castle strange roundabout", "/sampleData/elephant_and_castle.osm"},
                    {"Buckingham Palace area", "/sampleData/buckingham_area.osm"},
                    {"Paris, Arc de Trioumphe", "/sampleData/paris_arc_de_trioumphe_ways.osm"},
                    {"Simple in-way square", "/sampleData/rectangle.osm"},
            }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    protected static Logger_Interface LOG = Logger.getLoggerInstance(ChooserDialog.class.getSimpleName());


    /**
     * Default constructor.
     *
     * @param resultConsumer where to pass the instance of parse results
     */
    private ChooserDialog(Consumer<OsmParseResult> resultConsumer) {
        new JFrame("Please choose simulation map");
        setSize(400, 400);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Please choose in of the available presets for simulation area:"));

        filesAvailable.forEach((caption, file) -> {
            JButton button = new JButton(caption);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        OsmParseResult result = new OsmParser().parse(file, this.getClass().getResourceAsStream(file));
                        resultConsumer.accept(result);
                        ChooserDialog.this.setVisible(false);
                        ChooserDialog.this.dispose();
                    } catch (MapParseException e1) {
                        e1.printStackTrace();
                        LOG.log_Exception(e1);
                        JOptionPane.showMessageDialog(
                                ChooserDialog.this,
                                "Error loading data from packaged file. Please check logs.",
                                "Load error.",
                                JOptionPane.ERROR_MESSAGE);

                    }
                }
            });
            add(button);
        });

        pack();
        setLocationRelativeTo(null); //center window
    }

    /**
     * Factory method to show dialog for result.
     *
     * @param resultConsumer where to pass parsed result.
     */
    public static void showForOSMLoadResult(Consumer<OsmParseResult> resultConsumer) {
        new ChooserDialog(resultConsumer).setVisible(true);
    }

}
