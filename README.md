# CHATOUS
Questo progetto implementa un sistema di chat multi-utente basato su un'architettura Client-Server. Il server gestisce le connessioni in arrivo e distribuisce i messaggi a tutti i client connessi (broadcast), garantendo un'esperienza di chat fluida e in tempo reale.
# Applicazione Chat tramite Socket TCP/IP

## 📋 Indice
1. [Descrizione Progetto]
2. [Architettura Generale]
3. [Struttura del Progetto]
4. [Componenti Principali]
5. [Guida all'Installazione]
6. [Guida all'Utilizzo]
7. [Protocollo di Comunicazione]
8. [Dettagli Tecnici]
9. [Pattern di Programmazione]

---

## Descrizione Progetto

Si tratta di un'**applicazione client-server per chat in tempo reale** sviluppata in Java, che utilizza i socket TCP/IP per la comunicazione di rete. L'applicazione consente a più client di connettersi a un server centrale e scambiarsi messaggi tramite un'interfaccia grafica Swing.

### Caratteristiche Principali
- ✅ Comunicazione client-server in tempo reale
- ✅ Supporto per più client contemporanei
- ✅ **Interfaccia grafica moderna e professionale**
- ✅ Threading per operazioni non-bloccanti
- ✅ Gestione thread-safe dell'UI
- ✅ Broadcast dei messaggi a tutti i client connessi
- ✅ **Timestamp su ogni messaggio**
- ✅ **Barra di stato con indicatore di connessione**
- ✅ **Colori distinti per messaggi server/client**
- ✅ **Font Segoe UI e layout responsive**

### Tecnologie Utilizzate
- **Linguaggio**: Java
- **Framework UI**: Swing (JFrame, JTextPane, JPanel, BorderLayout)
- **Styling**: Colori personalizzati, Font Segoe UI, JTextPane con AttributeSet
- **Networking**: Socket TCP/IP (ServerSocket, Socket)
- **Threading**: Java Thread, SwingUtilities.invokeLater()
- **I/O**: BufferedReader, PrintWriter
- **Grafica**: SimpleDateFormat per timestamp, EmptyBorder, LineBorder

---

## Architettura Generale

### Modello Client-Server

```
                    ┌─────────────────┐
                    │     SERVER      │
                    │   (porta 42345) │
                    └────────┬────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
            ┌───────┐   ┌───────┐   ┌───────┐
            │Client │   │Client │   │Client │
            │   1   │   │   2   │   │   3   │
            └───────┘   └───────┘   └───────┘
```

### Flusso di Comunicazione

1. **Connessione**: Ogni client si connette al server sulla porta 42345
2. **Ricezione**: Il server accetta la connessione e crea un thread dedicato (`ThreadClient`)
3. **Trasmissione**: I messaggi ricevuti vengono trasmessi a tutti gli altri client
4. **Visualizzazione**: Tutti i client ricevono e visualizzano i messaggi

---


## Componenti Principali

### 1. Server.java (Versione Principale)

**Responsabilità**: Gestire connessioni multiple e distribuire messaggi

```
┌─────────────────────────────────┐
│          Server                 │
├─────────────────────────────────┤
│ - serverSocket: ServerSocket    │
│ - clientConnessi: List<Thread>  │
│ - area: JTextPane (log)         │
│ - input: JTextField             │
│ - statusLabel: JLabel           │
├─────────────────────────────────┤
│ + connectToServer()             │
│ + sendMessage()                 │
│ + append(msg, Color)            │
│ + createTopPanel()              │
│ + createSouthPanel()            │
└─────────────────────────────────┘
```

**Metodi Principali**:
- `connectToServer()`: Avvia il server e accetta connessioni in un thread dedicato
- `sendMessage()`: Invia un messaggio con timestamp a tutti i client
- `append(msg, Color)`: Aggiorna JTextPane con colori personalizzati
- `createTopPanel()`: Crea il pannello superiore con barra di stato
- `createSouthPanel()`: Crea il pannello inferiore con input e bottone

**Design UI**:
- 📱 **Dimensioni**: 600x500 pixel
- 🎨 **Colori**: Blu (#2196F3) primario, Verde (#4CAF50) per lo stato online
- 📊 **Sfondo**: Grigio chiaro (#F0F2F5)
- 🕐 **Messaggi**: Timestamp HH:mm:ss, sfondo blu per server, verde per client
- 🔘 **Bottone**: Sfondo blu, testo bianco, cursor a mano
- ⌨️ **Font**: Segoe UI 12pt

**Porta**: 42345

---

### 2. Client.java (Versione Principale)

**Responsabilità**: Collegamento al server e invio/ricezione messaggi

```
┌─────────────────────────────────┐
│         Client2                 │
├─────────────────────────────────┤
│ - socket: Socket                │
│ - in: BufferedReader            │
│ - out: PrintWriter              │
│ - area: JTextPane (chat)        │
│ - statusLabel: JLabel           │
├─────────────────────────────────┤
│ + connectToServer()             │
│ + sendMessage()                 │
│ + append(msg, Color)            │
│ + createTopPanel()              │
│ + createSouthPanel()            │
└─────────────────────────────────┘
```

**Metodi Principali**:
- `connectToServer()`: Stabilisce connessione al server con aggiornamento stato
- `sendMessage()`: Invia il messaggio con timestamp al server
- `append(msg, Color)`: Visualizza messaggi nella JTextPane con colori

**Design UI**:
- 📱 **Dimensioni**: 600x500 pixel
- 🎨 **Colori**: Blu primario, rosso per disconnessione
- 📊 **Sfondo**: Grigio chiaro con area messaggi bianca
- 🟢 **Indicatore connessione**: Pallino verde quando connesso, rosso quando offline
- 🕐 **Formato messaggi**: "HH:mm:ss - Server/Client: messaggio"
- 🔲 **Bordi**: LineBorder blu attorno all'input
- ⌨️ **Font**: Segoe UI 12-13pt

**Connessione**: localhost:42345

---

### 3. ThreadClient.java

**Responsabilità**: Gestire comunicazione con un singolo client

```
┌──────────────────────────────────┐
│       ThreadClient extends       │
│          Thread                  │
├──────────────────────────────────┤
│ - socket: Socket                 │
│ - server: Server                 │
│ - in: BufferedReader             │
│ - out: PrintWriter               │
├──────────────────────────────────┤
│ + run()                          │
│ + inviaMsgAlClient(msg)          │
└──────────────────────────────────┘
```

**Ciclo di Vita**:

```
START
  ↓
accept() ← Accetta il client
  ↓
initialize streams ← Inizializza reader/writer
  ↓
append("Client connesso") ← Log di connessione
  ↓
while (readMessage()) ← Loop infinito di ascolto
  ├─ append(msg) ← Log del messaggio
  └─ broadcast() ← Invia agli altri client
  ↓
cleanup ← Chiude risorse
  ↓
END
```

## Guida all'Installazione

### Prerequisiti
- Java Development Kit (JDK) 8 o superiore
- IDE: IntelliJ IDEA, Eclipse, VS Code con estensione Java

### Esecuzione

#### Metodo 1: Da IDE
1. Aprire il progetto nell'IDE
2. Eseguire `Server.java` (clic destro → Run)
3. Eseguire `Client.java` (più volte per più client)

## Guida all'Utilizzo

### Avvio dell'Applicazione

1. **Avviare il Server**
   - Eseguire `Server.java`
   - La finestra del server si apre con interfaccia professionale
   - Titolo: "Server Chat - Professionale"
   - Mostra lo stato: "🟢 Server online sulla porta 42345"
   - La finestra è 600x500 con colori Blu/Verde

2. **Avviare i Client**
   - Eseguire `Client2.java` (una volta per ogni client)
   - Ogni client mostra: "🟢 Connesso al server"
   - La finestra mostra i messaggi ricevuti con colori differenziati
   - Stesso design professionale del server

### Invio Messaggi

1. Digitare il messaggio nella casella di testo
2. Premere il pulsante "Invia" o il tasto Enter
3. Il messaggio viene mostrato nella stessa finestra con indentazione
4. Tutti gli altri client ricevono il messaggio: `HH:mm:ss - Server: [messaggio]`

### Colori e Significato

| Colore | Elemento | Significato |
|--------|----------|-------------|
| 🔵 Blu (#2196F3) | Titoli, Bottone | Primario / Server |
| 🟢 Verde (#4CAF50) | Stato online | Connesso |
| 💙 Blu chiaro | Sfondo messaggi | Messaggi dal Server |
| 💚 Verde chiaro | Sfondo messaggi | Messaggi dai Client |
| 🔴 Rosso | Messaggi di errore | Problema di connessione |
| ⚪ Grigio | Sfondo e messaggi sistema | Informazioni di sistema |

### Esempio di Chat Completa

```
[Server]
🟢 Server online sulla porta 42345

[SISTEMA] Server avviato sulla porta 42345
[SISTEMA] Nuovo client connesso: 127.0.0.1
[SISTEMA] Nuovo client connesso: 127.0.0.1

HH:mm:ss - Server: Ciao ragazzi, benvenuti!
HH:mm:ss - Client: Ciao server!
HH:mm:ss - Client: Sono il secondo client


[Client 1]
🟢 Connesso al server

[SISTEMA] Connesso al server localhost:42345

HH:mm:ss - Server: Ciao ragazzi, benvenuti!
HH:mm:ss - Client: Ciao server!

HH:mm:ss - Client: Sono il secondo client


[Client 2]
🟢 Connesso al server

[SISTEMA] Connesso al server localhost:42345

HH:mm:ss - Server: Ciao ragazzi, benvenuti!
HH:mm:ss - Client: Ciao server!

HH:mm:ss - Client: Sono il secondo client
```

---

## Protocollo di Comunicazione

### Socket TCP/IP

**Caratteristiche**:
- 🔌 Connessione affidabile (TCP)
- 📍 Indirizzo: localhost (127.0.0.1)
- 🔢 Porta: 42345
- 📊 Protocollo: testo (linee terminate con \n)

### Formato Messaggi

```
Client → Server → Broadcast
┌─────────────────────────────────┐
│ String msg (UTF-8)              │
│ Terminato con \n                │
│ Letto con BufferedReader.readline()
└─────────────────────────────────┘
```


## Miglioramenti Grafici Professionali

Questa versione introduce un design **professionale e moderno** per l'interfaccia grafica:

#### Palette Colori Professionale

```java
private static final Color PRIMARY_COLOR = new Color(33, 150, 243);      // Blu #2196F3
private static final Color SECONDARY_COLOR = new Color(76, 175, 80);     // Verde #4CAF50
private static final Color DARK_BG = new Color(240, 242, 245);           // Grigio #F0F2F5
private static final Color SERVER_MSG = new Color(200, 224, 255);        // Blu chiaro
private static final Color CLIENT_MSG = new Color(220, 237, 200);        // Verde chiaro
```

#### Componenti Stilizzati

**1. Pannello Superiore (`createTopPanel`)**
- 📌 Titolo principale in Blu (#2196F3), Font Segoe UI Bold 18pt
- 🔌 Indicatore stato connessione (● online/offline)
- 👥 Contatore client connessi in tempo reale
- 📐 Padding e margini coerenti

**2. Area Messaggi (`JTextPane`)**
- 📝 Usa `JTextPane` al posto di `JTextArea` per supportare colori
- 🎨 Messaggi con sfondo colorato: Blu per Server, Verde per Client
- 🕐 Timestamp automatico HH:mm:ss
- 📏 Font Segoe UI 12pt, margini interni con padding

**3. Pannello Inferiore (`createSouthPanel`)**
- ⌨️ Input field con border blu spesso 1px
- 🎯 Bottone "Invia" con:
  - Sfondo Blu (#2196F3)
  - Testo bianco bold
  - Effetto cursor mano al passaggio
  - Padding: 8px superiore/inferiore, 20px sinistro/destro
- 📏 Bordo `CompoundBorder` (LineBorder + EmptyBorder)

#### Gestione UI Thread-Safe

Tutte le operazioni di aggiornamento UI ricevono i dati attuali:

```java
public void append(String msg, Color bgColor) {
    SwingUtilities.invokeLater(() -> {
        javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setBackground(attrs, bgColor);
        javax.swing.text.StyleConstants.setForeground(attrs, new Color(40, 40, 40));
        javax.swing.text.StyleConstants.setFontFamily(attrs, "Segoe UI");
        javax.swing.text.StyleConstants.setFontSize(attrs, 12);
        area.getDocument().insertString(area.getDocument().getLength(), msg, attrs);
    });
}
```

#### Layout Responsive

- **BorderLayout**: Divisione principale in 3 zone (North/Center/South)
- **Margini (EmptyBorder)**: 10px su tutti i lati
- **Spacing**: 8-10px tra componenti
- **Ridimensionamento**: Finestre 600x500 (150% delle originali)

#### Accessibilità

- ✅ Font leggibile: Segoe UI (Non default Arial)
- ✅ Contrasto colori WCAG compatibile
- ✅ Icone emoji per chiarezza (● ◉ ✓)
- ✅ Messaggi di errore in rosso evidente (#C83232)
- ✅ Focus ring su bottoni (cursor mano)

---

## Dettagli Tecnici del Rendering

### Threading

#### Perché è necessario il threading?

I socket sono operazioni **bloccanti**:
- `serverSocket.accept()` - blocca finché non arriva un client
- `in.readLine()` - blocca finché non riceve dati

Senza threading, l'applicazione si fermerebbe durante queste operazioni.

#### Threading nel Server

```java
// Server principale (Thread 1)
connectToServer() {
    new Thread(() -> {  // Thread 2
        while(true) {
            Socket socket = serverSocket.accept();
            ThreadClient tc = new ThreadClient(socket, this);
            tc.start();  // Thread 3, 4, 5... per ogni client
        }
    }).start();
}
```

**Struttura Thread**:
- **Main Thread**: Gestisce la UI
- **Connection Thread**: Loop di accettazione connessioni
- **Client Threads** (uno per client): Gestisce comunicazione specifica

#### Problema: Race Conditions

Quando più thread modificano `clientConnessi` contemporaneamente:

```java
// PERICOLOSO - Race condition
for (ThreadClient client : clientConnessi) {
    client.inviaMsgAlClient(msg);
}
clientConnessi.remove(this);
```

**Soluzione**: Usare `synchronized` o `CopyOnWriteArrayList`

### Thread-Safety dell'UI

```java
// ✅ CORRETTO - UI updates nel Event Dispatch Thread
SwingUtilities.invokeLater(() -> area.append(msg));

// ❌ SBAGLIATO - accesso diretto da altro thread
area.append(msg);  // Può causare inconsistenze visive
```

### Gestione delle Risorse

```java
// Problema: risorse non chiuse
while ((msg = in.readLine()) != null) {
    // ...
}
// Se il ciclo si interrompe, socket non viene chiuso!

// Soluzione: try-with-resources (Java 7+)
try (Socket socket = serverSocket.accept();
     BufferedReader in = new BufferedReader(...);
     PrintWriter out = new PrintWriter(...)) {
    // Il codice qui...
} // Automaticamente chiude tutte le risorse
```

---

## Pattern di Programmazione

### 1. Model-View-Controller (MVC)

Utilizzato nella **VersioneRam**:

```
Controller (Client.java, Server2ChatGpt.java)
    │
    ├─→ Model (Logica di socket)
    │
    └─→ View (GUI.java, FinestraServer.java)
```

**Vantaggi**:
- Separazione responsabilità
- Testabilità
- Riusabilità

### 2. Producer-Consumer

Il server funge da **intermediario**:

```
Client 1 →┐
          ├→ Server (Consumer/Producer) → Broadcast
Client 2 →┘
```

### 3. Thread Pool (Potenziale Evoluzione)

Attualmente: Un thread per client
Evolved: `ExecutorService` con pool limitato

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.execute(new ThreadClient(socket, this));
```

### 4. Singleton Pattern (Potenziale)

Assicurare una sola istanza di Server:

```java
public class Server {
    private static Server instance;
    
    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }
}
```

### 5. Observer Pattern (Potenziale)

Notificare i client su eventi del server:

```java
interface ServerListener {
    void onClientConnected(Socket socket);
    void onClientDisconnected(Socket socket);
    void onMessageReceived(String msg);
}
```

---

## Diagrammi UML

### Diagramma delle Classi (Versione Principale)

```
┌─────────────────────────────┐
│        Server               │
│   extends JFrame            │
├─────────────────────────────┤
│ - serverSocket              │
│ - area: JTextArea           │
│ - input: JTextField         │
│ + clientConnessi            │
├─────────────────────────────┤
│ + connectToServer()         │
│ + sendMessage()             │
│ + append(String)            │
│ + main(String[])            │
└──────────────┬──────────────┘
               │ creates
               │ many
               ▼
┌─────────────────────────────┐
│      ThreadClient           │
│   extends Thread            │
├─────────────────────────────┤
│ - socket: Socket            │
│ - server: Server            │
│ - in: BufferedReader        │
│ - out: PrintWriter          │
├─────────────────────────────┤
│ + run()                     │
│ + inviaMsgAlClient(String)  │
└─────────────────────────────┘


┌─────────────────────────────┐
│       Client2               │
│   extends JFrame            │
├─────────────────────────────┤
│ - socket: Socket            │
│ - in: BufferedReader        │
│ - out: PrintWriter          │
│ - area: JTextArea           │
│ - input: JTextField         │
├─────────────────────────────┤
│ + connectToServer()         │
│ + sendMessage()             │
│ + append(String)            │
│ + main(String[])            │
└─────────────────────────────┘

Client2 --connects-to--> Server (via Socket)
```
---

## Troubleshooting

### Problema: "Port already in use"

```
java.net.BindException: Address already in use
```

**Soluzione**:
```bash
# Trovare processo sulla porta 42345
lsof -i :42345

# Terminare il processo
kill -9 <PID>

# Oppure utilizzare una porta diversa
```

### Problema: "Connection refused"

Il client non riesce a connettersi al server.

**Verificare**:
- Server è avviato?
- Corretto indirizzo IP?
- Corretta porta (42345)?
- Firewall blocca la porta?

### Problema: GUI bloccata

L'interfaccia non risponde durante l'invio/ricezione.

**Causa**: Operazioni di rete nel main thread
**Soluzione**: Già implementata - usare thread separati

### Problema: Messaggi non ricevuti

Verificare il protocollo:
```java
// Assicurarsi di terminare con newline
out.println(msg);  // ✅ Include newline
out.print(msg);    // ❌ Non include newline
```

---

## Note di Documentazione

Questa documentazione è stata creata come **capo lavoro** per demonstrare:

✅ **Competenze Tecniche**:
- Programmazione Java avanzata
- Networking (Socket TCP/IP)
- GUI Swing
- Threading e concorrenza
- Gestione risorse
- Architetture software

✅ **Best Practices**:
- Code organization
- Documentation completezza
- Error handling
- Thread-safety
- Design patterns

✅ **Qualità di Documentazione**:
- Diagrammi chiari
- Esempi di codice
- Spiegazioni dettagliate
- Troubleshooting

---

## Autore

**Progetto**: Applicazione Chat via Socket TCP/IP  
**Data**: Aprile 2026  
**Corso**: TPSI (Tecnologie e Programmazione Sistemi Informatici)

---

## Licenza

Libero per uso educativo e didattico.

---
