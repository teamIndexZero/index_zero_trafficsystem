package kcl.teamIndexZero.traffic.gui.mvc;

import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Testing various aspects of Gui Model
 */
public class GuiModelTest {
    GuiModel model;

    @Before
    public void setup() {
        model = new GuiModel();
    }

    @Test
    public void shouldAllSettersFireEvent() {
        // given
        GuiModel.ChangeListener mockListener = mock(GuiModel.ChangeListener.class);
        model.addChangeListener(mockListener);

        // when
        // then
        Method[] allMethods = GuiModel.class.getDeclaredMethods();
        Arrays.asList(allMethods)
                .stream()
                .filter(m -> m.getName().startsWith("set"))
                .forEach(m1 -> {
                    try {
                        Object[] parametersAsMocks = new Object[m1.getParameterCount()];
                        Arrays.fill(parametersAsMocks, null);
                        m1.invoke(model, parametersAsMocks);
                        verify(mockListener).onModelChanged();
                        reset(mockListener);
                    } catch (Exception e) {
                        throw new RuntimeException("Error invoking method " + m1.getName(), e);
                    }
                });
    }

    @Test
    public void shouldModelReset() {
        //given
        GuiModel oldModel = new GuiModel();
        model.setLastSimulationTickAndImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), new SimulationTick(0, LocalDateTime.now(), 10));
        model.setParams(new SimulationParams(LocalDateTime.now(), 20, 20));
        model.setStatus(GuiModel.SimulationStatus.OFF);

        // when
        model.reset();

        // then
        assertThat(model).isEqualTo(oldModel);
    }
}
