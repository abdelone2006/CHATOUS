import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JTextPane area = new JTextPane();
    private JTextField input = new JTextField();
    private JButton invia = new JButton("Invia");
    private JLabel statusLabel = new JLabel("● Disconnesso");
    
    // Colori professionali
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color SECONDARY_COLOR = new Color(76, 175, 80);
    private static final Color DARK_BG = new Color(240, 242, 245);
    private static final Color SERVER_MSG = new Color(200, 224, 255);
    private static final Color CLIENT_MSG = new Color(220, 237, 200);

    public Client() {
        setTitle("Client Chat - Professionale");
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
        
        JLabel titleLabel = new JLabel("Client Chat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(200, 50, 50));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);
        
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
                socket = new Socket("localhost", 42345);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("● Connesso al server");
                    statusLabel.setForeground(SECONDARY_COLOR);
                });
                
                append("\n[SISTEMA] Connesso al server localhost:42345\n\n", new Color(100, 100, 100));

                String msg;
                while ((msg = in.readLine()) != null) {
                    Color msgColor = msg.contains("Server:") ? SERVER_MSG : CLIENT_MSG;
                    append("  " + msg + "\n", msgColor);
                }
                
                append("\n[SISTEMA] Disconnesso dal server\n", new Color(200, 50, 50));
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("● Disconnesso");
                    statusLabel.setForeground(new Color(200, 50, 50));
                });
            } catch (Exception e) {
                append("\n[ERRORE] Connessione rifiutata: " + e.getMessage() + "\n", new Color(200, 50, 50));
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("● Errore di connessione");
                    statusLabel.setForeground(new Color(200, 50, 50));
                });
            }
        }).start();
    }

    private void sendMessage() {
        String msg = input.getText();
        if (msg.trim().isEmpty()) return;
        if (out == null) {
            append("[ERRORE] Non connesso al server\n", new Color(200, 50, 50));
            return;
        }
        
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String formattedMsg = timestamp + " - Client: " + msg;
        out.println(formattedMsg);
        append("  " + formattedMsg + "\n", CLIENT_MSG);
        input.setText("");
        input.requestFocus();
    }
    
    private void append(String msg, Color bgColor) {
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
        SwingUtilities.invokeLater(() -> new Client().setVisible(true));
    }
}
