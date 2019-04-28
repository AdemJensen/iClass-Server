package top.chorg.kernel.cmd.privateResponders.file;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.FileQueryState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.file.DownloadRequestReturn;
import top.chorg.kernel.server.base.api.file.FileInfo;
import top.chorg.kernel.server.base.api.file.GlobalStorage;
import top.chorg.support.TokenMaker;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class Download extends CmdResponder {

    public Download(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        int request;
        try {
            request = Integer.parseInt(nextArg());
        } catch (NumberFormatException e) {
            Sys.devInfoF("Download File", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-downloadFile",
                            "Parameter incomplete"
                    )
            );
            return 1;
        }
        FileInfo info = FileQueryState.getFileInfo(request);
        if (info == null) {
            Sys.devInfoF("Download File", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-downloadFile",
                            "File not exist."
                    )
            );
            return 2;
        }
        if (UserQueryState.getLevelInClass(client, info.classId) < info.level &&
                info.uploader != client) {
            Sys.devInfoF("Download File", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-downloadFile",
                            "Permission denied"
                    )
            );
            return 3;
        } else {
            String token = TokenMaker.make(Global.random, 64);

            Global.setVar(
                    Global.gson.toJson(new GlobalStorage(client, token)),
                    info.id
            );
            Global.cmdServer.sendMessage(client, new Message(
                            "R-downloadFile",
                            Global.gson.toJson(new DownloadRequestReturn(
                                    request,
                                    token
                            ))
                    )
            );
        }

        return 0;
    }
}