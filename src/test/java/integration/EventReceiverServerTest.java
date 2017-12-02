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

  @BeforeClass
  public static void createServer() {

    logger.info("Integration event receiver test is started.");

    inService = new AtomicBoolean(true);
    userMap = new ConcurrentHashMap<>();
    eventBlockingQueue = new PriorityBlockingQueue<>(1024, new EventComparator());
    pool = Executors.newFixedThreadPool(50);
    eventServer = new EventReceiverServer(userMap, eventBlockingQueue, pool, inService);
    userServer = new UserRegistrarServer(userMap, pool, inService);

    // Register test clients.
    TcpTestClient client1 = new TcpTestClient("Test Client1", userServerPort, "60\n", inService);
    TcpTestClient client2 = new TcpTestClient("Test Client2", userServerPort, "50\n", inService);
    TcpTestClient client3 = new TcpTestClient("Test Client3", userServerPort, "12\n\r", inService);
    TcpTestClient client4 = new TcpTestClient("Test Client4", userServerPort, "9\r\n", inService);
    TcpTestClient client5 = new TcpTestClient("Test Client5", userServerPort, "32\n", inService);
    TcpTestClient client6 = new TcpTestClient("Test Client6", userServerPort, "56\n", inService);
    pool.submit(client1);
    pool.submit(client2);
    pool.submit(client3);
    pool.submit(client4);
    pool.submit(client5);
    pool.submit(client6);

    // Create server.
    pool.submit(eventServer);
    pool.submit(userServer);
  }

  @Test
  public void test01_checkServerCreation() {
    Assert.assertNotNull(eventServer);
    Assert.assertNotNull(userServer);
  }

  @Test
  public void test02_sendEvents() {

    TcpTestClient client1 = new TcpTestClient("Test Client1", eventServerPort, "3|F|60|50", inService);
    TcpTestClient client2 = new TcpTestClient("Test Client2", eventServerPort, "1|U|12|9", inService);
    TcpTestClient client3 = new TcpTestClient("Test Client3", eventServerPort, "4|B", inService);
    TcpTestClient client4 = new TcpTestClient("Test Client4", eventServerPort, "2|P|32|56", inService);
    TcpTestClient client5 = new TcpTestClient("Test Client5", eventServerPort, "5|S|32", inService);
    pool.submit(client1);
    pool.submit(client2);
    pool.submit(client3);
    pool.submit(client4);
    pool.submit(client5);

    holdInWhile();

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