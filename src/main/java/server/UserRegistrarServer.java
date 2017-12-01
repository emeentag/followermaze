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
 * This server opens a port for registration and then create register handlers for 
 * each and every register request.
 */
public class UserRegistrarServer extends Server implements Runnable {

  private final Logger logger = Logger.getLogger(this.getClass());
  private final int port = 9099;

  public UserRegistrarServer(ConcurrentHashMap<Long, User> userMap, ExecutorService pool, AtomicBoolean inService) {
    super(userMap, pool, inService);
  }

  @Override
  public void run() {
    try {
      setServerSocket(new ServerSocket(port));

      // Accept connection while serving.
      while (getInService().get()) {
        final Socket socket = getServerSocket().accept();

        // Handle register.
        getPool().submit(new UserRegisterHandler(getUserMap(), socket, getInService()));
      }

      shutDownServer();

      logger.info("Registration server stops to serve on port: " + port);

    } catch (IOException e) {
      logger.error("There was something wrong while starting registrar server.", e);
    }
  }

}