package top.chorg.kernel.cmd.privateResponders.classes;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.UserUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.auth.ClassInfo;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class JoinClass extends CmdResponder {
    public JoinClass(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int request;
        try {
            request = Objects.requireNonNull(nextArg(int.class));
        } catch (NumberFormatException | NullPointerException e) {
            Sys.devInfoF("Join Class", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-joinClass",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        ClassInfo classObj = UserQueryState.getClassInfo(request);
        if (classObj == null) {
            Sys.devInfoF("Join Class", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-joinClass",
                            "Class not exist"
                    )
            );
            return 3;
        }
        int clientLevel = UserQueryState.getLevelInClass(client, request);
        if (clientLevel >= 0) {
            Sys.devInfoF("Join Class", "Client(%d) is not authorized to do this.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-joinClass",
                            "You are already in class"
                    )
            );
            return 5;
        }
        if (UserUpdateState.joinClass(client, request)) {
            Global.cmdServer.sendMessage(client, new Message(
                            "R-joinClass",
                            "OK"
                    )
            );
            return 0;
        } else {
            Sys.devInfoF("Join Class", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-joinClass",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
    }
}
