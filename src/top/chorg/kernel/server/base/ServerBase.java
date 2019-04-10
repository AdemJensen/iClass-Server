package top.chorg.kernel.server.base;

import top.chorg.kernel.server.base.api.Message;
import top.chorg.support.Timer;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerBase extends Thread {
    private HashMap<Integer, Client> records = new HashMap<>();
    private ConnectionAuthenticatorLambda validationMethod;
    private ConnectionRequestReceiver connectionRequestReceiver;
    private ServerSocket serverSocket;
    private int port;
    private Class<?> clientReceiver, clientSender;

    public ServerBase(int port, Class<?> clientReceiver, Class<?> clientSender,
                      ConnectionAuthenticatorLambda validationMethod) {
        this.port = port;
        this.clientReceiver = clientReceiver;
        this.clientSender = clientSender;
        this.validationMethod = validationMethod;
        connectionRequestReceiver = new ConnectionRequestReceiver();
        connectionRequestReceiver.start();
    }

    public boolean sendMessage(int to, Message message) {
        return records.get(to).sender.send(message.encode());
    }

    public void bringOffline(int id) {
        if (!records.containsKey(id)) {
            Sys.err("Bring Offline", "Client not online.");
            return;
        }
        records.get(id).bringOffline();
        records.remove(id);
    }

    public boolean isOnline(int id) {
        if (records.containsKey(id)) {
            if (records.get(id).isConnected()) return true;
            else {
                records.remove(id);
                return false;
            }
        } else return false;
    }

    @Override
    public void interrupt() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Sys.warn("Server", "Something happened while closing server socket (IOException).");
        }
        super.interrupt();
    }

    public int getActiveClientNum() {
        return records.size();
    }

    public boolean serverIsRunning() {
        return connectionRequestReceiver.isAlive();
    }

    private class ConnectionRequestReceiver extends Thread {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);// 监听客户端的连接
                while (true) {
                    Socket clientSocket = serverSocket.accept(); // 阻塞
                    ConnectionAuthenticator validator = new ConnectionAuthenticator(clientSocket, validationMethod, records);
                    new Timer(6000, (Object[] args) -> {
                        if (validator.isAlive() && !validator.getStatus().equals("done")) {
                            this.interrupt();
                            Sys.devInfoF(
                                    "Server",
                                    "A client authentication has timed out (Status : %s).",
                                    validator.getStatus()
                            );
                            return 3;
                        }
                        return 0;
                    });
                }
            } catch (IOException e) {
                Sys.err("Server", "A server thread has crashed!" );
                System.exit(1);
            }
        }
    }


    public interface ConnectionAuthenticatorLambda {
        int auth(BufferedReader reader, PrintWriter writer);
    }
    private class ConnectionAuthenticator extends Thread {
        private String status = "idle";
        private Socket target;
        private HashMap<Integer, Client> records;
        private int returnVal = (int) Global.getVar("PROCESS_RETURN");
        private ConnectionAuthenticatorLambda func;

        public ConnectionAuthenticator(Socket target, ConnectionAuthenticatorLambda func, HashMap<Integer, Client> map) {
            this.target = target;
            this.func = func;
            this.records = map;
            this.start();
        }

        @Override
        public void run() {
            try {
                Sys.devInfo("Server", "A client is attempting to connect.");
                BufferedReader checker = new BufferedReader(new InputStreamReader(target.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(target.getOutputStream()));
                this.status = "authenticating";
                returnVal = this.func.auth(checker, writer);
                this.status = "done";
                if (returnVal > 0) {
                    records.put(
                            returnVal,
                            new Client(returnVal, target, checker, writer, clientReceiver, clientSender)
                    );
                    returnVal = 0;
                } else {
                    // Sys.devInfo("Server", "A client sent invalid authentication info.");
                    returnVal = 3;
                }
            } catch (IOException e) {
                Sys.devInfo("Server", "A client authentication has failed.");
                returnVal = 2;
                this.status = "fail";
            }
        }

        public String getStatus() {
            return this.status;
        }

        public int getReturnVal() {
            return this.returnVal;
        }
    }
}
