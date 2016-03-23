package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.simulator.data.features.*;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapObject;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Settings and details panel.
 * <p>
 * It has several panels inside:
 * <p>
 * - Cars panel: list of all vehicles currently in operation
 * - Settings panel: allows to alter simulation/display settings
 * - Info panel: shows generic stats about simulation
 */
public class SimulationDetailsPanel extends JPanel implements GuiModel.ChangeListener {

    private final GuiModel model;
    private final GuiController controller;
    private JList<MapObject> carList;
    private JLabel carDetailsLabel;
    private JLabel featureDetailsLabel;
    private JLabel tickDetailsField = new JLabel();
    private JLabel carsCurrentlyOnScreenLabel = new JLabel();
    private JPanel infoPanel = new JPanel();
    private JPanel settingsPanel = new JPanel();
    private JPanel carsPanel = new JPanel();
    private JPanel featuresPanel = new JPanel();
    private JList<Feature> featureList;

    /**
     * Constructor.
     *
     * @param model      model
     * @param controller controller
     */
    public SimulationDetailsPanel(GuiModel model, GuiController controller) {
        this.model = model;
        this.controller = controller;
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Info", infoPanel);
        tabs.addTab("Cars", carsPanel);
        tabs.addTab("Settings", settingsPanel);
        tabs.addTab("Map", featuresPanel);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        setupInfoPanel();
        setupSettingsPanel();
        setupCarsPanel();
        setupFeaturesPanel();

        model.addChangeListener(this);
    }

    public static String createTableRow(String label, String value) {
        return String.format("<tr><td>%s</td><td><b>%s</b></td></tr>", label, value);
    }

