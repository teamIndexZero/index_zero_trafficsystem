package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.log.Logger;
import kcl.teamIndexZero.traffic.log.Logger_Interface;
import kcl.teamIndexZero.traffic.simulator.osm.MapParseException;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParseResult;
import kcl.teamIndexZero.traffic.simulator.osm.OsmParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * A map chooser dialog. Here, user can decide which one of the provided files to use as a map base for simulation.
 * <p>
 * Files are packaged within the application for now, should add an option to read from outside.
 */
public class ChooserDialog extends JFrame {

    private static final Map<String, String> MICRO_MODEL_FILES =
            Arrays.stream(new String[][]{
                    {"Strand area", "/sampleData/micro/strand.osm.xml"},
                    {"Elephant and Castle strange roundabout", "/sampleData/micro/elephant_and_castle.osm.xml"},
                    {"Buckingham Palace area", "/sampleData/micro/buckingham_area.osm.xml"},
                    {"Paris, Arc de Triomphe", "/sampleData/micro/paris_arc_de_triomphe_ways.osm.xml"},
                    {"Manhattan/Battery Park", "/sampleData/micro/Manhattan_Battery_Park_Junction.osm.xml"}
            }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    private static final Map<String, String> SYNTHETIC_MODEL_FILES =
            Arrays.stream(new String[][]{
                    {"Simple one-way square", "/sampleData/synthetic/rectangle.osm.xml"},
                    {"Straight road with 6 lanes", "/sampleData/synthetic/3_by_3_lanesRoad.osm.xml"},
            }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    private static final Map<String, String> MACRO_MODEL_FILES =
            Arrays.stream(new String[][]{
                    {"Whole Greater London", "/sampleData/macro/greater-london-car-ways.osm.gz"},
                    {"Whole Manhattan", "/sampleData/macro/Manhattan_Roads.osm.gz"}
            }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    protected static Logger_Interface LOG = Logger.getLoggerInstance(ChooserDialog.class.getSimpleName());
    private Consumer<OsmParseResult> resultConsumer;


    /**
     * Default constructor.
     *
     * @param resultConsumer where to pass the instance of parse results
     */
    private ChooserDialog(Consumer<OsmParseResult> resultConsumer) {
        this.resultConsumer = resultConsumer;
        new JFrame("Please choose simulation map");
        setSize(400, 400);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Load map file from filesystem. Be sure to read through details on preparing file first!"));

        JButton openFreeButton = new JButton("Open .osm file from filesystem ");
        openFreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();

                int returnVal = fc.showOpenDialog(ChooserDialog.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        openOsmFile(file.getName(), new FileInputStream(fc.getSelectedFile()));
                    } catch (FileNotFoundException e1) {
                        LOG.log_Exception(e1);
                    }
                }
            }
        });
        add(openFreeButton);
        add(new JSeparator());
        add(new JLabel("Choose one of Micro-model samples:"));

        MICRO_MODEL_FILES.forEach((caption, file) -> {
            JButton button = new JButton(caption);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openFile(file, caption);
                }
            });
            add(button);
        });

        add(new JSeparator());
        JLabel slowWarningLabel = new JLabel("<html>Choose one of Macro-model samples:<font color='red'>(WARNING! MAY BE SLOW!)</font></html>");
        add(slowWarningLabel);

        MACRO_MODEL_FILES.forEach((caption, file) -> {
            JButton button = new JButton(caption);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openFile(file, caption);
                }
            });
            add(button);
        });

        add(new JSeparator());
        add(new JLabel("(WARNING! MAY BE SLOW!) Choose one of Macro-model samples:"));

        SYNTHETIC_MODEL_FILES.forEach((caption, file) -> {
            JButton button = new JButton(caption);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openFile(file, caption);
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

    public void openFile(String file, String caption) {
        try {
            InputStream stream;
            if (file.endsWith(".gz")) {
                stream = new GZIPInputStream(ChooserDialog.class.getResourceAsStream(file));
            } else {
                stream = ChooserDialog.class.getResourceAsStream(file);
            }
            openOsmFile(caption, stream);
        } catch (IOException e1) {
            throw new IllegalStateException(e1);
        }
    }

    public void openOsmFile(String name, InputStream fileToOpen) {
        try {
            OsmParseResult result = new OsmParser().parse(name, fileToOpen);
            ChooserDialog.this.resultConsumer.accept(result);
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

}
