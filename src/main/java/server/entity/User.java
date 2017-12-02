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
  private PrintWriter writer;
  private ConcurrentLinkedQueue<User> followers;

  public User(Long id, PrintWriter writer, ConcurrentLinkedQueue<User> followers) {
    this.id = id;
    this.writer = writer;
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
   * @param writer the writer to set
   */
  public void setWriter(PrintWriter writer) {
    this.writer = writer;
  }

  /**
   * @return the writer
   */
  public PrintWriter getWriter() {
    return writer;
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