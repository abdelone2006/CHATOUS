import java.io.*;
import java.net.*;
public class ThreadClient extends Thread {
    private Socket socket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;

    public ThreadClient(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            server.append("  Client connesso: " + socket.getInetAddress() + "\n", new java.awt.Color(100, 150, 100));

            String msg;
            while ((msg = in.readLine()) != null) {
                server.append("  " + msg + "\n", new java.awt.Color(220, 237, 200));
                for (ThreadClient client : server.clientConnessi) {
                    if (client != this) {
                        client.inviaMsgAlClient(msg);
                    }
                }
            }
            server.append("  Client disconnesso: " + socket.getInetAddress() + "\n", new java.awt.Color(200, 50, 50));
            server.clientConnessi.remove(this);
            socket.close();
        } catch (Exception e) {
            server.append("  [ERRORE] " + e.getMessage() + "\n", new java.awt.Color(200, 50, 50));
        }
    }

    public void inviaMsgAlClient(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
