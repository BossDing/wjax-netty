package me.normanmaurer.netty.wjax.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class PlainEchoServer {

        public static void main(String[] args) {
            try {
                final ServerSocket socket = new ServerSocket(8888);
                while (true) {
                    final Socket clientSocket = socket.accept();
                    System.out.println("Accepted connection from " + clientSocket);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(clientSocket.getInputStream()));
                                PrintWriter writer = new PrintWriter(clientSocket
                                        .getOutputStream(), true);
                                while(true) {
                                    writer.println(reader.readLine());
                                    writer.flush();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                try {
                                    clientSocket.close();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }).start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

}
