package top.chorg.kernel.cmd.privateResponders;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.system.Global;

public class Logoff extends CmdResponder {

    public Logoff(String[] args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int clientId = Integer.parseInt(nextArg());
        Global.cmdServer.bringOffline(clientId);
        return 0;
    }
}
