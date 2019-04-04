package top.chorg.Kernel.Server.CmdServer;

import top.chorg.Kernel.Database.UserQueryState;
import top.chorg.Kernel.Server.Base.Auth.User;
import top.chorg.Kernel.Communication.Message;
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
                        String str = reader.readLine();
                        if (str == null) {
                            throw new IOException();
                        }
                        Message raw = (Message) SerializeUtils.deserialize(str);
                        if (!raw.msgType.equals("login")) {
                            throw new IllegalArgumentException("1");
                        }
                        SerializableMap authMsg = (SerializableMap) raw.content;
                        if (!authMsg.containsKey("method") ||
                                !authMsg.containsKey("u") || !authMsg.containsKey("p")) {
                            throw new IllegalArgumentException("2");
                        }
                        User res = UserQueryState.validateUser(
                                (String) authMsg.get("u"),
                                (String) authMsg.get("p")
                        );
                        if (res == null) {
                            Sys.devInfo("Cmd Server", "A client has failed authentication.");
                            return 0;
                        } else {
                            writer.write(SerializeUtils.serialize(new Message(
                                    "login",
                                    new SerializableMap(
                                            "result", "Granted",
                                            "obj", res
                                    )
                            )));
                            writer.flush();
                            Sys.devInfo("Cmd Server", "A client has completed authentication.");
                            return res.getId();
                        }

                    } catch (IOException e) {
                        Sys.devInfo("Cmd Server", "A broken connection occurred while authenticating.");
                        return -2;
                    } catch (ClassNotFoundException | ClassCastException | IllegalArgumentException e) {
                        Sys.devInfoF("Cmd Server", "A client has sent invalid auth info (%s).", e);
                        return -1;
                    }
                }
        );
    }
}
