package top.chorg.kernel.cmd.privateResponders.vote;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.VoteQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.vote.FetchInfoResult;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class FetchInfo extends CmdResponder {

    public FetchInfo(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        try {
            int request = Objects.requireNonNull(nextArg(int.class));
            FetchInfoResult vote = VoteQueryState.fetchInfo(request, client);
            if (vote == null) {
                Sys.devInfoF("Fetch Vote Info", "Client(%d) has sent invalid request.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-fetchVoteInfo",
                                "Vote not exist"
                        )
                );
                return 3;
            }
            int clientLevel = UserQueryState.getLevelInClass(client, vote.classId);
            if (clientLevel < vote.level) {
                Sys.devInfoF("Fetch Vote Info", "Client(%d) is not authorized to do this.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-fetchVoteInfo",
                                "Permission denied"
                        )
                );
                return 4;
            }
            if (!Global.cmdServer.sendMessage(client, new Message(
                    "R-fetchVoteInfo",
                    Global.gson.toJson(VoteQueryState.fetchInfo(request, client))
            ))) {
                Sys.errF("Fetch Vote Info", "Error while sending results to Client(%d).", client);
                return 1;
            }
        } catch (NullPointerException e) {
            Sys.devInfoF("Fetch Vote Info", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-fetchVoteInfo",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        return 0;
    }

}

