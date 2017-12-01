package integration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import integration.client.TcpTestClient;
import server.UserRegistrarServer;
import server.entity.User;

/**
 * This test aims to check if the registration cycle successfully
 * created on port 9099.
 */
/**
 * UserRegistrarServerTest
 */
public class UserRegistrarServerTest {

  private static final Logger logger = Logger.getLogger(UserRegistrarServerTest.class);
  private static final int serverPort = 9099;

  private static AtomicBoolean inService;
  private static UserRegistrarServer server;
  private static ExecutorService pool;
  private static ConcurrentHashMap<Long, User> userMap;

  @BeforeClass
  public static void createServer() {
    logger.info("Integration registration test is started.");

    inService = new AtomicBoolean(true);
    pool = Executors.newFixedThreadPool(50);
    userMap = new ConcurrentHashMap<>();
    server = new UserRegistrarServer(userMap, pool, inService);

    // Create server.
    pool.submit(server);
  }

  @Before
  public void mockServer() {
    userMap.clear();
  }

  @Test
  public void test01_checkServerCreation() {
    Assert.assertNotNull(server);
  }

  @Test
  public void test02_registerClient() {

    TcpTestClient client = new TcpTestClient("Test Client", serverPort, "2932\n", inService);
    pool.submit(client);

    hold(3000);

    Assert.assertEquals(1, userMap.size());
  }

  @Test
  public void test03_registerManyClient() {

    TcpTestClient client1 = new TcpTestClient("Test Client1", serverPort, "1\n", inService);
    TcpTestClient client2 = new TcpTestClient("Test Client2", serverPort, "2\n", inService);
    TcpTestClient client3 = new TcpTestClient("Test Client3", serverPort, "3\n\r", inService);
    TcpTestClient client4 = new TcpTestClient("Test Client4", serverPort, "4\r\n", inService);
    TcpTestClient client5 = new TcpTestClient("Test Client5", serverPort, "5\n", inService);
    pool.submit(client1);
    pool.submit(client2);
    pool.submit(client3);
    pool.submit(client4);
    pool.submit(client5);

    hold(3000);

    Assert.assertEquals(5, userMap.size());
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