package top.chorg.kernel.cmd.privateResponders.announce;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.AnnounceQueryState;
import top.chorg.kernel.database.AnnounceUpdateState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.announcements.AddRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class Add extends CmdResponder {

    public Add(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        AddRequest request = nextArg(AddRequest.class);
        if (request == null) {
            Sys.devInfoF("Add Announce", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-addAnnounce",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        int clientLevel = UserQueryState.getLevelInClass(client, request.classId);
        if (clientLevel == 0 || clientLevel < request.level) {
            Sys.devInfoF("Add Announce", "Client(%d) is not authorized to do this.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-addAnnounce",
                            "Permission denied"
                    )
            );
            return 5;
        }
        int updateRes = AnnounceUpdateState.addAnnounce(request, client);
        if (updateRes < 0) {
            Sys.devInfoF("Add Announce", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-addAnnounce",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-addAnnounce",
                        "OK"
                )
        );
        for (Integer user : Global.cmdServer.getOnlineUsers()) {
            if (UserQueryState.getLevelInClass(user, request.classId) >= request.level) {
                Sys.devInfoF("Add Announce", "Sent QTE message to %d.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "onNewAnnounce",
                                Global.gson.toJson(AnnounceQueryState.fetchListById(updateRes))
                        )
                );
                return 5;
            }
        }
        return 0;
    }
}
