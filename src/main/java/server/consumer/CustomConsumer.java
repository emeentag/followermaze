package server.consumer;

import java.util.concurrent.ConcurrentHashMap;

import server.entity.User;

/**
 * Consumer
 */
public abstract class CustomConsumer {

  private ConcurrentHashMap<Long, User> userMap;

  public CustomConsumer(ConcurrentHashMap<Long, User> userMap) {
    this.userMap = userMap;
  }

  /**
   * @param userMap the userMap to set
   */
  public void setUserMap(ConcurrentHashMap<Long, User> userMap) {
    this.userMap = userMap;
  }

  /**
   * @return the userMap
   */
  public ConcurrentHashMap<Long, User> getUserMap() {
    return userMap;
  }

}