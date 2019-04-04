package top.chorg.Kernel.Cmd.PublicResponders;

import top.chorg.Kernel.Cmd.CmdResponder;
import top.chorg.Kernel.Communication.Message;
import top.chorg.System.Global;
import top.chorg.System.Sys;

import java.io.Serializable;

public class StartServer extends CmdResponder {

    public StartServer(Serializable args) {
        super(args);
    }

    @Override
    public int response() {
        String[] arr = (String[]) args;
        if (arr.length < 1) {
            Sys.err("Starter", "Too few arguments, type 'help start' for more information.");
        }
        CmdResponder res = Global.cmdManPrivate.execute(new Message(
                "start",
                arr
        ));
        while (res.isAlive()) { }
        return 0;
    }

    @Override
    public String getManual() {
        return "Start the server functions. Use 'start all' to start all the services, start 'cmdSrv' for cmdServer, " +
                "'fileSrv' for fileServer, and 'db' for database connection.";
    }

}
