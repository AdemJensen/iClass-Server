package top.chorg.kernel.cmd.privateResponders.file;

import top.chorg.kernel.cmd.CmdResponder;
import top.chorg.kernel.database.FileUpdateState;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.file.UploadRequest;
import top.chorg.kernel.server.base.api.file.UploadRequestReturn;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.util.Objects;

public class Upload extends CmdResponder {

    public Upload(String... args) {
        super(args);
    }

    @Override
    public int response() throws IndexOutOfBoundsException {
        int client = Objects.requireNonNull(nextArg(int.class));
        UploadRequest request = nextArg(UploadRequest.class);
        if (request == null) {
            Sys.devInfoF("Upload File", "Client(%d) has sent invalid request.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-uploadFile",
                            "Parameter incomplete"
                    )
            );
            return 2;
        }
        int slotId = FileUpdateState.insertUploadSlot(request, client);
        if (slotId < 0) {
            Sys.devInfoF("Upload File", "Operation changed nothing.", client);
            Global.cmdServer.sendMessage(client, new Message(
                            "R-uploadFile",
                            "Unknown (Nothing changed)"
                    )
            );
            return 6;
        }
        Global.cmdServer.sendMessage(client, new Message(
                        "R-uploadFile",
                        Global.gson.toJson(new UploadRequestReturn(
                                slotId,
                                request.name
                        ))
                )
        );
        return 0;
    }
}