package server;

import com.sun.source.tree.Scope;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import protocol.MessageType;
import protocol.Protocol;
import protocol.ProtocolImp;

/**
 * Server handler class handle messages received
 *
 * @author xiaochong
 */
public class ServerHandler implements Runnable {
  private Semaphore semaphore;
  private ServerSocket serverSocket;
  private Protocol protocol;
  private ConcurrentHashMap<String, Socket> socketMap;

  public ServerHandler(Semaphore semaphore, ServerSocket serverSocket) {
    this.semaphore = semaphore;
    this.serverSocket = serverSocket;
    protocol = new ProtocolImp();
  }

  @Override
  public void run() {
    while (true) {
      try {
        this.semaphore.acquire();
        Socket socket = this.serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String line;
        // !line.equals(logoff)
        line = in.readLine();
        MessageType type = protocol.getMessageType(line);
        switch (type) {
          case BROADCAST_MESSAGE -> {
          }
          case SEND_INSULT -> {
          }
          case CONNECT_MESSAGE -> {

            socketMap.put(protocol.decode(type, line), socket);
            // success
            String reponse = "" + socketMap.size();
            // fail


            out.write(protocol.encode(MessageType.CONNECT_RESPONSE, reponse));
          }

          case DISCONNECT_MESSAGE -> {
            String username = protocol.decode(type, line);
            Socket socket1 = socketMap.get(username);
            socket1.close();
            socketMap.remove(username);
          }
        }

        if (socketMap.size() == 0)
          break;

        in.close();

        semaphore.release();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}