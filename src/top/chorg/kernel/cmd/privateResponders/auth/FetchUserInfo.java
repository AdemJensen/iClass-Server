package top.chorg.kernel.cmd.privateResponders.auth;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.auth.User;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class FetchUserInfo extends CmdResponder {
    public FetchUserInfo(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        try {
            int request = Objects.requireNonNull(nextArg(int.class));
            User user = UserQueryState.getUser(request);
            if (user == null) {
                Sys.devInfoF("Fetch User Info", "Client(%d) has sent invalid request.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-fetchUserInfo",
                                "User not exist"
                        )
                );
                return 3;
            }
            if (!Global.cmdServer.sendMessage(client, new Message(
                    "R-fetchUserInfo",
                    Global.gson.toJson(user)
            ))) {
                Sys.errF("Fetch User Info", "Error while sending results to Client(%d).", client);
                return 1;
            }
        } catch (NullPointerException e) {
            Sys.devInfoF("Fetch User Info", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-fetchUserInfo",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        return 0;
    }
}
