package top.chorg.kernel.cmd.privateResponders.chat;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.ChatQueryState;
import top.chorg.kernel.database.ChatUpdateState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.chat.ChatMsg;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class Send extends CmdResponder {

    public Send(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        ChatMsg request = nextArg(ChatMsg.class);
        if (request == null) {
            Sys.devInfoF("Send Chat", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-sendChat",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        if (request.type == 1) {
            if (UserQueryState.getLevelInClass(client, request.toId) < 0) {
                Global.cmdServer.sendMessage(client, new Message(
                        "R-sendChat",
                        "You are not part of class " + request.toId
                ));
                return 1;
            }
        }
        int insertedId = ChatUpdateState.insertChat(request, client);
        if (insertedId > 0) {
            request = ChatQueryState.getMsgById(insertedId);
            if (request == null) {
                Sys.devInfoF("Send Chat", "Operation unexpectedly failed (Client: %d).", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-sendChat",
                                "Unknown (Unexpectedly)"
                        )
                );
                return 6;
            }
            if (request.type == 1) {
                int[] classmates = UserQueryState.getClassmates(request.toId);
                if (classmates == null) {
                    Sys.devInfoF("Send Chat", "Operation unexpectedly failed (Client: %d).", client);
                    Global.cmdServer.sendMessage(client, new Message(
                                    "R-sendChat",
                                    "Unknown (Unexpectedly)"
                            )
                    );
                    return 6;
                }
                for (int classmate : classmates) {
                    if (Global.cmdServer.isOnline(classmate)) {
                        Global.cmdServer.sendMessage(classmate, new Message(
                                        "onChat",
                                        Global.gson.toJson(request)
                                )
                        );
                    }
                }
            } else {
                if (Global.cmdServer.isOnline(request.toId)) {
                    Global.cmdServer.sendMessage(request.toId, new Message(
                                    "onChat",
                                    Global.gson.toJson(request)
                            )
                    );
                }
            }

        } else {
            Sys.devInfoF("Send Chat", "Operation failed (Client: %d).", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-sendChat",
                            "Unknown (Message send failed)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-sendChat",
                        Global.gson.toJson(request)
                )
        );
        return 0;
    }
}
