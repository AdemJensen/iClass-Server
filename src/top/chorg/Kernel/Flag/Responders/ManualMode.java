package top.chorg.Kernel.Flag.Responders;

import top.chorg.Kernel.Flag.FlagResponder;
import top.chorg.System.Global;

public class ManualMode extends FlagResponder {
    @Override
    public int response() {
        Global.setVar("AUTO_START_SERVICE", false);
        return 0;
    }

    @Override
    public String getManual() {
        return "To start the server without services start automatically.";
    }

}
