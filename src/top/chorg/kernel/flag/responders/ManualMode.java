package top.chorg.kernel.flag.responders;

import top.chorg.kernel.flag.FlagResponder;
import top.chorg.system.Global;

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
