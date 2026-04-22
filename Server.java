import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server extends JFrame {
    private ServerSocket serverSocket;
    private JTextPane area = new JTextPane();
    private JTextField input = new JTextField();
    private JButton invia = new JButton("Invia");
    private JLabel statusLabel = new JLabel("● Server offline");
    private JLabel clientsLabel; // Aggiungi questa come variabile di istanza
    public List<ThreadClient> clientConnessi = new ArrayList<>();
    
    // Colori professionali
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color SECONDARY_COLOR = new Color(76, 175, 80);
    private static final Color DARK_BG = new Color(240, 242, 245);
    private static final Color SERVER_MSG = new Color(200, 224, 255);
    private static final Color CLIENT_MSG = new Color(220, 237, 200);

    public Server() {
        setTitle("Server Chat - Professionale");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(DARK_BG);
        
        // Panel superiore con status
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Area messaggi
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferiore con input
        JPanel southPanel = createSouthPanel();
        add(southPanel, BorderLayout.SOUTH);
        
        // Margini
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        connectToServer();
        invia.addActionListener(e -> sendMessage());
        input.addActionListener(e -> sendMessage());
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(DARK_BG);
        
        JLabel titleLabel = new JLabel("Server Chat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        clientsLabel = new JLabel("Client connessi: " + clientConnessi.size());
        clientsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clientsLabel.setForeground(new Color(120, 120, 120));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.CENTER);
        panel.add(clientsLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSouthPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        input.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        input.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        input.setBackground(Color.WHITE);
        
        invia.setFont(new Font("Segoe UI", Font.BOLD, 12));
        invia.setBackground(PRIMARY_COLOR);
        invia.setForeground(Color.WHITE);
        invia.setBorder(new EmptyBorder(8, 20, 8, 20));
        invia.setFocusPainted(false);
        invia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        invia.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        panel.add(input, BorderLayout.CENTER);
        panel.add(invia, BorderLayout.EAST);
        
        return panel;
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(42345);
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("● Server online sulla porta 42345");
                    statusLabel.setForeground(SECONDARY_COLOR);
                });
                append("\n[SISTEMA] Server avviato sulla porta 42345\n\n", new Color(100, 100, 100));
                while(true) {
                    Socket socket = serverSocket.accept();
                    ThreadClient tc = new ThreadClient(socket, this);
                    clientConnessi.add(tc);
                    updateClientsLabel(); // Aggiorna il label
                    tc.start();
                    append("[SISTEMA] Nuovo client connesso: " + socket.getInetAddress().getHostAddress() + "\n\n", 
                           new Color(100, 150, 100));
                }
            } catch (Exception e) {
                append("[ERRORE] " + e.getMessage() + "\n", new Color(200, 50, 50));
            }
        }).start();
    }
    
    private void updateClientsLabel() {
        SwingUtilities.invokeLater(() -> {
            clientsLabel.setText("Client connessi: " + clientConnessi.size());
        });
    }

    private void sendMessage() {
        String msg = input.getText();
        if (msg.trim().isEmpty()) return;
        
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String formattedMsg = timestamp + " - Server: " + msg;
        append("  " + formattedMsg + "\n", SERVER_MSG);
        
        for (ThreadClient client : clientConnessi) {
            client.inviaMsgAlClient(formattedMsg);
        }
        input.setText("");
        input.requestFocus();
    }

    public void append(String msg) {
        append(msg, CLIENT_MSG);
    }
    
    public void append(String msg, Color bgColor) {
        SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
                javax.swing.text.StyleConstants.setBackground(attrs, bgColor);
                javax.swing.text.StyleConstants.setForeground(attrs, new Color(40, 40, 40));
                javax.swing.text.StyleConstants.setFontFamily(attrs, "Segoe UI");
                javax.swing.text.StyleConstants.setFontSize(attrs, 12);
                area.getDocument().insertString(area.getDocument().getLength(), msg, attrs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Server().setVisible(true));
    }
}
