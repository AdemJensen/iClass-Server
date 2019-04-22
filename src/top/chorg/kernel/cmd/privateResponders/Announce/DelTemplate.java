package top.chorg.kernel.cmd.privateResponders.announce;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.TemplateQueryState;
import top.chorg.kernel.database.TemplateUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class DelTemplate extends CmdResponder {

    public DelTemplate(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        try {
            int request = Objects.requireNonNull(nextArg(int.class));
            boolean template = TemplateQueryState.belongsTo(request, client);
            if (!template) {
                Sys.devInfoF("Del Template", "Client(%d) has sent invalid request.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-delAnnounceTemplate",
                                "Template not exist or permission denied"
                        )
                );
                return 3;
            }
            if (!TemplateUpdateState.deleteTemplate(request)) {
                Sys.devInfoF("Del Template", "Operation changed nothing.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-delAnnounceTemplate",
                                "Unknown (Nothing changed)"
                        )
                );
                return 6;
            }
            Global.cmdServer.sendMessage(client, new Message(
                            "R-delAnnounceTemplate",
                            "OK"
                    )
            );
        } catch (NullPointerException e) {
            Sys.devInfoF("Del Template", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-delAnnounceTemplate",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        return 0;
    }
}
