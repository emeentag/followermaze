import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import server.EventReceiverServer;
import server.UserRegistrarServer;
import server.consumer.EventConsumerManager;
import server.entity.Event;
import server.entity.User;
import server.entity.comparator.EventComparator;

/*
 * Followermaze app.
 */
public class App {

  private final Logger logger = Logger.getLogger(this.getClass());

  private AtomicBoolean inService;
  private ExecutorService pool;
  private ConcurrentHashMap<Long, User> userMap;
  private PriorityBlockingQueue<Event> eventBlockingQueue;
  private UserRegistrarServer registrarServer;
  private EventReceiverServer eventServer;
  private EventConsumerManager eventConsumerManager;

  public App(String[] args) {
    setupLogger();
    init();
  }

  private void init() {
    this.inService = new AtomicBoolean(true);
    this.pool = Executors.newFixedThreadPool(100);
    this.userMap = new ConcurrentHashMap<>();
    this.eventBlockingQueue = new PriorityBlockingQueue<>(2048, new EventComparator());

    // Create event consumer.
    this.eventConsumerManager = new EventConsumerManager(userMap, eventBlockingQueue, pool, inService);

    // Create registrar server.
    this.registrarServer = new UserRegistrarServer(this.userMap, this.pool, this.inService);

    // Create event receiver server.
    this.eventServer = new EventReceiverServer(this.userMap, this.eventBlockingQueue, this.pool, this.inService);

    // Run event consumers.
    this.pool.submit(this.eventConsumerManager);

    // Run registrar server.
    this.pool.submit(this.registrarServer);
    this.pool.submit(this.eventServer);

    logger.info("Servers are up and running.");
  }

  /**
  * Setups the loader.
  * Uses the log4j.properties
  */
  private void setupLogger() {
    String log4jConfigFile = System.getProperty("user.dir") + File.separator + "src/main/resources/log4j.properties";

    PropertyConfigurator.configure(log4jConfigFile);
    logger.setLevel(Level.ALL);
  }

  public static void main(String[] args) {
    new App(args);
  }

}
