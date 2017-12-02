package server.entity;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User
 * This class is the entity of each user client.
 * This class contains the user-server connection over user's socket writer.
 * Also contains the follower list.
 */
public class User {

  private Long id;
  private Socket socket;
  private ConcurrentLinkedQueue<User> followers;

  public User(Long id, Socket socket, ConcurrentLinkedQueue<User> followers) {
    this.id = id;
    this.socket = socket;
    this.followers = followers;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param socket the socket to set
   */
  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  /**
   * @return the socket
   */
  public Socket getSocket() {
    return socket;
  }

  /**
   * @param concurrentLinkedQueue the concurrentLinkedQueue to set
   */
  public void setFollowers(ConcurrentLinkedQueue<User> followers) {
    this.followers = followers;
  }

  /**
   * @return the concurrentLinkedQueue
   */
  public ConcurrentLinkedQueue<User> getFollowers() {
    return followers;
  }
}