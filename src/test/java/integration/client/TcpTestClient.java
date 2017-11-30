package integration.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

/**
 * TcpClient
 */
public class TcpTestClient implements Runnable {

  private final Logger logger = Logger.getLogger(this.getClass());

  private String name;
  private int port;
  private String message;
  private AtomicBoolean inService;
  private Socket socket;

  public TcpTestClient(String name, int port, String message, AtomicBoolean inService) {
    this.name = name;
    this.port = port;
    this.message = message;
    this.inService = inService;
  }

  @Override
  public void run() {
    this.sendRequest(message);
    this.getResponse();
  }

  private void getResponse() {
    while (this.inService.get()) {
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        String payload = reader.readLine();

        logger.info("Response received: " + payload);

      } catch (IOException e) {
        logger.error("Socket is not available.", e);
        this.inService.set(false);
      }
    }
  }

  public void sendRequest(String message) {
    try {
      this.socket = new Socket(InetAddress.getLocalHost(), this.getPort());

      PrintWriter writer = new PrintWriter(this.socket.getOutputStream(), true);
      writer.write(message);
      writer.println();

      logger.info("Message: " + message + "is sent.");

    } catch (UnknownHostException e) {
      logger.error("There is no host.", e);
      this.inService.set(false);
    } catch (IOException e) {
      logger.error("Socket is not available.", e);
      this.inService.set(false);
    }
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param port the port to set
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * @return the port
   */
  public int getPort() {
    return port;
  }

}