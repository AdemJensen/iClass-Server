package top.chorg.Kernel.Server.CmdServer;

import top.chorg.Kernel.Database.UserQueryState;
import top.chorg.Kernel.Server.Base.ServerBase;
import top.chorg.Support.SerializableMap;
import top.chorg.Support.SerializeUtils;
import top.chorg.System.Sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CmdServer extends ServerBase {
    public CmdServer(int port) {
        super(
                port,
                top.chorg.Kernel.Server.CmdServer.Receiver.class,
                top.chorg.Kernel.Server.CmdServer.Sender.class,
                (BufferedReader reader, PrintWriter writer) -> {        //Validation method
                    try {
                        SerializableMap authMsg = (SerializableMap) SerializeUtils.deserialize(reader.readLine());
                        if (!authMsg.containsKey("method") ||
                                !authMsg.containsKey("u") || !authMsg.containsKey("p")) {
                            throw new IllegalArgumentException();
                        }
                        if (UserQueryState.validateUser(
                                (String) authMsg.get("u"),
                                (String) authMsg.get("p")
                        )) {

                        }

                    } catch (IOException e) {
                        Sys.devInfo("Cmd Server", "A broken connection occurred while authenticating.");
                        return 2;
                    } catch (ClassNotFoundException | ClassCastException | IllegalArgumentException e) {
                        Sys.devInfo("Cmd Server", "A client has sent invalid auth info.");
                        return 1;
                    }
                    return 0;
                }
        );
    }
}
