import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import server.UserRegistrarServer;
import server.entity.User;

/*
 * Followermaze app.
 */
public class App {

  private final Logger logger = Logger.getLogger(this.getClass());

  private AtomicBoolean inService;
  private ExecutorService pool;
  private ConcurrentHashMap<Long, User> userMap;
  private UserRegistrarServer registrarServer;

  public App(String[] args) {
    setupLogger();
    init();
  }

  private void init() {
    this.inService = new AtomicBoolean(true);
    this.pool = Executors.newFixedThreadPool(50);
    this.userMap = new ConcurrentHashMap<>();

    // Create registrar server.
    this.registrarServer = new UserRegistrarServer(this.userMap, this.pool, this.inService);

    // Run registrar server.
    this.pool.submit(registrarServer);

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
