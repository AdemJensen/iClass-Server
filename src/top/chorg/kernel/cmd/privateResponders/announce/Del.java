package top.chorg.kernel.cmd.privateResponders.announce;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.AnnounceQueryState;
import top.chorg.kernel.database.AnnounceUpdateState;
import top.chorg.kernel.database.LogUpdateState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.announcements.FetchListResult;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class Del extends CmdResponder {

    public Del(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        try {
            int request = Objects.requireNonNull(nextArg(int.class));
            FetchListResult announce = AnnounceQueryState.fetchListById(request);
            if (announce == null) {
                Sys.devInfoF("Del Announce", "Client(%d) has sent invalid request.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-delAnnounce",
                                "Announcement not exist"
                        )
                );
                return 3;
            }
            int clientLevel = UserQueryState.getLevelInClass(client, announce.classId);
            if (clientLevel == 0 || clientLevel < announce.level) {
                Sys.devInfoF("Del Announce", "Client(%d) is not authorized to do this.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-delAnnounce",
                                "Permission denied"
                        )
                );
                return 4;
            }
            if (!AnnounceUpdateState.deleteAnnounce(request)) {
                Sys.devInfoF("Del Announce", "Operation changed nothing.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-delAnnounce",
                                "Unknown (Nothing changed)"
                        )
                );
                return 6;
            }
            Global.cmdServer.sendMessage(client, new Message(
                            "R-delAnnounce",
                            "OK"
                    )
            );
            LogUpdateState.addLog(client, AnnounceQueryState.fetchListById(request).classId,
                    "deleted announcement <<" + AnnounceQueryState.fetchListById(request).title + "(" + request + ")>>");
        } catch (NullPointerException e) {
            Sys.devInfoF("Del Announce", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-delAnnounce",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        return 0;
    }

}
