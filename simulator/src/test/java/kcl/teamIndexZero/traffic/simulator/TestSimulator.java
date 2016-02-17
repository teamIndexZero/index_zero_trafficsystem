package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.simulator.data.SimulationParams;
import kcl.teamIndexZero.traffic.simulator.data.SimulationTick;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Tests for Simulator class
 */
public class TestSimulator {
    private static class TickNumberMatcher extends BaseMatcher<SimulationTick> {
        private int tickNumber;

        public TickNumberMatcher(int tickNumber) {
            this.tickNumber = tickNumber;
        }

        @Override
        public boolean matches(Object item) {
            return ((SimulationTick) item).getTickNumber() == tickNumber;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Tick number does not match");
        }
    }

    private SimulationParams params;
    private ISimulationAware simulationAwareMock;

    @Before
    public void prepareData() {
        params = new SimulationParams(LocalDateTime.now(), 10, 100);
        simulationAwareMock = mock(ISimulationAware.class);

    }

    @Test
    public void shouldSimulatorUseParamsCount() {
        // given
        Simulator simulator = new Simulator(params, Collections.singletonList(simulationAwareMock));

        // when
        simulator.start();

        // then
        verify(simulationAwareMock, times(params.durationInTicks)).tick(any());
    }

    @Test
    public void shouldSimulatorInvokeListenersInStrictOrder() {
        // given
        ISimulationAware listener2 = mock(ISimulationAware.class);
        params.durationInTicks = 1;
        Simulator simulator = new Simulator(params, Arrays.asList(simulationAwareMock, listener2));

        // when
        simulator.start();
        simulator.stop();

        // then
        InOrder inOrder = inOrder(simulationAwareMock, listener2);
        inOrder.verify(simulationAwareMock).tick(any());
        inOrder.verify(listener2).tick(any());
    }

    @Test
    public void shouldSimulatorAdvanceTickNumber() {
        // given
        Simulator simulator = new Simulator(params, Collections.singletonList(simulationAwareMock));

        // when
        simulator.start();

        // then
        InOrder inOrder = inOrder(simulationAwareMock);
        inOrder.verify(simulationAwareMock).tick(argThat(new TickNumberMatcher(0)));
        inOrder.verify(simulationAwareMock).tick(argThat(new TickNumberMatcher(1)));
        inOrder.verify(simulationAwareMock).tick(argThat(new TickNumberMatcher(98)));// making sure that 99 is called after 0
        inOrder.verify(simulationAwareMock).tick(argThat(new TickNumberMatcher(99)));
        inOrder.verifyNoMoreInteractions(); //and we're not calling that again after 99
    }

}
