package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import server.entity.User;

/**
 * EventReceiverServer
 * This server opens a port and starts to listen for events and then
 * create event receiver handlers for each and every event request.
 */
public class EventReceiverServer extends Server implements Runnable {

  private final Logger logger = Logger.getLogger(this.getClass());
  private final int port = 9090;

  public EventReceiverServer(ConcurrentHashMap<Long, User> userMap, ExecutorService pool, AtomicBoolean inService) {
    super(userMap, pool, inService);
  }

  @Override
  public void run() {
    try {
      setServerSocket(new ServerSocket(port));

      // Accept connection while serving.
      while (getInService().get()) {
        final Socket socket = getServerSocket().accept();

        // Handle the event.
      }

    } catch (IOException e) {
      logger.error("There was something wrong while starting event receiver server.", e);
    }
  }

}