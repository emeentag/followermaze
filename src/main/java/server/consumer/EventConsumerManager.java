package server.consumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import server.entity.Event;
import server.entity.User;

/**
 * EventConsumeManager
 * This class consumes an event by taking it from blocking queue.
 * This class assigns each event to a consumer for sending to the destination.
 */
public class EventConsumerManager extends CustomConsumer implements Runnable {

  private final Logger logger = Logger.getLogger(this.getClass());

  private AtomicBoolean inService;
  private ExecutorService pool;
  private PriorityBlockingQueue<Event> eventBlockingQueue;
  private AtomicLong currentSequence;
  private EventConsumer eventConsumer;

  public EventConsumerManager(ConcurrentHashMap<Long, User> userMap, PriorityBlockingQueue<Event> eventBlockingQueue,
      ExecutorService pool, AtomicBoolean inService) {
    super(userMap);

    this.inService = inService;
    this.pool = pool;
    this.eventBlockingQueue = eventBlockingQueue;
    this.currentSequence = new AtomicLong(1L);
    this.eventConsumer = new EventConsumer(getUserMap());
  }

  @Override
  public void run() {

    while (this.inService.get()) {

      // Poll the event.
      Event event = takeNextPossibleEvent();

      if (event != null) {
        // Create a thread for handling the event.
        this.eventConsumer.consumeEvent(event);
      }
    }

  }

  /**
   * Events are added to the queue according to our event comparator.
   * Means that if the sequence is smaller than the next one, small one
   * will be the head of the queue. So in here in order to send events to the 
   * destination we are checking the heads sequence with our sequence counter.
   * If they are same then we are sending the event otherwise waits until the current
   * sequence comes.
   */
  private Event takeNextPossibleEvent() {
    Event head = this.eventBlockingQueue.peek();

    if ((head != null) && (head.getSequence() == this.currentSequence.get())) {
      this.currentSequence.incrementAndGet();
      return this.eventBlockingQueue.poll();
    }

    return null;
  }

}