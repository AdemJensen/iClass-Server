package top.chorg.kernel.cmd.privateResponders.chat;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.ChatQueryState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.chat.FetchHistoryRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class FetchHistory extends CmdResponder {

    public FetchHistory(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        FetchHistoryRequest request = nextArg(FetchHistoryRequest.class);
        if (request == null) {
            Sys.devInfoF("Fetch Chat", "Client(%d) has sent invalid request.", client);
            return 2;
        }
        if (request.type == 1) {
            if (UserQueryState.getLevelInClass(client, request.toId) < 0) {
                Global.cmdServer.sendMessage(client, new Message(
                        "R-fetchChatHistory",
                        "You are not part of class " + request.toId
                ));
                return 1;
            }
        }
        Global.cmdServer.sendMessage(client, new Message(
                "R-fetchChatHistory",
                Global.gson.toJson(ChatQueryState.fetchHistory(
                        request, client
                ))
        ));
        return 0;
    }

}
