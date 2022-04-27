package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import protocol.Protocol;
import protocol.ProtocolImp;


/**
 * client class
 *
 * @author Zitao Shen
 */
public class Client {
  private String userName;
  private Protocol protocol  = new ProtocolImp();

  private boolean logOff = false;
  private boolean connectStatus = false;
  private Socket client = null;

  private final static int READER_NUM = 1;

  public Client() {
  }

  private void start(String[] args) throws IOException {
    if (args.length<2)  {
      System.err.println("Wrong number of args! Usage: Client <server IP> <server port>");
      System.exit(1);
    }

    //Try to connect to server
    Scanner sc = new Scanner(System.in);
    String hostname = args[0];
    int port =  Integer.parseInt(args[1]);

    try {
      client = new Socket(hostname, port);
    }catch(Exception e){
      System.err.println("Could not connect to "+hostname+":"+port+ ", has it started?");
      System.exit(1);
    }

    while (!this.logOff) {
      try {
        DataOutputStream toServer;
        //if server closed ahead
        try {
          toServer = new DataOutputStream(client.getOutputStream());
        }catch(Exception e){
          System.out.println("Server closed, retry connecting in 3 seconds.");
          TimeUnit.SECONDS.sleep(3);
          //Continue to check and connect server if server closed
          try {
            client = new Socket(hostname, port);
          }catch(Exception e2){
            System.err.println("Could not connect to "+hostname+":"+port+ ", has it started?");
          }
          continue;
        }
        String input = "";
        DataInputStream fromServer = new DataInputStream(client.getInputStream());
        while(!connectStatus){

          System.out.println(">>> Enter username to login as <username> \n");
          input = sc.nextLine();

          //user didn't input anything
          while (input.trim().equals("")){
            System.out.println(">>> Input is empty, please ENTER an username as <username> \n");
            input = sc.nextLine();
          }
          System.out.println(">>> Hi," + input.trim());
//          This username is case seneitive
          this.setUserName(input.trim());
          InputHandler inputHandler = new InputHandler(this.userName, toServer);
          OutputHandler outputHandler = new OutputHandler(this.userName, fromServer);
          inputHandler.connectServer();
          client.setSoTimeout(3000);
          fromServer.readInt();
          connectStatus = outputHandler.connectStatusResponseHandle();
        }
        CountDownLatch readLatch = new CountDownLatch(this.READER_NUM);
        new ReaderThread(this, readLatch,client).start();
        new WriterThread(this, readLatch,client).start();
        readLatch.await();
      }catch(SocketTimeoutException e){
        System.err.println("Timeout error. Server not responding.");
      }catch(Exception e){
        if (client != null) client.close();
      }
    }

    System.exit(0);
  }

  public static void main(String[] args) throws IOException{
    Client client = new Client();
    client.start(args);
  }

  public boolean isLogOff() {
    return logOff;
  }

  public void setLogOff(boolean logOff) {
    this.logOff = logOff;
  }


  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Client client = (Client) o;
    return Objects.equals(userName, client.userName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName);
  }

  @Override
  public String toString() {
    return "Client{}";
  }
}