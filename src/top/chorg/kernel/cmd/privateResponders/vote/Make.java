package top.chorg.kernel.cmd.privateResponders.vote;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.VoteQueryState;
import top.chorg.kernel.database.VoteUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.vote.FetchInfoResult;
import top.chorg.kernel.server.base.api.vote.MakeRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class Make extends CmdResponder {

    public Make(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        MakeRequest request = nextArg(MakeRequest.class);
        if (request == null) {
            Sys.devInfoF("Make Vote", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-makeVote",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        FetchInfoResult vote = VoteQueryState.fetchInfo(request.voteId, client);
        if (vote == null) {
            Sys.devInfoF("Make Vote", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-makeVote",
                            "Vote not exist"
                    )
            );
            return 3;
        }
        int clientLevel = UserQueryState.getLevelInClass(client, vote.classId);
        if (clientLevel < vote.level) {
            Sys.devInfoF("Make Vote", "Client(%d) is not authorized to do this.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-makeVote",
                            "Permission denied"
                    )
            );
            return 5;
        }
        if (vote.method == 0) {
            if (request.ops.length != 1) {
                Sys.devInfoF(
                        "Make Vote", "Client(%d) has sent more than 1 selections to a single vote.",
                        client
                );
                Global.cmdServer.sendMessage(client, new Message(
                                "R-makeVote",
                                "Invalid selection amount"
                        )
                );
                return 7;
            }
        }
        int selectionNum = VoteQueryState.getSelectionNum(request.voteId);
        for (int op : request.ops) {
            if (op >= selectionNum) {
                Sys.devInfoF("Make Vote", "Invalid vote selection number submitted.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-makeVote",
                                "Invalid vote selection value"
                        )
                );
                return 7;
            }
        }
        if (!VoteUpdateState.makeVote(request, client)) {
            Sys.devInfoF("Make Vote", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-makeVote",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-makeVote",
                        "OK"
                )
        );
        return 0;
    }
}