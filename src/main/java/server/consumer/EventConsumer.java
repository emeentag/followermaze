package server.consumer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import server.entity.Event;
import server.entity.User;

/**
 * EventConsumer
 * This class sends the event to the related destination or does whatever
 * this event needs.
 */
public class EventConsumer extends CustomConsumer {

  private final Logger logger = Logger.getLogger(this.getClass());

  public EventConsumer(ConcurrentHashMap<Long, User> userMap) {
    super(userMap);
  }

  public void consumeEvent(Event event) {
    logger.info("Event is: " + event.getEventType().name());

    switch (event.getEventType()) {
    case FOLLOW:
      handleFollow(event);
      break;
    case UNFOLLOW:
      handleUnFollow(event);
      break;
    case BROADCAST:
      handleBroadcast(event);
      break;
    case PRIVATE:
      handlePrivate(event);
      break;
    case STATUS:
      handleStatus(event);
      break;

    default:
      break;
    }

    logger.info("Event: " + event.getPayload() + " consumed.");
  }

  /**
   * Notify toUser's followers.
   */
  private void handleStatus(Event event) {
    User fromUser = findUser(event.getFromUser());
    if (fromUser != null) {
      fromUser.getFollowers().forEach((user) -> {
        this.notifyUser(user, event.getPayload());
      });
    }
  }

  /**
   * Notify toUser.
   */
  private void handlePrivate(Event event) {
    User toUser = findUser(event.getToUser());
    if (toUser != null) {
      this.notifyUser(toUser, event.getPayload());
    }
  }

  /**
   * Notify all registered users.
   */
  private void handleBroadcast(Event event) {
    getUserMap().forEach((id, user) -> {
      this.notifyUser(user, event.getPayload());
    });
  }

  /**
   * Remove fromUser from toUser's follower list.
   * There will be no notification.
   */
  private void handleUnFollow(Event event) {
    User fromUser = findUser(event.getFromUser());
    User toUser = findUser(event.getToUser());
    if (fromUser != null && toUser != null) {
      toUser.getFollowers().remove(fromUser);
    }
  }

  /**
   * Add fromUser into toUser's follower list.
   * Notify toUser.
   */
  private void handleFollow(Event event) {
    User fromUser = findUser(event.getFromUser());
    User toUser = findUser(event.getToUser());
    if (fromUser != null && toUser != null) {
      toUser.getFollowers().add(fromUser);
      this.notifyUser(toUser, event.getPayload());
    }
  }

  /**
   * Find the user from user map.
   */
  private User findUser(Long id) {
    return getUserMap().get(id);
  }

  /**
   * Write the payload into user's writer which is binded to the
   * socket of it.
   */
  private void notifyUser(User user, String payload) {
    PrintWriter writer;
    try {
      if (user.getSocket() != null) {
        writer = new PrintWriter(user.getSocket().getOutputStream());

        if (writer != null) {
          writer.println(payload);

          logger.info("Message: " + payload + " is sent to: " + user.getId());
        }
      }
    } catch (IOException e) {
      logger.info("An error occcured while sending message: " + payload);
    }
  }
}