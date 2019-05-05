package top.chorg.kernel.server.cmdServer;

import top.chorg.kernel.server.base.Client;
import top.chorg.kernel.server.base.ClientSenderBase;

import java.io.PrintWriter;
import java.io.Serializable;

public class Sender extends ClientSenderBase {

    public Sender(Client clientObj, PrintWriter pw) {
        super(clientObj, pw);
    }

    @Override
    public boolean send(Serializable msg) {
        if (!clientObj.isConnected()) return false;
        pw.println(msg);
        pw.flush();
        return true;
    }
}
