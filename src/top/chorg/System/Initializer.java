package top.chorg.System;

import top.chorg.Kernel.Flag.FlagManager;
import top.chorg.Kernel.Flag.Responders.*;

/**
 * Master initializer, register all the global variables and responders.
 * Fully static, no need to make instance.
 */
public class Initializer {
    private static void DEV_PRE_OPERATIONS() {  // Development operations
        Global.clearConfig();
        Global.setConfig("Cmd_Server_Host", "127.0.0.1");
        Global.setConfig("Cmd_Server_Port", 9999);
        Global.saveFileConfig();
        Global.loadFileConfig();
    }

    /**
     * Master initialization execution method.
     *
     * @param flagList The arg list passed in at main().
     */
    public static void execute(String[] flagList) {
        // TODO: Plugin loading.

        registerGlobalVariables();
        if (!Global.loadFileConfig()) {
            Sys.warn("Init", "Unable to load config file, using default config file instead.");
            if (!Global.loadDefaultConfig()) {
                Sys.err("Init", "Unable to load default config file.");
                Sys.exit(10);
            }
        }

        registerFlagResponders();
        registerPrivateCommands();
        registerCommands();

        FlagManager.execute(flagList);  // Start flag option processor.

        //DEV_PRE_OPERATIONS();

//        Connector.connect((String) Global.getConfig("Socket_Host"), (int) Global.getConfig("Socket_Port"));
//        Connector.disconnect();
    }

    /**
     * Global variable register.
     * Register all the initial global variables.
     */
    private static void registerGlobalVariables() {
        Global.setVar("VERSION", "0.0.2");    // Master system version.

        Global.setVar("DEV_MODE_KEY", "Theresa Apocalypse");    // Development mode key.
        Global.setVar("PROCESS_RETURN", -1);  // When a thread is in process, here is its default return value.

        Global.setVar("GUI_MODE", true);        // To determine current display mode.
        Global.setVar("DEV_MODE", false);       // To determine whether this is development mode or not.

        Global.setVar("LOG_ROUTE", "./logs");   // Route of log files.
        Global.setVar("CONF_ROUTE", ".");       // Route of route files.
        Global.setVar("CONF_FILE", "config.conf");                      // Config file name.
        Global.setVar("DEFAULT_CONF_FILE", "config.default.conf");      // Default config file name.

    }

    /**
     * Flag Responder register.
     * Register all the flag responders.
     */
    private static void registerFlagResponders() {
        FlagManager.register("-dev", new DevModeResponder());
        FlagManager.register("-Dev", new DevModeResponder());
        FlagManager.register("-DEV", new DevModeResponder());

        FlagManager.register("-cmd", new CmdModeResponder());
        FlagManager.register("-Cmd", new CmdModeResponder());
        FlagManager.register("-CMD", new CmdModeResponder());

        FlagManager.register("-gui", new GuiModeResponder());
        FlagManager.register("-Gui", new GuiModeResponder());
        FlagManager.register("-GUI", new GuiModeResponder());

        FlagManager.register("-h", new HelpResponder());
        FlagManager.register("--help", new HelpResponder());
    }

    /**
     * Private command Responder register.
     * Register all the command responders.
     */
    private static void registerPrivateCommands() {

    }

    /**
     * Public command Responder register.
     * Register all the command responders.
     */
    private static void registerCommands() {

        Global.cmdManPublic.register("exit", top.chorg.Kernel.Cmd.PublicResponders.ExitResponder.class);
        Global.cmdManPublic.register("stop", top.chorg.Kernel.Cmd.PublicResponders.ExitResponder.class);

        Global.cmdManPublic.register("help", top.chorg.Kernel.Cmd.PublicResponders.HelpResponder.class);
        Global.cmdManPublic.register("man", top.chorg.Kernel.Cmd.PublicResponders.HelpResponder.class);

    }

}
