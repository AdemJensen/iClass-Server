package top.chorg.Kernel.Server.Base;

import top.chorg.Support.Timer;
import top.chorg.System.Global;
import top.chorg.System.Sys;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerBase extends Thread {
    private HashMap<Integer, Client> records = new HashMap<>();
    ConnectionAuthenticatorLambda validationMethod;
    private ConnectionRequestReceiver connectionRequestReceiver;
    private int port;
    Class<?> clientReceiver, clientSender;

    public ServerBase(int port, Class<?> clientReceiver, Class<?> clientSender,
                      ConnectionAuthenticatorLambda validationMethod) {
        this.port = port;
        this.clientReceiver = clientReceiver;
        this.clientSender = clientSender;
        this.validationMethod = validationMethod;
        connectionRequestReceiver = new ConnectionRequestReceiver();
        connectionRequestReceiver.start();
    }

    public boolean sendMessage(int to, Serializable message) {
        return records.get(to).sender.send(message);
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
            ServerSocket serverSocket;// 创建服务器Socket对象
            try {
                serverSocket = new ServerSocket(port);// 监听客户端的连接
                Socket clientSocket = serverSocket.accept(); // 阻塞
                ConnectionAuthenticator validator = new ConnectionAuthenticator(clientSocket, validationMethod, records);
                validator.start();
            } catch (IOException e) {
                Sys.err("Server", "A server thread has crashed!");
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
                new Timer(6000, (Object[] args) -> {
                    if (this.isAlive() && !this.getStatus().equals("done")) {
                        this.interrupt();
                        Sys.devInfoF(
                                "Server",
                                "A client authentication has timed out (Status : %s).",
                                this.getStatus()
                        );
                        return 3;
                    }
                    return 0;
                });
                this.status = "authenticating";
                returnVal = this.func.auth(checker, writer);
                this.status = "done";
                if (returnVal > 0) {
                    records.put(returnVal, new Client(target, checker, writer, clientReceiver, clientSender));
                } else {
                    Sys.devInfo("Server", "A client sent invalid authentication info.");
                    returnVal = 3;
                }
                returnVal = 0;
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
