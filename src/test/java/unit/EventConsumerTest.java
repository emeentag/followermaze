package unit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import server.consumer.EventConsumer;
import server.entity.Event;
import server.entity.User;
import utils.Util;
import utils.UtilFactory;

/**
 * EventConsumer
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventConsumerTest {

  private ConcurrentHashMap<Long, User> userMap;

  @Before
  public void update() {
    this.userMap = new ConcurrentHashMap<>();

    User user1 = new User(60L, null, new ConcurrentLinkedQueue<>());
    User user2 = new User(50L, null, new ConcurrentLinkedQueue<>());
    User user3 = new User(12L, null, new ConcurrentLinkedQueue<>());
    User user4 = new User(9L, null, new ConcurrentLinkedQueue<>());
    User user5 = new User(32L, null, new ConcurrentLinkedQueue<>());
    User user6 = new User(56L, null, new ConcurrentLinkedQueue<>());

    this.userMap.put(60L, user1);
    this.userMap.put(50L, user2);
    this.userMap.put(12L, user3);
    this.userMap.put(9L, user4);
    this.userMap.put(32L, user5);
    this.userMap.put(56L, user6);
  }

  @Test
  public void test01_consumeFollowEvent() {
    User user1 = this.userMap.get(60L);
    User user2 = this.userMap.get(50L);
    User user3 = this.userMap.get(12L);
    User user4 = new User(122L, null, new ConcurrentLinkedQueue<>());
    User user5 = new User(123L, null, new ConcurrentLinkedQueue<>());

    // Registered users.
    EventConsumer eventConsumer = new EventConsumer(userMap);
    Util util = UtilFactory.getUtil();

    Event event = util.marshallEvent("1|F|60|12");
    eventConsumer.consumeEvent(event);
    event = util.marshallEvent("2|F|50|12");
    eventConsumer.consumeEvent(event);

    // Not registered users.
    event = util.marshallEvent("3|F|50|122");
    eventConsumer.consumeEvent(event);
    event = util.marshallEvent("4|F|123|122");
    eventConsumer.consumeEvent(event);
    event = util.marshallEvent("5|F|122|123");
    eventConsumer.consumeEvent(event);

    Assert.assertEquals(2, user3.getFollowers().size());
    Assert.assertTrue(user3.getFollowers().contains(user1));
    Assert.assertTrue(user3.getFollowers().contains(user2));

    Assert.assertEquals(0, user4.getFollowers().size());
    Assert.assertEquals(0, user5.getFollowers().size());
    Assert.assertFalse(user4.getFollowers().contains(user2));
    Assert.assertFalse(user4.getFollowers().contains(user5));
    Assert.assertFalse(user5.getFollowers().contains(user4));
  }

  @Test
  public void test02_consumeUnFollowEvent() {
    User user1 = this.userMap.get(60L);
    User user2 = this.userMap.get(50L);
    User user3 = this.userMap.get(12L);
    User user4 = new User(122L, null, new ConcurrentLinkedQueue<>());
    User user5 = new User(123L, null, new ConcurrentLinkedQueue<>());

    // Create followers.
    user3.getFollowers().add(user1);
    user3.getFollowers().add(user2);

    user4.getFollowers().add(user2);
    user4.getFollowers().add(user5);
    user5.getFollowers().add(user4);

    // Registered users.
    EventConsumer eventConsumer = new EventConsumer(userMap);
    Util util = UtilFactory.getUtil();

    Event event = util.marshallEvent("1|U|60|12");
    eventConsumer.consumeEvent(event);
    event = util.marshallEvent("2|U|50|12");
    eventConsumer.consumeEvent(event);

    // Not registered users.
    event = util.marshallEvent("3|U|50|122");
    eventConsumer.consumeEvent(event);
    event = util.marshallEvent("4|U|123|122");
    eventConsumer.consumeEvent(event);
    event = util.marshallEvent("5|U|122|123");
    eventConsumer.consumeEvent(event);

    Assert.assertEquals(0, user3.getFollowers().size());
    Assert.assertFalse(user3.getFollowers().contains(user1));
    Assert.assertFalse(user3.getFollowers().contains(user2));

    Assert.assertEquals(2, user4.getFollowers().size());
    Assert.assertEquals(1, user5.getFollowers().size());
    Assert.assertTrue(user4.getFollowers().contains(user2));
    Assert.assertTrue(user4.getFollowers().contains(user5));
    Assert.assertTrue(user5.getFollowers().contains(user4));
  }

  @Test
  public void test03_consumeUnBroadcastEvent() {

    // Just check for an exception or anything else.
    EventConsumer eventConsumer = new EventConsumer(userMap);
    Util util = UtilFactory.getUtil();

    Event event = util.marshallEvent("5|B");
    try {
      eventConsumer.consumeEvent(event);
    } catch (Exception e) {
      Assert.fail();
    }

  }

  @Test
  public void test04_consumeUnPrivateEvent() {
    User user1 = this.userMap.get(60L);
    User user2 = this.userMap.get(50L);
    User user3 = this.userMap.get(12L);
    User user4 = new User(122L, null, new ConcurrentLinkedQueue<>());
    User user5 = new User(123L, null, new ConcurrentLinkedQueue<>());

    try {
      // Registered users.
      EventConsumer eventConsumer = new EventConsumer(userMap);
      Util util = UtilFactory.getUtil();

      Event event = util.marshallEvent("1|P|60|12");
      eventConsumer.consumeEvent(event);
      event = util.marshallEvent("2|P|50|12");
      eventConsumer.consumeEvent(event);

      // Not registered users.
      event = util.marshallEvent("3|P|50|122");
      eventConsumer.consumeEvent(event);
      event = util.marshallEvent("4|P|123|122");
      eventConsumer.consumeEvent(event);
      event = util.marshallEvent("5|P|122|123");
      eventConsumer.consumeEvent(event);
    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void test05_consumeUnStatusEvent() {
    User user1 = this.userMap.get(60L);
    User user2 = this.userMap.get(50L);
    User user3 = this.userMap.get(12L);
    User user4 = new User(122L, null, new ConcurrentLinkedQueue<>());
    User user5 = new User(123L, null, new ConcurrentLinkedQueue<>());

    // Create followers.
    user3.getFollowers().add(user1);
    user3.getFollowers().add(user2);

    user4.getFollowers().add(user2);
    user4.getFollowers().add(user5);
    user5.getFollowers().add(user4);

    // Registered users.
    EventConsumer eventConsumer = new EventConsumer(userMap);
    Util util = UtilFactory.getUtil();

    try {
      Event event = util.marshallEvent("1|S|12");
      eventConsumer.consumeEvent(event);

      // Not registered users.
      event = util.marshallEvent("2|S|122");
      eventConsumer.consumeEvent(event);
      event = util.marshallEvent("3|S|123");
      eventConsumer.consumeEvent(event);
    } catch (Exception e) {
      Assert.fail();
    }
  }

}