package integration;

import java.io.IOException;
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

  private static AtomicBoolean inService;
  private static UserRegistrarServer server;
  private static ExecutorService pool;
  private static ConcurrentHashMap<Long, User> userMap;
  private static final int serverPort = 9099;

  @BeforeClass
  public static void mockServer() {
    logger.info("Integration registration test is started.");
    inService = new AtomicBoolean(true);
    pool = Executors.newFixedThreadPool(10);
    userMap = new ConcurrentHashMap<>();
    server = new UserRegistrarServer(userMap, pool, inService);
  }

  @Test
  public void test01CheckServerCreation() {
    Assert.assertNotNull(server);
  }

  @Test
  public void test02RegisterClient() {
    pool.submit(server);

    TcpTestClient client = new TcpTestClient("Test Client", serverPort, "2932\n", inService);
    pool.submit(client);

    hold();

    Assert.assertEquals(1, userMap.size());
  }

  @AfterClass
  public static void killServer() {
    pool.shutdown();
    logger.info("Integration registration test is finished.");
  }

  private static void hold() {
    while (inService.get()) {
      //Just for merging threads.
      try {
        Thread.sleep(3000);

        inService.set(false);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}