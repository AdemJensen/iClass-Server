package top.chorg.kernel.server.base;

import java.io.BufferedReader;

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
