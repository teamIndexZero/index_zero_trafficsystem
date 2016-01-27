package kcl.teamIndexZero.traffic.simulator;

import kcl.teamIndexZero.traffic.simulator.SomeNiceClass;
import org.junit.Assert;
import org.junit.Test;
import static org.fest.assertions.api.Assertions.*;


import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
public class TestTestClass {

    @Test
    public void testShouldBeTrue() {
        Assert.assertTrue((new SomeNiceClass().hi));
    }

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
