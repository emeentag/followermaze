package server.event.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import server.entity.User;
import utils.UtilFactory;

/**
 * UserRegisterHandler
 * This class handles the registration requests and put each request
 * into a <id, user> map.
 */
public class UserRegisterHandler implements Runnable {

  private final Logger logger = Logger.getLogger(this.getClass());

  private Socket socket;
  private AtomicBoolean inService;
  private ConcurrentHashMap<Long, User> userMap;

  public UserRegisterHandler() {
    super();
  }

  public UserRegisterHandler(ConcurrentHashMap<Long, User> userMap, Socket socket, AtomicBoolean inService) {
    this.userMap = userMap;
    this.socket = socket;
    this.inService = inService;
  }

  @Override
  public void run() {
    try {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      final PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);

      //while (this.inService.get()) {
      String payload = reader.readLine();

      Long id = UtilFactory.getUtil().extractId(payload);

      logger.info("New registration request for id: " + payload);

      User user = new User(id, writer, new ConcurrentLinkedQueue<User>());
      this.userMap.put(id, user);

      logger.info("A new user registered with id: " + id);
      //}

    } catch (IOException e) {
      logger.error("An exception occured while reading registration.", e);
    }
  }

}