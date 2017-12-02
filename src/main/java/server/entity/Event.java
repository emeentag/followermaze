package server.entity;

import server.event.type.EventType;

/**
 * Event
 */
public class Event implements Comparable<Event> {

  private String payload;
  private Long sequence;
  private EventType eventType;
  private Long fromUser;
  private Long toUser;

  public Event() {
    super();
  }

  public Event(String payload, Long sequence, Long id, EventType eventType, Long fromUser, Long toUser) {
    this.payload = payload;
    this.sequence = sequence;
    this.eventType = eventType;
    this.fromUser = fromUser;
    this.toUser = toUser;
  }

  /**
   * @param eventType the eventType to set
   */
  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  /**
   * @return the eventType
   */
  public EventType getEventType() {
    return eventType;
  }

  /**
   * @param fromUser the fromUser to set
   */
  public void setFromUser(Long fromUser) {
    this.fromUser = fromUser;
  }

  /**
   * @return the fromUser
   */
  public Long getFromUser() {
    return fromUser;
  }

  /**
   * @param payload the payload to set
   */
  public void setPayload(String payload) {
    this.payload = payload;
  }

  /**
   * @return the payload
   */
  public String getPayload() {
    return payload;
  }

  /**
   * @param sequence the sequence to set
   */
  public void setSequence(Long sequence) {
    this.sequence = sequence;
  }

  /**
   * @return the sequence
   */
  public Long getSequence() {
    return sequence;
  }

  /**
   * @param toUser the toUser to set
   */
  public void setToUser(Long toUser) {
    this.toUser = toUser;
  }

  /**
   * @return the toUser
   */
  public Long getToUser() {
    return toUser;
  }

  @Override
  public int compareTo(Event o) {
    return Long.compare(this.getSequence(), o.getSequence());
  }

}