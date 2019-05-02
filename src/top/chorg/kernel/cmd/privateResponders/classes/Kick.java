package top.chorg.kernel.cmd.privateResponders.classes;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.UserUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.auth.ClassInfo;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class Kick extends CmdResponder {
    public Kick(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int[] request;
        try {
            request = Objects.requireNonNull(nextArg(int[].class));
        } catch (NumberFormatException | NullPointerException e) {
            Sys.devInfoF("Kick member", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-kickMember",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        int clientLevel = UserQueryState.getLevelInClass(client, request[0]);
        if (clientLevel < 1) {
            Sys.devInfoF("Kick member", "Client(%d) is not authorized to do this.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-kickMember",
                            "Permission denied"
                    )
            );
            return 5;
        }
        ClassInfo classObj = UserQueryState.getClassInfo(request[0]);
        if (classObj == null) {
            Sys.devInfoF("Kick member", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-kickMember",
                            "Class not exist"
                    )
            );
            return 3;
        }
        int targetUser = UserQueryState.getLevelInClass(request[1], request[0]);
        if (targetUser >= clientLevel) {
            Sys.devInfoF("Kick member", "Client(%d) is not authorized to do this.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-kickMember",
                            "Permission denied"
                    )
            );
            return 7;
        } else if (targetUser < 0) {
            Sys.devInfoF("Kick member", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-kickMember",
                            "User not exist"
                    )
            );
            return 8;
        }
        if (UserUpdateState.exitClass(request[1], request[0])) {
            Global.cmdServer.sendMessage(client, new Message(
                            "R-kickMember",
                            "OK"
                    )
            );
            return 0;
        } else {
            Sys.devInfoF("Kick member", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-kickMember",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
    }
}
