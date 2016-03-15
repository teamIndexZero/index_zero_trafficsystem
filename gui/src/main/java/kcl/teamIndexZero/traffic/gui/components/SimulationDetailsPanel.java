package kcl.teamIndexZero.traffic.gui.components;

import kcl.teamIndexZero.traffic.gui.mvc.GuiController;
import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.MapObject;
import kcl.teamIndexZero.traffic.simulator.data.mapObjects.Vehicle;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
    private JLabel detailsLabel;
    private JLabel tickDetailsField = new JLabel();
    private JLabel carsCurrentlyOnScreenLabel = new JLabel();
    private JPanel infoPanel = new JPanel();
    private JPanel settingsPanel = new JPanel();
    private JPanel carsPanel = new JPanel();

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

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        setupInfoPanel();
        setupSettingsPanel();
        setupCarsPanel();

        model.addChangeListener(this);
    }

    private void setupCarsPanel() {
        carsPanel.setLayout(new BorderLayout());

        carList = new JList<>(new MapObjectListModel());
        carList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        carList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        carList.setVisibleRowCount(-1);
        carList.setCellRenderer(new OverridingListModelRenderer());

        carList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                model.setSelectedMapObject(carList.getSelectedValue());
            }
        });

        JScrollPane scrollPane = new JScrollPane(carList);
        carsPanel.add(scrollPane, BorderLayout.CENTER);
        detailsLabel = new JLabel();
        detailsLabel.setVisible(false);
        carsPanel.add(detailsLabel, BorderLayout.SOUTH);
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

        extraDelayBetweenTicksSlider.setMajorTickSpacing(20);
        extraDelayBetweenTicksSlider.setMinorTickSpacing(10);
        extraDelayBetweenTicksSlider.setPaintTicks(true);
        extraDelayBetweenTicksSlider.setPaintLabels(true);

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
            detailsLabel.setText(createDetailsText(model.getSelectedMapObject()));
            detailsLabel.setVisible(true);
        } else {
            detailsLabel.setVisible(false);
        }
    }

    private String createDetailsText(MapObject selectedMapObject) {
        String content = "";
        content += createTableRow("Name", selectedMapObject.getName());
        content += createTableRow("Road", selectedMapObject.getLane().getRoad().getName());
        if (selectedMapObject instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) selectedMapObject;
            content += createTableRow("Speed", String.format("%.2f", vehicle.getSpeedMetersPerSecond()) + " m/s");
            content += createTableRow("Accel", String.format("%.2f", vehicle.getAccelerationMetersPerSecondSecond()) + " m/s<sup>2</sup>");
        }
        return String.format("<html><table>%s</table></html>", content);
    }

    private String createTableRow(String label, String value) {
        return String.format("<tr><td>%s</td><td><b>%s</b></td></tr>", label, value);
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
    private class OverridingListModelRenderer implements ListCellRenderer<MapObject> {
        private DefaultListCellRenderer defaultOne = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends MapObject> list, MapObject value, int index, boolean isSelected, boolean cellHasFocus) {
            return defaultOne.getListCellRendererComponent(list, value.getNameAndRoad(), index, isSelected, cellHasFocus);
        }
    }
}
