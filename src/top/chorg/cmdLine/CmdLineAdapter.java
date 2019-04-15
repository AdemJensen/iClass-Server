package top.chorg.cmdLine;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Scanner;

public class CmdLineAdapter {
    public static void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to iClass Server.");
        System.out.printf("System running under command line mode (Ver %s).\n", Global.getVar("VERSION"));
        while (true) {
            outputDecoration();
            String cmd = sc.nextLine();
            if (cmd.equals("exit")) break;
            String[] args = cmd.split(" ");
            if (args.length == 0 || args[0].length() == 0) continue;
            if (!Global.cmdManPublic.cmdExists(args[0])) {
                Sys.errF("CMD", "Command '%s' not found.", args[0]);
                continue;
            }
            CmdResponder responderObj = Global.cmdManPublic.execute(args);
            if (responderObj == null) {
                Sys.err("Cmd Line", "Responder error: Unable to create responder instance.");
                Sys.exit(14);
            } else {
                while (responderObj.isAlive());
            }
        }
        Global.cmdManPublic.execute("stop");
    }

    public static void outputDecoration() {
        if (Global.cmdServer == null) System.out.print("[Server not established] >>> ");
        else System.out.printf("[%d Active Clients] >>> ", Global.cmdServer.getActiveClientNum());
    }
}
