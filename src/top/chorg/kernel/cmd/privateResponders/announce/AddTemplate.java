package top.chorg.kernel.cmd.privateResponders.announce;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.TemplateUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.announcements.AddTemplateRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class AddTemplate extends CmdResponder {

    public AddTemplate(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        AddTemplateRequest request = nextArg(AddTemplateRequest.class);
        if (request == null) {
            Sys.devInfoF("Add Template", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-addTemplate",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        if (!TemplateUpdateState.addTemplate(request, client)) {
            Sys.devInfoF("Add Template", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-addTemplate",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-addTemplate",
                        "OK"
                )
        );
        return 0;
    }
}

