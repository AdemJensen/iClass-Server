package top.chorg.kernel.cmd.privateResponders.auth;

import com.google.gson.JsonParseException;
import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class GetLevel extends CmdResponder {
    public GetLevel(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int request;
        try {
            request = Objects.requireNonNull(nextArg(int.class));
        } catch (NumberFormatException | NullPointerException e) {
            Sys.devInfoF("Get Level", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getLevel",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-getLevel",
                        String.valueOf(UserQueryState.getLevelInClass(client, request))
                )
        );
        return 0;
    }

}
