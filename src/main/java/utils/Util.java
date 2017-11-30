package utils;

/**
 * Util
 */
public class Util {

  public Long extractId(String payload) {
    Long id = Long.parseLong(payload.replace("\n", "").replace("\r", ""));

    return id;
  }
}