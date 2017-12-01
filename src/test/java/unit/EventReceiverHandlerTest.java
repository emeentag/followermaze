package unit;

import org.junit.Assert;
import org.junit.Test;

import server.entity.Event;
import server.event.type.EventType;
import utils.UtilFactory;

/**
 * EventReceiverHandlerTest
 */
public class EventReceiverHandlerTest {

  @Test
  public void test01_marshallFollowEvent() {
    Event event = UtilFactory.getUtil().marshallEvent("666\\|F\\|60\\|50");

    Assert.assertEquals("666\\|F\\|60\\|50", event.getPayload());
    Assert.assertEquals(Long.valueOf(666), event.getSequence());
    Assert.assertEquals(EventType.FOLLOW.name(), event.getEventType().name());
    Assert.assertEquals(Long.valueOf(60), event.getFromUser());
    Assert.assertEquals(Long.valueOf(50), event.getToUser());
  }

  @Test
  public void test02_marshallUnFollowEvent() {
    Event event = UtilFactory.getUtil().marshallEvent("1\\|U\\|12\\|9");

    Assert.assertEquals("1\\|U\\|12\\|9", event.getPayload());
    Assert.assertEquals(Long.valueOf(1), event.getSequence());
    Assert.assertEquals(EventType.UNFOLLOW.name(), event.getEventType().name());
    Assert.assertEquals(Long.valueOf(12), event.getFromUser());
    Assert.assertEquals(Long.valueOf(9), event.getToUser());
  }

  @Test
  public void test03_marshallBroadcastEvent() {
    Event event = UtilFactory.getUtil().marshallEvent("542532\\|B");

    Assert.assertEquals("542532\\|B", event.getPayload());
    Assert.assertEquals(Long.valueOf(542532), event.getSequence());
    Assert.assertEquals(EventType.BROADCAST.name(), event.getEventType().name());
    Assert.assertEquals(Long.valueOf(0), event.getFromUser());
    Assert.assertEquals(Long.valueOf(0), event.getToUser());
  }

  @Test
  public void test04_marshallPrivateEvent() {
    Event event = UtilFactory.getUtil().marshallEvent("43\\|P\\|32\\|56");

    Assert.assertEquals("43\\|P\\|32\\|56", event.getPayload());
    Assert.assertEquals(Long.valueOf(43), event.getSequence());
    Assert.assertEquals(EventType.PRIVATE.name(), event.getEventType().name());
    Assert.assertEquals(Long.valueOf(32), event.getFromUser());
    Assert.assertEquals(Long.valueOf(56), event.getToUser());
  }

  @Test
  public void test05_marshallStatusEvent() {
    Event event = UtilFactory.getUtil().marshallEvent("634\\|S\\|32");

    Assert.assertEquals("634\\|S\\|32", event.getPayload());
    Assert.assertEquals(Long.valueOf(634), event.getSequence());
    Assert.assertEquals(EventType.STATUS.name(), event.getEventType().name());
    Assert.assertEquals(Long.valueOf(32), event.getFromUser());
    Assert.assertEquals(Long.valueOf(0), event.getToUser());
  }
}