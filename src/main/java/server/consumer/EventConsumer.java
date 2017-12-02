package server.consumer;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
   * Notify fromUser's followers.
   */
  private void handleStatus(Event event) {
    User fromUser = findUser(event.getFromUser());

    if (fromUser != null) {
      fromUser.getFollowers().forEach((user) -> {
        this.notifyUser(user, event.getPayload());
      });

      logger.info("Status event is proccessed.");
    } else {
      logger.info("Status event ignored regarding to not found fromUser:" + event.getFromUser());
    }
  }

  /**
   * Notify toUser.
   */
  private void handlePrivate(Event event) {
    validateUsers(event);

    User toUser = findUser(event.getToUser());

    this.notifyUser(toUser, event.getPayload());

    logger.info("Private event is proccessed.");
  }

  /**
   * Notify all registered users.
   */
  private void handleBroadcast(Event event) {
    getUserMap().forEach((id, user) -> {
      this.notifyUser(user, event.getPayload());
    });

    logger.info("Broadcast event is proccessed.");
  }

  /**
   * Remove fromUser from toUser's follower list.
   * There will be no notification.
   */
  private void handleUnFollow(Event event) {
    validateUsers(event);

    User fromUser = findUser(event.getFromUser());
    User toUser = findUser(event.getToUser());

    toUser.getFollowers().remove(fromUser);

    logger.info("Unfollow event is proccessed.");
  }

  /**
   * Add fromUser into toUser's follower list.
   * Notify toUser.
   */
  private void handleFollow(Event event) {
    validateUsers(event);

    User fromUser = findUser(event.getFromUser());
    User toUser = findUser(event.getToUser());

    toUser.getFollowers().add(fromUser);
    this.notifyUser(toUser, event.getPayload());

    logger.info("Follow event is proccessed.");
  }

  /**
   * Create user if they are not exist.
   * Wee have to do this because somecases are needed
   * to get notified from and unregistered user. Like status.
   */
  private void validateUsers(Event event) {
    User fromUser = findUser(event.getFromUser());
    User toUser = findUser(event.getToUser());

    if (fromUser == null) {
      logger.info(
          "There is not a fromUser: " + event.getFromUser() + " for this " + event.getEventType().name() + " event.");

      User user = new User(event.getFromUser(), null, new ConcurrentLinkedQueue<User>());

      getUserMap().put(event.getFromUser(), user);

      logger.info("User: " + event.getFromUser() + " added to the user map.");
    }

    if (toUser == null) {
      logger
          .info("There is not a toUser: " + event.getToUser() + " for this " + event.getEventType().name() + " event.");

      User user = new User(event.getToUser(), null, new ConcurrentLinkedQueue<User>());

      getUserMap().put(event.getToUser(), user);

      logger.info("User: " + event.getToUser() + " added to the user map.");
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
    PrintWriter writer = user.getWriter();

    if (writer != null) {
      writer.println(payload);

      if (!writer.checkError()) {
        logger.info("Event: " + payload + " is sent to: " + user.getId());
      } else {
        logger.error("An error occcured while sending event: " + payload);
      }
    }
  }
}