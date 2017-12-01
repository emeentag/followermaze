package integration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import integration.client.TcpTestClient;
import server.EventReceiverServer;
import server.UserRegistrarServer;
import server.entity.User;

/**
 * EventReceiverServerTest
 */
public class EventReceiverServerTest {

  private static final Logger logger = Logger.getLogger(UserRegistrarServerTest.class);
  private static final int eventServerPort = 9090;
  private static final int userServerPort = 9099;

  private static AtomicBoolean inService;
  private static ExecutorService pool;
  private static ConcurrentHashMap<Long, User> userMap;
  private static EventReceiverServer eventServer;
  private static UserRegistrarServer userServer;

  @BeforeClass
  public static void createServer() {

    logger.info("Integration event receiver test is started.");

    inService = new AtomicBoolean(true);
    userMap = new ConcurrentHashMap<>();
    pool = Executors.newFixedThreadPool(50);
    eventServer = new EventReceiverServer(userMap, pool, inService);
    userServer = new UserRegistrarServer(userMap, pool, inService);

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
  public void test02_registerClients() {

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

    hold(3000);

    Assert.assertEquals(6, userMap.size());
  }

  @Test
  public void test03_sendEvents() {

    TcpTestClient client1 = new TcpTestClient("Test Client1", eventServerPort, "666\\|F\\|60\\|50", inService);
    TcpTestClient client2 = new TcpTestClient("Test Client2", eventServerPort, "1\\|U\\|12\\|9", inService);
    TcpTestClient client3 = new TcpTestClient("Test Client3", eventServerPort, "542532\\|B", inService);
    TcpTestClient client4 = new TcpTestClient("Test Client4", eventServerPort, "43\\|P\\|32\\|56", inService);
    TcpTestClient client5 = new TcpTestClient("Test Client5", eventServerPort, "634\\|S\\|32", inService);
    pool.submit(client1);
    pool.submit(client2);
    pool.submit(client3);
    pool.submit(client4);
    pool.submit(client5);

    hold(3000);

    Assert.assertEquals(5, this.eventServer.getEventSize());
  }

  @AfterClass
  public static void killServer() {
    inService.set(false);
    pool.shutdown();
    logger.info("Integration registration test is finished.");
  }

  private static void hold(int timeout) {
    //Just for merging threads.
    try {
      Thread.sleep(timeout);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}