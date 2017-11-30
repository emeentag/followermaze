package unit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import utils.UtilFactory;

/**
 * UserRegisterHandlerTest
 */
public class UserRegisterHandlerTest {

  @Before
  public void mockHandler() {

  }

  @Test
  public void test01ExtractId() {
    StringBuffer sBuf = new StringBuffer();
    sBuf.append("2932").append("\n").append("\r");

    Long id = UtilFactory.getUtil().extractId("2932\n\r");
    Assert.assertEquals(2932, id.longValue());

    id = UtilFactory.getUtil().extractId("2932\r\n");
    Assert.assertEquals(2932, id.longValue());
  }
}