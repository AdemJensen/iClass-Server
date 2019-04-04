package top.chorg.System;

import top.chorg.Kernel.Cmd.CmdResponder;
import top.chorg.Kernel.Flag.FlagManager;
import top.chorg.Kernel.Flag.Responders.*;
import top.chorg.Kernel.Communication.Message;

/**
 * Master initializer, register all the global variables and responders.
 * Fully static, no need to make instance.
 */
public class Initializer {
    private static void DEV_PRE_OPERATIONS() {  // Development operations
        Global.clearConfig();
        Global.setConfig("Cmd_Server_Port", 9998);
        Global.setConfig("File_Server_Port", 9999);
        Global.setConfig("DB_Url", "jdbc:mysql://localhost:3306/DB_iClass?useSSL=false");
        Global.setConfig("DB_Port", 3306);
        Global.setConfig("DB_Username", "master");
        Global.setConfig("DB_Password", "123456");
        Global.saveFileConfig();
        Global.saveDefaultConfig();
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

        if (((boolean) Global.getVar("AUTO_START_SERVICE"))) {
            CmdResponder res = Global.cmdManPrivate.execute(new Message(
                    "start",
                    new String[]{"all"}
            ));
            while (res.isAlive()) { }
        }
        Global.dropVar("AUTO_START_SERVICE");

    }

    /**
     * Global variable register.
     * Register all the initial global variables.
     */
    private static void registerGlobalVariables() {
        Global.setVar("VERSION", "0.0.2");    // Master system version.

        Global.setVar("AUTO_START_SERVICE", true);     // Start services automatically.

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
        FlagManager.register("-dev", new DevMode());
        FlagManager.register("-Dev", new DevMode());
        FlagManager.register("-DEV", new DevMode());

        FlagManager.register("-cmd", new CmdMode());
        FlagManager.register("-Cmd", new CmdMode());
        FlagManager.register("-CMD", new CmdMode());

        FlagManager.register("-gui", new GuiMode());
        FlagManager.register("-Gui", new GuiMode());
        FlagManager.register("-GUI", new GuiMode());

        FlagManager.register("-h", new Help());
        FlagManager.register("--help", new Help());

        FlagManager.register("-m", new ManualMode());
        FlagManager.register("--manual", new ManualMode());
    }

    /**
     * Private command Responder register.
     * Register all the command responders.
     */
    private static void registerPrivateCommands() {

        Global.cmdManPrivate.register("start", top.chorg.Kernel.Cmd.PrivateResponders.StartServer.class);

    }

    /**
     * Public command Responder register.
     * Register all the command responders.
     */
    private static void registerCommands() {

        Global.cmdManPublic.register("exit", top.chorg.Kernel.Cmd.PublicResponders.Exit.class);
        Global.cmdManPublic.register("stop", top.chorg.Kernel.Cmd.PublicResponders.Exit.class);

        Global.cmdManPublic.register("help", top.chorg.Kernel.Cmd.PublicResponders.Help.class);
        Global.cmdManPublic.register("man", top.chorg.Kernel.Cmd.PublicResponders.Help.class);

        Global.cmdManPublic.register("start", top.chorg.Kernel.Cmd.PublicResponders.StartServer.class);

    }

}
