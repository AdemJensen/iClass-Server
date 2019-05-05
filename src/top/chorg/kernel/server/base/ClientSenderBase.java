package top.chorg.kernel.server.base;

import java.io.PrintWriter;
import java.io.Serializable;

public abstract class ClientSenderBase {
    protected Client clientObj;
    protected PrintWriter pw;

    public ClientSenderBase(Client clientObj, PrintWriter pw) {
        this.clientObj = clientObj;
        this.pw = pw;
    }

    public abstract boolean send(Serializable msg);

}
