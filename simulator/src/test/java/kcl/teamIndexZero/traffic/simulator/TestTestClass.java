package kcl.teamIndexZero.traffic.simulator;

import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
public class TestTestClass {

    @Test
    public void testWithMockitoAndFest() {
        List<String> mockedList = mock(List.class);

        mockedList.add("one");
        mockedList.clear();

        verify(mockedList).add("one");
        verify(mockedList).clear();

        assertThat(mockedList).isInstanceOf(List.class);
    }
}
