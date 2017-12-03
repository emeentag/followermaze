package unit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import server.consumer.EventConsumerManager;
import server.entity.Event;
import server.entity.User;
import server.entity.comparator.EventComparator;
import utils.Util;
import utils.UtilFactory;

/**
 * EventConsumerManagerTest
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventConsumerManagerTest {
  private AtomicBoolean inService;
  private ExecutorService pool;
  private ConcurrentHashMap<Long, User> userMap;
  private PriorityBlockingQueue<Event> eventBlockingQueue;

  @Before
  public void update() {
    this.inService = new AtomicBoolean(true);
    this.pool = Executors.newFixedThreadPool(2);
    this.userMap = new ConcurrentHashMap<>();
    this.eventBlockingQueue = new PriorityBlockingQueue<>(1024, new EventComparator());

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
  public void test01_consumeEvents() {

    this.pool.submit(new EventConsumerManager(this.userMap, this.eventBlockingQueue, this.pool, this.inService));

    Util util = UtilFactory.getUtil();

    Event event1 = util.marshallEvent("4|F|60|50");
    Event event2 = util.marshallEvent("1|U|12|9");
    Event event3 = util.marshallEvent("5|B");
    Event event4 = util.marshallEvent("2|P|32|56");
    Event event5 = util.marshallEvent("3|S|32");

    this.eventBlockingQueue.put(event1);
    this.eventBlockingQueue.put(event2);
    this.eventBlockingQueue.put(event3);
    this.eventBlockingQueue.put(event4);
    this.eventBlockingQueue.put(event5);

    holdInWhile();

    this.inService.set(false);

    // All events has to be consumed.
    Assert.assertEquals(0, this.eventBlockingQueue.size());

  }

  @Test
  public void test02_checkEventOrder() {

    this.pool.submit(new EventConsumerManager(this.userMap, this.eventBlockingQueue, this.pool, this.inService));

    Util util = UtilFactory.getUtil();

    Event event1 = util.marshallEvent("4|F|60|50");
    Event event2 = util.marshallEvent("1|U|12|9");
    Event event3 = util.marshallEvent("5|B");
    Event event4 = util.marshallEvent("2|P|32|56");
    Event event5 = util.marshallEvent("3|S|32");

    this.eventBlockingQueue.put(event1);
    this.eventBlockingQueue.put(event2);
    hold(100);
    this.eventBlockingQueue.put(event3);
    this.eventBlockingQueue.put(event4);
    hold(100);
    this.eventBlockingQueue.put(event5);

    holdInWhile();

    this.inService.set(false);

    // All events has to be consumed.
    Assert.assertEquals(0, this.eventBlockingQueue.size());

  }

  /**
   * Wait until consumer thread consumes the event queue.
   */
  private void holdInWhile() {
    while (this.eventBlockingQueue.size() > 0) {
    }
  }

  /**
   * Just for merging threads.
   */
  private void hold(int timeout) {
    try {
      Thread.sleep(timeout);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}