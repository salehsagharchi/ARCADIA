package Game.Network;

import java.io.*;
import java.net.Socket;

/**
 * @author SALEHSAGHARCHI
 * Date: 2018-07-17
 * Time: 3:25 AM
 */
public class Client extends NetworkPlayer {
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private PrintStream printStream;
    private Temp temp;
    private boolean isAvailable;
    private Thread inputThread;
    private String name;

    private boolean isClassInServerManager;

    public Client(Socket socket, boolean isClassInServerManager) {
        setSocket(socket);
        this.isClassInServerManager = isClassInServerManager;
        name = "";
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Thread getInputThread() {
        return inputThread;
    }

    public void setInputThread(Thread inputThread) {
        this.inputThread = inputThread;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            printStream = new PrintStream(outputStream, true);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectInputStream = new ObjectInputStream(inputStream);
            temp = new Temp(objectOutputStream);
            isAvailable = true;
        } catch (IOException e) {
            isAvailable = false;
            e.printStackTrace();
        }
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public Temp getPrintStream() {
        //   return printStream;
        return temp;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        if (isClassInServerManager) {
            return ("[Client] : \"" + getName() + "\"" + " IP:" + getSocket().getInetAddress() + " Port:" + getSocket().getPort()).trim();
        } else {
            return ("[Client] : \"" + getName() + "\"" + " IP:" + getSocket().getLocalAddress() + " Port:" + getSocket().getLocalPort()).trim();
        }
    }
}
