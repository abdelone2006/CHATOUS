import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
 
// Classe client che si connette a un server socket per comunicare via messaggi
public class chat extends JFrame {
 
    // Socket per la connessione al server
    private Socket socket;
    // Stream di input per leggere messaggi dal server
    private BufferedReader in;
    // Stream di output per inviare messaggi al server
    private PrintWriter out;
 
    private JTextArea area = new JTextArea();
    private JTextField input = new JTextField();
    private JButton invia = new JButton("Invia");
 
    // Costruttore della classe Client2
    // Inizializza l'interfaccia grafica e avvia la connessione al server
    public chat() {
        setTitle("Client");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
 
        add(new JScrollPane(area), BorderLayout.CENTER);
 
        JPanel south = new JPanel(new BorderLayout());
        south.add(input, BorderLayout.CENTER);
        south.add(invia, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
 
 
        // Avvia la connessione al server
        connectToServer();
        invia.addActionListener(e -> sendMessage());
    }
 
    // Stabilisce la connessione al server in un thread separato
    // Legge continuamente i messaggi ricevuti dal server e li visualizza
    private void connectToServer() {
        new Thread(() -> {
            try {
                //
                //
 
                socket = new Socket("2.tcp.eu.ngrok.io", 10979);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
 
                append("Connesso al server\n");
                // legge il numero della porta del server
                append("Porta del server:"+socket.getPort()+"\n");
                // legge l'ip del server
                append("IP adress:"+socket.getInetAddress()+"\n");
 
                String msg;
                while ((msg = in.readLine()) != null) {
                    append("" + msg + "\n");
                }
            } catch (Exception e) {
                append("Errore di connessione\n");
            }
        }).start();
    }
 
    private void sendMessage() {
        String msg = input.getText();
        // Invia il messaggio al server
        out.println("dave: " + msg);
        append(msg + "\n");
        input.setText("");
    }
 
    // Aggiorna l'area di testo in modo thread-safe utilizzando EventDispatchThread
    private void append(String msg) {
        SwingUtilities.invokeLater(() -> area.append(msg));
    }
 
    // Metodo main: crea e visualizza l'interfaccia del client
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client().setVisible(true));
    }
}