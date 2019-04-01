package top.chorg.Kernel.Server.Base;

import java.io.PrintWriter;
import java.io.Serializable;

public class ClientSenderBase {
    PrintWriter pw;

    public ClientSenderBase(PrintWriter pw) {
        this.pw = pw;
    }

    public boolean send(Serializable msg) {
        return false;
    }

}
