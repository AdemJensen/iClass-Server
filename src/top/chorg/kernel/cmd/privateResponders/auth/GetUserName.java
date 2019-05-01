package top.chorg.kernel.cmd.privateResponders.auth;

import com.google.gson.JsonParseException;
import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class GetUserName extends CmdResponder {
    public GetUserName(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int[] request;
        try {
            request = Objects.requireNonNull(nextArg(int[].class));
        } catch (JsonParseException | NullPointerException e) {
            Sys.devInfoF("Fetch UserName", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getUserName",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        String[] classMates = UserQueryState.getUserNameSet(request);
        if (classMates == null) {
            Sys.devInfoF("Fetch UserName", "Client(%d): Class not exist (%d).", client, request);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getUserName",
                            "Class not exist."
                    )
            );
            return 3;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-getUserName",
                        Global.gson.toJson(classMates)
                )
        );
        return 0;
    }
}
