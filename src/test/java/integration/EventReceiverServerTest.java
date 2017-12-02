package integration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import integration.client.TcpTestClient;
import server.EventReceiverServer;
import server.UserRegistrarServer;
import server.consumer.EventConsumerManager;
import server.entity.Event;
import server.entity.User;
import server.entity.comparator.EventComparator;

/**
 * EventReceiverServerTest
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventReceiverServerTest {

  private static final Logger logger = Logger.getLogger(EventReceiverServerTest.class);
  private static final int eventServerPort = 9090;
  private static final int userServerPort = 9099;

  private static AtomicBoolean inService;
  private static ExecutorService pool;
  private static ConcurrentHashMap<Long, User> userMap;
  private static PriorityBlockingQueue<Event> eventBlockingQueue;
  private static EventReceiverServer eventServer;
  private static UserRegistrarServer userServer;
  private static EventConsumerManager consumerManager;

  @BeforeClass
  public static void createServer() {
    logger.info("Integration event receiver test is started.");

    inService = new AtomicBoolean(true);
    userMap = new ConcurrentHashMap<>();
    eventBlockingQueue = new PriorityBlockingQueue<>(1024, new EventComparator());
    pool = Executors.newCachedThreadPool();
    eventServer = new EventReceiverServer(userMap, eventBlockingQueue, pool, inService);
    userServer = new UserRegistrarServer(userMap, pool, inService);
    consumerManager = new EventConsumerManager(userMap, eventBlockingQueue, pool, inService);

    // Create server.
    pool.submit(consumerManager);
    pool.submit(userServer);
    pool.submit(eventServer);

    // Register test clients.
    TcpTestClient client1 = new TcpTestClient("Test Client1", userServerPort, "3\n", inService);
    TcpTestClient client2 = new TcpTestClient("Test Client2", userServerPort, "4\n", inService);
    TcpTestClient client3 = new TcpTestClient("Test Client3", userServerPort, "2\n\r", inService);
    TcpTestClient client4 = new TcpTestClient("Test Client4", userServerPort, "1\r\n", inService);
    TcpTestClient client5 = new TcpTestClient("Test Client5", userServerPort, "6\n", inService);
    TcpTestClient client6 = new TcpTestClient("Test Client6", userServerPort, "5\n", inService);
    pool.submit(client1);
    pool.submit(client2);
    pool.submit(client3);
    pool.submit(client4);
    pool.submit(client5);
    pool.submit(client6);

    while (userMap.size() < 6) {
      // Hold until registration is done.
    }

    logger.info("Registered number of users: " + userMap.size());
  }

  @Test
  public void test01_checkServerCreation() {
    Assert.assertNotNull(consumerManager);
    Assert.assertNotNull(eventServer);
    Assert.assertNotNull(userServer);
  }

  @Test
  public void test02_sendEvents() {
    TcpTestClient sender = new TcpTestClient();
    sender.setName("Sender Client");
    sender.setPort(eventServerPort);
    sender.setInService(inService);
    sender.createSocket();

    sender.sendRequest("1|F|3|4");
    sender.sendRequest("3|U|3|4");
    sender.sendRequest("4|B");
    sender.sendRequest("2|P|1|56");
    sender.sendRequest("5|F|2|3");
    sender.sendRequest("6|F|4|3");
    sender.sendRequest("7|U|2|3");

    hold(3000);

    Assert.assertEquals(0, userMap.get(4L).getFollowers().size());
    Assert.assertEquals(1, userMap.get(3L).getFollowers().size());
    Assert.assertEquals(0, eventBlockingQueue.size());
  }

  @AfterClass
  public static void killServer() {
    inService.set(false);
    pool.shutdown();
    logger.info("Integration event test is finished.");
  }

  /**
   * Wait until consumer thread consumes the event queue.
   */
  private static void holdInWhile() {
    while (eventBlockingQueue.size() > 0) {
    }
  }

  /**
   * Just for merging threads.
   */
  private static void hold(int timeout) {
    try {
      Thread.sleep(timeout);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}