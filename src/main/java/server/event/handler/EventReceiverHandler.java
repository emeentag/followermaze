package server.event.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import server.entity.Event;
import utils.UtilFactory;

/**
 * EventReceiverHandler
 * This class handles the event and then marshallize the event payload
 * into an object. This object stores the priority blocking queue with the 
 * id number priority. Small id has the higher priority. Because it means that 
 * corresponding message comes first.
 */
public class EventReceiverHandler implements Runnable {

  private final Logger logger = Logger.getLogger(this.getClass());

  private PriorityBlockingQueue<Event> eventBlockingQueue;
  private Socket socket;

  public EventReceiverHandler(PriorityBlockingQueue<Event> eventBlockingQueue, Socket socket) {
    this.eventBlockingQueue = eventBlockingQueue;
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

      String payload;

      while ((payload = reader.readLine()) != null) {
        Event event = UtilFactory.getUtil().marshallEvent(payload);
        this.eventBlockingQueue.put(event);
      }

      logger.info("Events are read.");

    } catch (IOException e) {
      logger.error("An exception occured while reading registration.", e);
    }
  }
}