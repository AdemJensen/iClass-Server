package top.chorg.kernel.cmd.privateResponders.classes;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static top.chorg.kernel.cmd.privateResponders.auth.IsUserOnline.sortOnlineUser;

public class FetchOnline extends CmdResponder {

    public FetchOnline(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int request;
        try {
            request = Objects.requireNonNull(nextArg(int.class));
        } catch (NumberFormatException | NullPointerException e) {
            Sys.devInfoF("Fetch Online", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-fetchOnline",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        int[] classMates = UserQueryState.getClassmates(request);
        if (classMates == null) {
            Sys.devInfoF("Fetch Online", "Client(%d): Class not exist (%d).", client, request);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-fetchOnline",
                            "Class not exist."
                    )
            );
            return 3;
        }
        return sortOnlineUser(client, classMates);
    }
}
