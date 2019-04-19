package top.chorg.kernel.cmd.privateResponders.Announce;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.TemplateQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class FetchTemplate extends CmdResponder {

    public FetchTemplate(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        if (!Global.cmdServer.sendMessage(client, new Message(
                "R-fetchAnnounceTemplate",
                Global.gson.toJson(TemplateQueryState.fetchTemplate(
                        client
                ))
        ))) {
            Sys.errF("Fetch Template", "Error while sending results to Client(%d).", client);
            return 1;
        }
        return 0;
    }

}
