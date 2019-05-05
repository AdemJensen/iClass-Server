package top.chorg.kernel.cmd.privateResponders.vote;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.LogUpdateState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.VoteQueryState;
import top.chorg.kernel.database.VoteUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.vote.FetchInfoResult;
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
            FetchInfoResult vote = VoteQueryState.fetchInfo(request, client);
            if (vote == null) {
                Sys.devInfoF("Del Vote", "Client(%d) has sent invalid request.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-delVote",
                                "Vote not exist"
                        )
                );
                return 3;
            }
            int clientLevel = UserQueryState.getLevelInClass(client, vote.classId);
            if (clientLevel == 0 || clientLevel < vote.level) {
                Sys.devInfoF("Del Vote", "Client(%d) is not authorized to do this.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-delVote",
                                "Permission denied"
                        )
                );
                return 4;
            }
            if (!VoteUpdateState.deleteVote(request)) {
                Sys.devInfoF("Del Vote", "Operation changed nothing.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-delVote",
                                "Unknown (Nothing changed)"
                        )
                );
                return 6;
            }
            Global.cmdServer.sendMessage(client, new Message(
                            "R-delVote",
                            "OK"
                    )
            );
            LogUpdateState.addLog(client, VoteQueryState.fetchInfo(request, client).classId,
                    "deleted vote <<" + VoteQueryState.fetchInfo(request, client).title + "(" + request + ")>>");
        } catch (NullPointerException e) {
            Sys.devInfoF("Del Vote", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-delVote",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        return 0;
    }

}
