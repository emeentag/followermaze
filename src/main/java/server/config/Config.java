package server.config;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

    StringBuffer sBuf = new StringBuffer();
    sBuf.append("\nConfig:\n\t").append("CONCURRENCY_LEVEL: " + CONCURRENCY_LEVEL + "\n\t")
        .append("LOG_LEVEL: " + LOG_LEVEL + "\n\t")
        .append("REGISTRATION_SERVER_PORT: " + REGISTRATION_SERVER_PORT + "\n\t")
        .append("EVENT_RECEIVER_SERVER_PORT: " + EVENT_RECEIVER_SERVER_PORT + "\n\t");

    Logger.getLogger(Config.class).info(sBuf.toString());
  }

}