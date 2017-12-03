package utils;

import java.io.File;

import server.entity.Event;
import server.event.type.EventType;

/**
 * Util
 */
public class Util {
  String pathOfFile = null;

  public Long extractId(String payload) {
    Long id = Long.parseLong(payload.replace("\n", "").replace("\r", ""));

    return id;
  }

  public Event marshallEvent(String payload) {
    String[] keys = null;
    Event event = null;
    try {
      // Make sure first char is a number.
      if (payload.charAt(0) >= '0' && payload.charAt(0) <= '9') {

        event = new Event();
        keys = payload.split("\\|");

        if (keys.length < 1)
          return null;

        event.setPayload(payload);
        event.setSequence(Long.valueOf(keys[0]));

        // Check for message type.
        if (keys.length >= 2) {
          if (keys[1].equals("F")) {
            event.setEventType(EventType.FOLLOW);
          } else if (keys[1].equals("U")) {
            event.setEventType(EventType.UNFOLLOW);
          } else if (keys[1].equals("B")) {
            event.setEventType(EventType.BROADCAST);
          } else if (keys[1].equals("P")) {
            event.setEventType(EventType.PRIVATE);
          } else if (keys[1].equals("S")) {
            event.setEventType(EventType.STATUS);
          }
        } else {
          return null;
        }

        // Set from user.
        if (keys.length >= 3) {
          event.setFromUser(Long.valueOf(keys[2]));
        } else {
          event.setFromUser(0L);
        }

        // Set to user.
        if (keys.length >= 4) {
          event.setToUser(Long.valueOf(keys[3]));
        } else {
          event.setToUser(0L);
        }
      }
    } catch (NumberFormatException nfe) {
      nfe.printStackTrace();
    } catch (IndexOutOfBoundsException ioe) {
      ioe.printStackTrace();
    }

    return event;
  }

  public String findFile(String name, File file) {
    File[] list = file.listFiles();

    if (pathOfFile == null) {
      for (File currentFile : list) {
        if (currentFile.isDirectory()) {
          findFile(name, currentFile);
        } else if (name.equalsIgnoreCase(currentFile.getName())) {
          pathOfFile = currentFile.getPath();
          break;
        }
      }
    }

    return pathOfFile;
  }
}