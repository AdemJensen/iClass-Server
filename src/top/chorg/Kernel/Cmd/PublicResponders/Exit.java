package top.chorg.Kernel.Cmd.PublicResponders;

import top.chorg.Kernel.Cmd.CmdResponder;
import top.chorg.System.Global;
import top.chorg.System.Sys;

import java.io.Serializable;

public class Exit extends CmdResponder {

    public Exit(Serializable args) {
        super(args);
    }

    @Override
    public int response() {
        if (Global.cmdServer.isAlive()) Global.cmdServer.interrupt();
        if (Global.fileServer.isAlive()) Global.fileServer.interrupt();
        Sys.info("Cmd Line", "Command line is closing now.");
        Sys.exit(0);
        return 0;
    }

    @Override
    public String getManual() {
        return "Exit the program with value 0.";
    }
}
