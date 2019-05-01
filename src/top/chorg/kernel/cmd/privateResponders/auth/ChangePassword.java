package top.chorg.kernel.cmd.privateResponders.auth;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.UserUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.auth.User;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class ChangePassword extends CmdResponder {

    public ChangePassword(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        String[] request = nextArg(String[].class);
        if (request == null) {
            Sys.devInfoF("Change Pass", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-changePassword",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        User user = UserQueryState.getUser(client);
        if (user == null) {
            Sys.devInfoF("Change Pass", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-changePassword",
                            "User not exist"
                    )
            );
            return 3;
        }
        if (UserUpdateState.changePassword(request[0], request[1], client)) {
            Global.cmdServer.sendMessage(client, new Message(
                            "R-changePassword",
                            "OK"
                    )
            );
            return 0;
        } else {
            Sys.devInfoF("Change Pass", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-changePassword",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
    }
}