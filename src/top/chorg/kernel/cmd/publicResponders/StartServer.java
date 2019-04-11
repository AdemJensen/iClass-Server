package top.chorg.kernel.cmd.publicResponders;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.support.StringArrays;
import top.chorg.system.Global;
import top.chorg.system.Sys;

public class StartServer extends CmdResponder {

    public StartServer(String[] args) {
        super(args);
    }

    @Override
    public int response() {
        if (!hasNextArg()) {
            Sys.err("Starter", "Too few arguments, type 'help start' for more information.");
            return 1;
        }
        CmdResponder res = Global.cmdManPrivate.execute(StringArrays.assemble("start", remainArgs()));
        while (res.isAlive()) { }
        return 0;
    }

    @Override
    public String getManual() {
        return "Start the server functions. Use 'start all' to start all the services, start 'cmdSrv' for cmdServer, " +
                "'fileSrv' for fileServer, and 'db' for database connection.";
    }

}
