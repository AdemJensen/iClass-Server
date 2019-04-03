package top.chorg.Kernel.Cmd.PrivateResponders;

import top.chorg.Kernel.Cmd.CmdResponder;

import java.io.Serializable;

public class HandleLoginRequest extends CmdResponder {

    public HandleLoginRequest(Serializable args) {
        super(args);
    }

    @Override
    public int response() {

        return 0;
    }

}
