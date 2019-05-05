package top.chorg.kernel.cmd.privateResponders.auth;

import com.google.gson.JsonParseException;
import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class IsUserOnline extends CmdResponder {
    public IsUserOnline(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int[] request;
        try {
            request = Objects.requireNonNull(nextArg(int[].class));
        } catch (JsonParseException | NullPointerException e) {
            Sys.devInfoF("Judge Online", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-judgeOnline",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        return sortOnlineUser(client, request);
    }

    public static int sortOnlineUser(int client, int[] request) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int classMate : request) {
            if (Global.cmdServer.isOnline(classMate)) arr.add(classMate);
        }
        Integer[] arI = new Integer[arr.size()];
        arr.toArray(arI);
        int[] finRes = Arrays.stream(arI).mapToInt(Integer::intValue).toArray();
        Global.cmdServer.sendMessage(client, new Message(
                        "R-fetchOnline",
                        Global.gson.toJson(finRes)
                )
        );
        return 0;
    }
}
