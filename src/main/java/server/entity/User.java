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
  private ConcurrentLinkedQueue<User> concurrentLinkedQueue;

  public User(Long id, PrintWriter writer, ConcurrentLinkedQueue<User> concurrentLinkedQueue) {
    this.id = id;
    this.writer = writer;
    this.concurrentLinkedQueue = concurrentLinkedQueue;
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
  public void setConcurrentLinkedQueue(ConcurrentLinkedQueue<User> concurrentLinkedQueue) {
    this.concurrentLinkedQueue = concurrentLinkedQueue;
  }

  /**
   * @return the concurrentLinkedQueue
   */
  public ConcurrentLinkedQueue<User> getConcurrentLinkedQueue() {
    return concurrentLinkedQueue;
  }
}