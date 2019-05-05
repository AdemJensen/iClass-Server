package top.chorg.kernel.cmd.privateResponders.announce;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.TemplateQueryState;
import top.chorg.kernel.database.TemplateUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.announcements.AlterTemplateRequest;
import top.chorg.kernel.server.base.api.announcements.FetchTemplateResult;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class AlterTemplate extends CmdResponder {

    public AlterTemplate(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        AlterTemplateRequest request = nextArg(AlterTemplateRequest.class);
        if (request == null) {
            Sys.devInfoF("Alter Template", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterTemplate",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        FetchTemplateResult template = TemplateQueryState.fetchTemplateById(request.id);
        if (template == null) {
            Sys.devInfoF("Alter Template", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterTemplate",
                            "Template not exist"
                    )
            );
            return 3;
        }
        if (!TemplateUpdateState.alterTemplate(request)) {
            Sys.devInfoF("Alter Template", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterTemplate",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-alterTemplate",
                        "OK"
                )
        );
        return 0;
    }
}
