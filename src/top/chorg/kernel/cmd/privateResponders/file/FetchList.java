package top.chorg.kernel.cmd.privateResponders.file;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.FileQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.file.FileInfo;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class FetchList extends CmdResponder {

    public FetchList(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int request;
        try {
            request = Objects.requireNonNull(nextArg(int.class));
        } catch (NumberFormatException | NullPointerException e) {
            Sys.devInfoF("Fetch File List", "Client(%d) has sent invalid request.", client);
            return 2;
        }
        FileInfo[] info;
        if (request == -1) { // self
            info = FileQueryState.getSelfList(client);
        } else {
            info = FileQueryState.getClassList(request, client);
        }
        String result;
        if (info == null) result = "Permission denied";
        else result = Global.gson.toJson(info);
        Global.cmdServer.sendMessage(client, new Message(
                "R-fetchFileList",
                result
        ));
        return 0;
    }

}