package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import server.entity.User;
import server.event.handler.UserRegisterHandler;

/**
 * UserRegistrarServer
 */
public class UserRegistrarServer implements Runnable {

  private final Logger logger = Logger.getLogger(this.getClass());
  private final int port = 9099;

  private AtomicBoolean inService;
  private ServerSocket serverSocket;
  private ExecutorService pool;
  private ConcurrentHashMap<Long, User> userMap;

  public UserRegistrarServer(ConcurrentHashMap<Long, User> userMap, ExecutorService pool, AtomicBoolean inService) {
    this.userMap = userMap;
    this.pool = pool;
    this.inService = inService;
  }

  @Override
  public void run() {
    try {
      this.serverSocket = new ServerSocket(port);

      // Accept connection while serving.
      while (this.inService.get()) {
        final Socket socket = serverSocket.accept();

        this.pool.submit(new UserRegisterHandler(this.userMap, socket, this.inService));
      }

      logger.info("Registration server stops to serve on port: " + port);

    } catch (IOException e) {
      logger.error("There was something wrong while registration.", e);
    }
  }

}