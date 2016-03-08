package kcl.teamIndexZero.traffic.gui.mvc;

import kcl.teamIndexZero.traffic.simulator.Simulator;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Tests for Gui Controller.
 */
public class GuiControllerTest {

    @Test
    public void shouldControllerStartSimulationInDifferentThread() throws InterruptedException {
        GuiController controller = null;
        try {
            // calling code (junit) main thread
            Thread currentThread = Thread.currentThread();
            Simulator simulatorMock = mock(Simulator.class);
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    //IMPORTANT! This is where the actual verification happens that we are not on UI thread.
                    assertThat(Thread.currentThread()).isNotEqualTo(currentThread);
                    return null;
                }
            }).when(simulatorMock).start();

            controller = new GuiController(mock(GuiModel.class), () -> simulatorMock);
            controller.start();
        } finally {
            // restart simulation if any
            if (controller != null) {
                controller.restart();

                // wait for the bg thread to terminate.
                if (controller.getSimulatorThread() != null) {
                    controller.getSimulatorThread().join();
                }
            }
        }
    }


}
