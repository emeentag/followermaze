package server.config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;

import utils.UtilFactory;

/**
 * Config
 * This class checks the enviromental varaibles.
 */
public class Config {
  public static Integer CONCURRENCY_LEVEL = 0;
  public static Level LOG_LEVEL = Level.OFF;
  public static Integer REGISTRATION_SERVER_PORT = 9099;
  public static Integer EVENT_RECEIVER_SERVER_PORT = 9090;

  private Config() {
    super();
  }

  /**
   * Set env vars.
   */
  public static void checkConfig() {
    String concurrencyLevel = System.getenv("CONCURRENCY_LEVEL");
    String logLevel = System.getenv("LOG_LEVEL");
    String registrationServerPort = System.getenv("REGISTRATION_SERVER_PORT");
    String eventReceiverServerPort = System.getenv("EVENT_RECEIVER_SERVER_PORT");

    if (concurrencyLevel != null) {
      CONCURRENCY_LEVEL = Integer.valueOf(concurrencyLevel);
    }

    if (logLevel != null) {
      if (logLevel.equals("all")) {
        LOG_LEVEL = Level.ALL;
      } else if (logLevel.equals("debug")) {
        LOG_LEVEL = Level.DEBUG;
      } else if (logLevel.equals("info")) {
        LOG_LEVEL = Level.INFO;
      } else {
        LOG_LEVEL = Level.OFF;
      }
    }

    if (registrationServerPort != null) {
      REGISTRATION_SERVER_PORT = Integer.valueOf(System.getenv("REGISTRATION_SERVER_PORT"));
    }

    if (eventReceiverServerPort != null) {
      EVENT_RECEIVER_SERVER_PORT = Integer.valueOf(System.getenv("EVENT_RECEIVER_SERVER_PORT"));
    }
  }

  public static String getConfigInfo() {
    StringBuffer sBuf = new StringBuffer();
    sBuf.append("\nConfig:\n\t").append("CONCURRENCY_LEVEL: " + CONCURRENCY_LEVEL + "\n\t")
        .append("LOG_LEVEL: " + LOG_LEVEL + "\n\t")
        .append("REGISTRATION_SERVER_PORT: " + REGISTRATION_SERVER_PORT + "\n\t")
        .append("EVENT_RECEIVER_SERVER_PORT: " + EVENT_RECEIVER_SERVER_PORT + "\n\t");

    return sBuf.toString();
  }

  /**
  * Setups the loader.
  * Uses the log4j.properties
  */
  public static void configureLogger() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hhmmss");
    System.setProperty("currentDate", dateFormat.format(new Date()));
    System.setProperty("logLevel", Config.LOG_LEVEL.toString());

    String log4jProperties = UtilFactory.getUtil().findFile("log4j.properties",
        new File(System.getProperty("user.dir")));

    if (log4jProperties != null) {
      PropertyConfigurator.configure(log4jProperties);
    } else {
      System.out.println("There is not log4j.properties file found in this directory.");
    }
  }

}