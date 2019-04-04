package top.chorg.Kernel.Server.Base;

import top.chorg.System.Sys;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class Client {
    public Socket socket;
    public PrintWriter writer;
    public BufferedReader reader;

    public ClientReceiverBase receiver;
    public ClientSenderBase sender;

    public Client(Socket soc, BufferedReader br, PrintWriter pw, Class<?> receiver, Class<?> sender) {
        this.socket = soc;
        this.writer = pw;
        this.reader = br;
        try {
            this.receiver = (ClientReceiverBase) receiver.getDeclaredConstructor(
                    Client.class,
                    BufferedReader.class
            ).newInstance(this, reader);
            this.sender = (ClientSenderBase) sender.getDeclaredConstructor(
                    Client.class,
                    PrintWriter.class
            ).newInstance(this, writer);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Sys.err("Client", "Invalid receiver or sender thread!");
            Sys.exit(19);
        }
    }

    public boolean isConnected() {
        return this.socket.isConnected();
    }
}
