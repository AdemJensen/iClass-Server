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

public class GetNickName extends CmdResponder {
    public GetNickName(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int[] request;
        try {
            request = Objects.requireNonNull(nextArg(int[].class));
        } catch (JsonParseException | NullPointerException e) {
            Sys.devInfoF("Fetch NickName", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getNickName",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        String[] classMates = UserQueryState.getNickNameSet(request);
        if (classMates == null) {
            Sys.devInfoF("Fetch NickName", "Client(%d): Unable to perform (%d).",
                    client, Arrays.toString(request));
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getNickName",
                            "Unable to perform action"
                    )
            );
            return 3;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-getNickName",
                        Global.gson.toJson(classMates)
                )
        );
        return 0;
    }
}
