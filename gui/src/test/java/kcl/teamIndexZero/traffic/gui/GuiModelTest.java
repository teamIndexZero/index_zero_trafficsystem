package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.gui.mvc.GuiModel;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

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
}
