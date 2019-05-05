package top.chorg.kernel.cmd.privateResponders;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.server.cmdServer.CmdServer;
import top.chorg.kernel.server.fileServer.FileServer;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.DriverManager;
import java.sql.SQLException;

public class StartServer extends CmdResponder {

    public StartServer(String...args) {
        super(args);
    }

    @Override
    public int response() {
        if (!hasNextArg()) {
            Sys.err("Starter", "Invalid parameter while starting server.");
            Sys.exit(20);
        }
        boolean startDb = false;
        boolean startFileSrv = false;
        boolean startCmdSrv = false;
        if (nextArg().equals("all")) {
            startDb = true;
            startFileSrv = true;
            startCmdSrv = true;
        } else {
            while (hasNextArg()) {
                switch (nextArg()) {
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
                        Sys.warn("Starter", "Ignored parameter while starting server.");
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
        Global.fileServer = new FileServer(Global.conf.File_Server_Port);
        Global.fileServer.start();
        Sys.info("Starter", "File server successfully started.");
    }

    private void startCmdServer() {
        if (Global.cmdServer != null && Global.fileServer.isAlive()) {
            Sys.err("Starter", "Cmd server already running.");
            return;
        }
        Sys.info("Starter", "Starting Cmd server...");
        Global.cmdServer = new CmdServer(Global.conf.Cmd_Server_Port);
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
                    Global.conf.DB_Url,
                    Global.conf.DB_Username,
                    Global.conf.DB_Password
            );
        } catch (SQLException e) {
            Sys.err("DB", "Unable to connect to MySQL server.");
            return false;
        }
        Sys.info("Starter", "DB connection successfully established.");
        return true;
    }

}
