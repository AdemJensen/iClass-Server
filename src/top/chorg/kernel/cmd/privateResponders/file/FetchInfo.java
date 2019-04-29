package top.chorg.kernel.cmd.privateResponders.file;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.FileQueryState;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.VoteQueryState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.file.FileInfo;
import top.chorg.kernel.server.base.api.vote.FetchInfoResult;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class FetchInfo extends CmdResponder {

    public FetchInfo(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        try {
            int request = Objects.requireNonNull(nextArg(int.class));
            FileInfo fileInfo = FileQueryState.getFileInfo(request);
            if (fileInfo == null) {
                Sys.devInfoF("Fetch File Info", "Client(%d) has sent invalid request.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-fetchFileInfo",
                                "File not exist"
                        )
                );
                return 3;
            }
            int clientLevel = UserQueryState.getLevelInClass(client, fileInfo.classId);
            if (clientLevel < fileInfo.level && client != fileInfo.uploader) {
                Sys.devInfoF("Fetch File Info", "Client(%d) is not authorized to do this.", client);
                Global.cmdServer.sendMessage(client, new Message(
                                "R-fetchFileInfo",
                                "Permission denied"
                        )
                );
                return 4;
            }
            if (!Global.cmdServer.sendMessage(client, new Message(
                    "R-fetchFileInfo",
                    Global.gson.toJson(fileInfo)
            ))) {
                Sys.errF("Fetch File Info", "Error while sending results to Client(%d).", client);
                return 1;
            }
        } catch (NullPointerException e) {
            Sys.devInfoF("Fetch File Info", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-fetchFileInfo",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        return 0;
    }

}

