package top.chorg.kernel.cmd.privateResponders;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.AnnounceQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.announcements.FetchListRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class FetchAnnounceList extends CmdResponder {

    public FetchAnnounceList(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        FetchListRequest request = nextArg(FetchListRequest.class);
        if (request == null) {
            Sys.devInfoF("Fetch Announce", "Client(%d) has sent invalid request.", client);
            return 2;
        }
        if (request.publisherId == 0) {
            if (!Global.cmdServer.sendMessage(client, new Message(
                    "R-fetchAnnounceList",
                    Global.gson.toJson(AnnounceQueryState.fetchList(
                            request.classId, request.level
                    ))
            ))) {
                Sys.errF("Fetch Announce", "Error while sending results to Client(%d).", client);
                return 1;
            }
        } else {
            if (!Global.cmdServer.sendMessage(client, new Message(
                    "R-fetchAnnounceList",
                    Global.gson.toJson(AnnounceQueryState.fetchList(
                            request.publisherId
                    ))
            ))) {
                Sys.errF("Fetch Announce", "Error while sending results to Client(%d).", client);
                return 1;
            }
        }

        return 0;
    }

}