    private void setupFeaturesPanel() {
        featuresPanel.setLayout(new BorderLayout());

        Collection<Feature> features = model.getMap().getMapFeatures().values()
                .stream()
                .filter(f -> !(f instanceof Lane))
                .collect(Collectors.toList());
        featureList = new JList<>(features.toArray(new Feature[features.size()]));

        featureList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        featureList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        featureList.setCellRenderer(new OverridingListModelRenderer<>(Feature::toHTMLString));
        featureList.setVisibleRowCount(-1);

        featureList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                model.setSelectedFeature(featureList.getSelectedValue());
            }
        });

        JScrollPane scrollPane = new JScrollPane(featureList);
        featuresPanel.add(scrollPane, BorderLayout.CENTER);
        featureDetailsLabel = new JLabel();
        featureDetailsLabel.setVisible(false);
        featuresPanel.add(featureDetailsLabel, BorderLayout.SOUTH);
    }

    private void setupCarsPanel() {
        carsPanel.setLayout(new BorderLayout());

        carList = new JList<>(new MapObjectListModel());
        carList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        carList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        carList.setVisibleRowCount(-1);
        carList.setCellRenderer(new OverridingListModelRenderer<>(MapObject::getNameAndRoad));

        carList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                model.setSelectedMapObject(carList.getSelectedValue());
            }
        });

        JScrollPane scrollPane = new JScrollPane(carList);
        carsPanel.add(scrollPane, BorderLayout.CENTER);
        carDetailsLabel = new JLabel();
        carDetailsLabel.setVisible(false);
        carsPanel.add(carDetailsLabel, BorderLayout.SOUTH);
    }

    private void setupSettingsPanel() {
        JSlider extraDelayBetweenTicksSlider = new JSlider(
                JSlider.HORIZONTAL,
                GuiModel.DELAY_MINIMAL,
                GuiModel.DELAY_MAXIMAL,
                model.getDelayBetweenTicks());

        extraDelayBetweenTicksSlider.addChangeListener(event -> {
            model.setDelayBetweenTicks(extraDelayBetweenTicksSlider.getValue());
        });

        extraDelayBetweenTicksSlider.setMajorTickSpacing(200);
        extraDelayBetweenTicksSlider.setMinorTickSpacing(50);
        extraDelayBetweenTicksSlider.setPaintTicks(true);
        extraDelayBetweenTicksSlider.setPaintLabels(true);

        JToggleButton showTrafficGenerators = new JCheckBox("Show Traffic Generators", model.isShowTrafficGenerators());
        showTrafficGenerators.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setShowTrafficGenerators(showTrafficGenerators.isSelected());
            }
        });

        JToggleButton debugRoads = new JCheckBox("Debug roads", model.debugRoads());
        debugRoads.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setDebugRoads(debugRoads.isSelected());
            }
        });

        JToggleButton showJunctionsCheckbox = new JCheckBox("Show junctions as circles", model.isShowJunctions());
        showJunctionsCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setShowJunctions(showJunctionsCheckbox.isSelected());
            }
        });

        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel delayLabel = new JLabel("Extra delay between simulation ticks");

        debugRoads.setAlignmentX(Component.LEFT_ALIGNMENT);
        showJunctionsCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        delayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        extraDelayBetweenTicksSlider.setAlignmentX(Component.LEFT_ALIGNMENT);

        settingsPanel.add(debugRoads);
        settingsPanel.add(showTrafficGenerators);
        settingsPanel.add(showJunctionsCheckbox);
        settingsPanel.add(delayLabel);
        settingsPanel.add(extraDelayBetweenTicksSlider);
    }

    private void setupInfoPanel() {
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        infoPanel.add(new JLabel("Following is the information about current simulation:\n\n"));
        infoPanel.add(tickDetailsField);
        infoPanel.add(carsCurrentlyOnScreenLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onModelChanged() {
        // updating tick details
        if (model.getTick() != null) {
            tickDetailsField.setText("Simulated Time: " + model.getTick().toString());
        } else {
            tickDetailsField.setText("<NONE>");
        }
        carsCurrentlyOnScreenLabel.setText("Cars currently on screen: " + model.getMap().getObjectsOnSurface().size());
        if (model.getSelectedMapObject() != null) {
            carList.setSelectedValue(model.getSelectedMapObject(), true);
            carDetailsLabel.setText(createDetailsText(model.getSelectedMapObject()));
            carDetailsLabel.setVisible(true);
        } else {
            carDetailsLabel.setVisible(false);
        }

        if (model.getSelectedFeature() != null) {
            featureDetailsLabel.setText(createFeatureDetailsText(model.getSelectedFeature()));
            featureDetailsLabel.setVisible(true);
            featureList.setSelectedValue(model.getSelectedFeature(), true);
        } else {
            featureDetailsLabel.setVisible(false);
        }
    }

    private String createDetailsText(MapObject selectedMapObject) {
        String content = "";
        content += createTableRow("Name", selectedMapObject.getName());
        content += createTableRow("Road", selectedMapObject.getLane().getRoad().getName());
        if (selectedMapObject instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) selectedMapObject;
            content += createTableRow("Speed", String.format("%.2f", vehicle.getSpeedKph()) + " kph");
            content += createTableRow("Accel", String.format("%.2f", vehicle.getAccelerationKphH()) + " kph/s");
        }
        return String.format("<html><table>%s</table></html>", content);
    }

    private String createFeatureDetailsText(Feature feature) {
        String content = "";
        content += createTableRow("ID", feature.getID().toString());

        if (feature instanceof Road) {
            Road r = (Road) feature;
            content += createTableRow("Name", r.getName());
            content += createTableRow("Fwd  lanes", String.valueOf(r.getForwardLaneCount()));
            content += createTableRow("Back lanes", String.valueOf(r.getBackwardLaneCount()));
            content += createTableRow("Layer", String.valueOf(r.getLayer()));
            content += createTableRow("Length", String.valueOf(r.getRoadLength()));
        }
        if (feature instanceof Junction) {
            Junction j = (Junction) feature;
            content += createTableRow("Connections", j.getConnectedFeatures()
                    .stream()
                    .map(feature1 -> {
                        if (feature1 instanceof Road) {
                            return ((Road) feature1).getName();
                        }
                        return feature1.toString();
                    })
                    .reduce((s1, s2) -> s1 + "<br/>" + s2)
                    .orElse("none"));
            content += createTableRow("Cars passed", String.valueOf(j.getUsage()));
        }
        if (feature instanceof TrafficGenerator) {
            TrafficGenerator j = (TrafficGenerator) feature;
            content += createTableRow("Cars sent", String.valueOf(j.getThisGeneratorCreationCounter()));
            content += createTableRow("Cars received", String.valueOf(j.getReceiptCounter()));
        }

        return String.format("<html><table>%s</table></html>", content);
    }

    /**
     * List model for an object observing our model.
     */
    private class MapObjectListModel extends AbstractListModel<MapObject> implements GuiModel.ChangeListener {
        private List<MapObject> mapObjects = new ArrayList<>();

        public MapObjectListModel() {
            model.addChangeListener(this);
        }

        @Override
        public int getSize() {
            return mapObjects.size();
        }

        @Override
        public MapObject getElementAt(int index) {
            return mapObjects.get(index);
        }

        @Override
        public void onModelChanged() {
            mapObjects.clear();
            mapObjects.addAll(model.getMap().getObjectsOnSurface());
            fireContentsChanged(this, 0, getSize() - 1);
        }
    }

    /**
     * A specific renderer for list which allows us to show something other than toString.
     */
    private class OverridingListModelRenderer<T> implements ListCellRenderer<T> {
        private Function<T, String> toStringFunction;
        private DefaultListCellRenderer defaultOne = new DefaultListCellRenderer();

        public OverridingListModelRenderer(Function<T, String> toStringFunction) {
            this.toStringFunction = toStringFunction;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
            return defaultOne.getListCellRendererComponent(list, toStringFunction.apply(value), index, isSelected, cellHasFocus);
        }
    }
}
