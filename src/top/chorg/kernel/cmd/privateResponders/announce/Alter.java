package top.chorg.kernel.cmd.privateResponders.announce;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.AnnounceQueryState;
import top.chorg.kernel.database.AnnounceUpdateState;
import top.chorg.kernel.database.LogUpdateState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.announcements.AlterRequest;
import top.chorg.kernel.server.base.api.announcements.FetchListResult;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class Alter extends CmdResponder {

    public Alter(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        AlterRequest request = nextArg(AlterRequest.class);
        if (request == null) {
            Sys.devInfoF("Alter Announce", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                    "R-alterAnnounce",
                    "Parameter incomplete"
                    )
            );
            return 2;
        }
        FetchListResult announce = AnnounceQueryState.fetchListById(request.id);
        if (announce == null) {
            Sys.devInfoF("Alter Announce", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterAnnounce",
                            "Announcement not exist"
                    )
            );
            return 3;
        }
        int clientLevel = UserQueryState.getLevelInClass(client, announce.classId);
        if (clientLevel == 0 || clientLevel < announce.level) {
            Sys.devInfoF("Alter Announce", "Client(%d) is not authorized to do this (old).", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterAnnounce",
                            "Permission denied (old class)"
                    )
            );
            return 4;
        }
        clientLevel = UserQueryState.getLevelInClass(client, request.classId);
        if (clientLevel == 0 || clientLevel < announce.level || clientLevel < request.level) {
            Sys.devInfoF("Alter Announce", "Client(%d) is not authorized to do this (target).", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterAnnounce",
                            "Permission denied (target class)"
                    )
            );
            return 5;
        }
        if (!AnnounceUpdateState.alterAnnounce(request)) {
            Sys.devInfoF("Alter Announce", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterAnnounce",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-alterAnnounce",
                        "OK"
                )
        );
        for (Integer user : Global.cmdServer.getOnlineUsers()) {
            if (UserQueryState.getLevelInClass(user, request.classId) >= request.level) {
                Sys.devInfoF("Add Announce", "Sent QTE message to %d.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "onNewAnnounce",
                                Global.gson.toJson(AnnounceQueryState.fetchListById(request.id))
                        )
                );
            }
        }
        LogUpdateState.addLog(client, request.classId, "altered announcement <<" +
                AnnounceQueryState.fetchListById(request.id).title + "(" + request.id + ")>>");
        return 0;
    }
}
