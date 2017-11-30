package server.entity;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User
 * This class is the entity of each user client.
 * This class contains the user-server connection over user's socket writer.
 * Also contains the follower list.
 */
public class User {

  private Long id;

  public User(Long id, PrintWriter writer, ConcurrentLinkedQueue<User> concurrentLinkedQueue) {
  }
}