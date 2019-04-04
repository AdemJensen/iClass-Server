package top.chorg.system;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.flag.FlagManager;
import top.chorg.kernel.flag.responders.*;
import top.chorg.support.StringArrays;

/**
 * Master initializer, register all the global variables and responders.
 * Fully static, no need to make instance.
 */
public class Initializer {
    private static void DEV_PRE_OPERATIONS() {  // Development operations
        Global.conf.Cmd_Server_Port = 9998;
        Global.conf.File_Server_Port = 9999;
        Global.conf.DB_Url = "jdbc:mysql://localhost:3306/DB_iClass?useSSL=false";
        Global.conf.DB_Port = 3306;
        Global.conf.DB_Username = "master";
        Global.conf.DB_Password = "123456";
        Global.conf.save();
        Global.conf.saveDefault();
        Global.conf.load();
    }

    /**
     * Master initialization execution method.
     *
     * @param flagList The arg list passed in at main().
     */
    public static void execute(String[] flagList) {
        // TODO: Plugin loading.

        registerGlobalVariables();

        //DEV_PRE_OPERATIONS();

        if (!Global.conf.load()) {
            Sys.warn("Init", "Unable to load config file, using default config file instead.");
            if (!Global.conf.loadDefault()) {
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
            CmdResponder res = Global.cmdManPrivate.execute(StringArrays.assemble("start", new String[]{"all"}));
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
        Global.setVar("CONF_FILE", "config.json");                      // Config file name.
        Global.setVar("DEFAULT_CONF_FILE", "config.default.json");      // Default config file name.

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

        Global.cmdManPrivate.register("start", top.chorg.kernel.cmd.privateResponders.StartServer.class);

    }

    /**
     * Public command Responder register.
     * Register all the command responders.
     */
    private static void registerCommands() {

        Global.cmdManPublic.register("exit", top.chorg.kernel.cmd.publicResponders.Exit.class);
        Global.cmdManPublic.register("stop", top.chorg.kernel.cmd.publicResponders.Exit.class);

        Global.cmdManPublic.register("help", top.chorg.kernel.cmd.publicResponders.Help.class);
        Global.cmdManPublic.register("man", top.chorg.kernel.cmd.publicResponders.Help.class);

        Global.cmdManPublic.register("start", top.chorg.kernel.cmd.publicResponders.StartServer.class);

    }

}
