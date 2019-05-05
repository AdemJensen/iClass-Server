package top.chorg.kernel.cmd.privateResponders.classes;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.LogQueryState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class GetLogs extends CmdResponder {
    public GetLogs(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int request;
        try {
            request = Objects.requireNonNull(nextArg(int.class));
        } catch (NumberFormatException | NullPointerException e) {
            Sys.devInfoF("Fetch Log", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getLog",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        int[] classMates = UserQueryState.getClassmates(request);
        if (classMates == null) {
            Sys.devInfoF("Fetch Log", "Client(%d): Class not exist (%d).", client, request);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-getLog",
                            "Class not exist"
                    )
            );
            return 3;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-getLog",
                        LogQueryState.getLog(request)
                )
        );
        return 0;
    }
}
