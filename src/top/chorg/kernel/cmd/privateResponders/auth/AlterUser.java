package top.chorg.kernel.cmd.privateResponders.auth;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.UserUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.auth.AlterUserRequest;
import top.chorg.kernel.server.base.api.auth.User;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class AlterUser extends CmdResponder {

    public AlterUser(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        AlterUserRequest request = nextArg(AlterUserRequest.class);
        if (request == null) {
            Sys.devInfoF("Alter User", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterUser",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        User user = UserQueryState.getUser(client);
        if (user == null) {
            Sys.devInfoF("Alter User", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterUser",
                            "User not exist"
                    )
            );
            return 3;
        }
        if (UserUpdateState.alterUserInfo(request, client)) {
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterUser",
                            "OK"
                    )
            );
            return 0;
        } else {
            Sys.devInfoF("Alter User", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-alterUser",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
    }
}