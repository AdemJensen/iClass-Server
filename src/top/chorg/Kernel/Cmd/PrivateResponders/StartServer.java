package top.chorg.Kernel.Cmd.PrivateResponders;

import top.chorg.Kernel.Cmd.CmdResponder;
import top.chorg.Kernel.Server.CmdServer.CmdServer;
import top.chorg.Kernel.Server.FileServer.FileServer;
import top.chorg.System.Global;
import top.chorg.System.Sys;

import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StartServer extends CmdResponder {

    public StartServer(Serializable args) {
        super(args);
    }

    @Override
    public int response() {
        String[] vars = (String[]) args;
        if (vars.length < 1) {
            Sys.err("Starter", "Invalid parameter while starting server.");
            Sys.exit(20);
        }
        boolean startDb = false;
        boolean startFileSrv = false;
        boolean startCmdSrv = false;
        if (vars[0].equals("all")) {
            startDb = true;
            startFileSrv = true;
            startCmdSrv = true;
        } else {
            for (String var : vars) {
                switch (var) {
                    case "db":
                        startDb = true;
                        break;
                    case "fileSrv":
                        startFileSrv = true;
                        break;
                    case "cmdSrv":
                        startCmdSrv = true;
                        break;
                    default:
                        Sys.warnF("Starter", "Ignored parameter while starting server ('%s').", var);
                }
            }
        }
        if (startDb) {
            if (!startBDConnection()) {
                Sys.err("Starter", "Error while starting DB connection.");
            }
        }
        if (startCmdSrv) startCmdServer();
        if (startFileSrv) startFileServer();
        return 0;
    }

    private void startFileServer() {
        if (Global.fileServer != null && Global.fileServer.isAlive()) {
            Sys.err("Starter", "File server already running.");
            return;
        }
        Sys.info("Starter", "Starting File server...");
        Global.fileServer = new FileServer((int) Global.getConfig("File_Server_Port"));
        Sys.info("Starter", "File server successfully started.");
    }

    private void startCmdServer() {
        if (Global.cmdServer != null && Global.fileServer.isAlive()) {
            Sys.err("Starter", "Cmd server already running.");
            return;
        }
        Sys.info("Starter", "Starting Cmd server...");
        Global.cmdServer = new CmdServer((int) Global.getConfig("Cmd_Server_Port"));
        Sys.info("Starter", "Cmd server successfully started.");
    }

    private boolean startBDConnection() {
        if (Global.fileServer != null && Global.fileServer.isAlive()) {
            Sys.err("Starter", "DB Connection still alive, no need to start.");
            return false;
        }
        Sys.info("Starter", "Establishing DB connection...");
        try {
            Global.database = DriverManager.getConnection(
                    (String) Global.getConfig("DB_Url"),
                    (String) Global.getConfig("DB_Username"),
                    (String) Global.getConfig("DB_Password")
            );
        } catch (SQLException e) {
            Sys.err("DB", "Unable to connect to MySQL server.");
            return false;
        }
        Sys.info("Starter", "DB connection successfully established.");
        return true;
    }

}
