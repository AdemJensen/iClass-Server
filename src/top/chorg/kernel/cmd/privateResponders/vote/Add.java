package top.chorg.kernel.cmd.privateResponders.vote;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.LogUpdateState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.VoteQueryState;
import top.chorg.kernel.database.VoteUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.vote.AddRequest;
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
            Sys.devInfoF("Add Vote", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-addVote",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        int clientLevel = UserQueryState.getLevelInClass(client, request.classId);
        if (clientLevel == 0 || clientLevel < request.level) {
            Sys.devInfoF("Add Vote", "Client(%d) is not authorized to do this.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-addVote",
                            "Permission denied"
                    )
            );
            return 5;
        }
        if (!VoteUpdateState.addVote(request, client)) {
            Sys.devInfoF("Add Vote", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-addVote",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-addVote",
                        "OK"
                )
        );
        LogUpdateState.addLog(client, request.classId, "added vote <<" + request.title + ">>");
        return 0;
    }
}