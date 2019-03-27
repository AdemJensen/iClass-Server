package top.chorg;

import top.chorg.CmdLine.CmdLineAdapter;
import top.chorg.System.Initializer;
import top.chorg.System.Sys;

public class Main {

    public static void main(String[] args) {
        Initializer.execute(args);      // Start initialization.

        if (Sys.isCmdEnv()) {
            CmdLineAdapter.start();     // Start Command Line mode.
        } else {
            // TODO: Start GUI mode.
            System.out.println("NOT YET USABLE");
        }
    }
}
