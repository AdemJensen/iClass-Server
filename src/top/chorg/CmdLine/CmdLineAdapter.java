package top.chorg.CmdLine;

import top.chorg.Kernel.Cmd.CmdResponder;
import top.chorg.Kernel.Server.Base.Message;
import top.chorg.System.Global;
import top.chorg.System.Sys;

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

            Message msg = new Message();
            msg.msgType = args[0];
            String[] content = new String[args.length - 1];
            System.arraycopy(args, 1, content, 0, args.length - 1);
            msg.content = content;
            CmdResponder responderObj = Global.cmdManPublic.execute(msg);

            if (responderObj == null) {
                Sys.err("Cmd Line", "Responder error: Unable to create responder instance.");
                Sys.exit(14);
            } else {
                while (responderObj.isAlive());
            }
        }
        Sys.info("Cmd Line", "Command line is closing now.");
    }

    public static void outputDecoration() {
        System.out.printf("[%d Active Clients] >>> ", 0);
    }
}
