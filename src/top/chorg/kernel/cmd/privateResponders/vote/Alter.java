package top.chorg.kernel.cmd.privateResponders.vote;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.LogUpdateState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.VoteQueryState;
import top.chorg.kernel.database.VoteUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.vote.AlterRequest;
import top.chorg.kernel.server.base.api.vote.FetchInfoResult;
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
            Sys.devInfoF("Alter Vote", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterVote",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        FetchInfoResult vote = VoteQueryState.fetchInfo(request.id, client);
        if (vote == null) {
            Sys.devInfoF("Alter Vote", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterVote",
                            "Vote not exist"
                    )
            );
            return 3;
        }
        int clientLevel = UserQueryState.getLevelInClass(client, vote.classId);
        if (clientLevel == 0 || clientLevel < vote.level) {
            Sys.devInfoF("Alter Vote", "Client(%d) is not authorized to do this (old).", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterVote",
                            "Permission denied (old class)"
                    )
            );
            return 4;
        }
        clientLevel = UserQueryState.getLevelInClass(client, request.classId);
        if (clientLevel == 0 || clientLevel < vote.level || clientLevel < request.level) {
            Sys.devInfoF("Alter Vote", "Client(%d) is not authorized to do this (target).", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterVote",
                            "Permission denied (target class)"
                    )
            );
            return 5;
        }
        if (!VoteUpdateState.alterVote(request, client)) {
            Sys.devInfoF("Alter Vote", "Operation has error occurred.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterVote",
                            "Unknown (error occurred)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-alterVote",
                        "OK"
                )
        );
        LogUpdateState.addLog(client, request.classId,
                "altered vote <<" + request.title + "(" + request.id + ")>>");
        return 0;
    }
}