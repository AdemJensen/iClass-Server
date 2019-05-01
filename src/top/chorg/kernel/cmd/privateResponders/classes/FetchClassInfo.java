package top.chorg.kernel.cmd.privateResponders.classes;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.auth.ClassInfo;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class FetchClassInfo extends CmdResponder {
    public FetchClassInfo(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        try {
            int request = Objects.requireNonNull(nextArg(int.class));
            ClassInfo classInfo = UserQueryState.getClassInfo(request);
            if (classInfo == null) {
                Sys.devInfoF("Fetch Class Info", "Client(%d) has sent invalid request.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-fetchClassInfo",
                                "Class not exist"
                        )
                );
                return 3;
            }
            if (!Global.cmdServer.sendMessage(client, new Message(
                    "R-fetchClassInfo",
                    Global.gson.toJson(classInfo)
            ))) {
                Sys.errF("Fetch Class Info", "Error while sending results to Client(%d).", client);
                return 1;
            }
        } catch (NullPointerException e) {
            Sys.devInfoF("Fetch Class Info", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-fetchClassInfo",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        return 0;
    }
}
