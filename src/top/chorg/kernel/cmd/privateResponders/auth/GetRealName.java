package top.chorg.kernel.cmd.privateResponders.auth;

import com.google.gson.JsonParseException;
import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class GetRealName extends CmdResponder {
    public GetRealName(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int[] request;
        try {
            request = Objects.requireNonNull(nextArg(int[].class));
        } catch (JsonParseException | NullPointerException e) {
            Sys.devInfoF("Fetch RealName", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getRealName",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        String[] classMates = UserQueryState.getRealNameSet(request);
        if (classMates == null) {
            Sys.devInfoF("Fetch RealName", "Client(%d): Class not exist (%d).", client, request);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getRealName",
                            "Class not exist."
                    )
            );
            return 3;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-getRealName",
                        Global.gson.toJson(classMates)
                )
        );
        return 0;
    }
}
