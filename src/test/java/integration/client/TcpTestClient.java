package integration.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
  private PrintWriter writer;

  public TcpTestClient() {
    super();
  }

  public TcpTestClient(String name, int port, String message, AtomicBoolean inService) {
    this.name = name;
    this.port = port;
    this.message = message;
    this.inService = inService;
  }

  public void createSocket() {
    try {
      this.socket = new Socket(InetAddress.getLocalHost(), this.getPort());

      this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
    } catch (UnknownHostException e) {
      logger.error("There is no host.", e);
      this.inService.set(false);
    } catch (IOException e) {
      logger.error("Socket is not available.", e);
      this.inService.set(false);
    }
  }

  @Override
  public void run() {
    createSocket();
    this.sendRequest(message);
    this.getResponse();
  }

  private void getResponse() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      String payload;

      while ((payload = reader.readLine()) != null) {
        logger.info("Response received: " + payload);
      }

    } catch (IOException e) {
      logger.error("Socket is not available.", e);
      this.inService.set(false);
    }
  }

  public void sendRequest(String message) {
    this.writer.println(message);

    if (!this.writer.checkError()) {
      logger.info("Message: " + message + " is sent.");
    } else {
      logger.error("An error occured while sending message.");
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

  /**
   * @param inService the inService to set
   */
  public void setInService(AtomicBoolean inService) {
    this.inService = inService;
  }

  /**
   * @return the inService
   */
  public AtomicBoolean getInService() {
    return inService;
  }

}