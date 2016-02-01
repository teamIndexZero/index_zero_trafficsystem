package kcl.teamIndexZero.traffic.gui;

import kcl.teamIndexZero.traffic.log.LoggerInterfaceMock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class GuiTest {

    @Test
    public void iShoudlBeNotFailing() {
        Assert.assertTrue(true);
    }


    @Test
    public void shouldMocksWork() {
        LoggerInterfaceMock mock = Mockito.mock(LoggerInterfaceMock.class);
        mock.log(1, 2, 3);
        Mockito.verify(mock).log(1, 2, 3);
    }
}
