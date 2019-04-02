package top.chorg.Kernel.Server.Base;

import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

public abstract class ClientSenderBase {
    protected Client clientObj;
    protected PrintWriter pw;

    public ClientSenderBase(Client clientObj, PrintWriter pw) {
        this.clientObj = clientObj;
        this.pw = pw;
    }

    public abstract boolean send(Serializable msg);

}
