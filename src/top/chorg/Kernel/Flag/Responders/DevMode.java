package top.chorg.Kernel.Flag.Responders;

import top.chorg.Kernel.Flag.FlagManager;
import top.chorg.Kernel.Flag.FlagResponder;
import top.chorg.System.Global;
import top.chorg.System.Sys;

public class DevMode extends FlagResponder {

    @Override
    public int response() {
        if (Global.varExists("DEV_MODE_MODIFIED")) {
            Sys.warn(
                    "Flags",
                    "The dev mode have been set once! New tag ignored!"
            );
        }
        if (getArg().equals(Global.getVar("DEV_MODE_KEY"))) {
            System.out.println("The system has enabled development mode.");
            Global.setVar("DEV_MODE", true);
            Global.setVar("DEV_MODE_MODIFIED", true);
        } else {
            Sys.warn(
                    "Flags",
                    "The flag '" + FlagManager.getCurFlag() + "' have been ignored!"
            );
        }
        return 0;
    }

    @Override
    public String getManual() {
        return "Include this flag and enter development password to enable the development mode.";
    }

    @Override
    public void aftActions() {
        Global.dropVar("DEV_MODE_MODIFIED");
    }
}
