package top.chorg.Kernel.Server.Base;

import java.io.BufferedReader;
import java.net.Socket;

public abstract class ClientReceiverBase extends Thread {
    protected Client clientObj;
    protected BufferedReader br;

    public ClientReceiverBase(Client clientObj, BufferedReader br) {
        this.clientObj = clientObj;
        this.br = br;
        this.start();
    }

    @Override
    public abstract void run();
}
