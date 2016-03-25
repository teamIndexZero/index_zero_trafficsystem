package kcl.teamIndexZero.traffic.gui.mvc;

import kcl.teamIndexZero.traffic.simulator.data.SimulationMap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * Testing various aspects of Gui Model
 */
public class GuiModelTest {
    GuiModel model;

    @Before
    public void setup() {
        model = new GuiModel(mock(SimulationMap.class));
    }

    @Test
    @Ignore
    //TODO have to think carefully what are the implications and how do we actually fix the test.
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
}
