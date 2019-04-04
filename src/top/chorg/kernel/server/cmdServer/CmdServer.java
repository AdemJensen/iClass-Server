package top.chorg.kernel.server.cmdServer;

import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.server.base.dataClass.auth.AuthInfo;
import top.chorg.kernel.server.base.dataClass.auth.AuthResult;
import top.chorg.kernel.server.base.dataClass.auth.User;
import top.chorg.kernel.server.base.dataClass.Message;
import top.chorg.kernel.server.base.ServerBase;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CmdServer extends ServerBase {
    public CmdServer(int port) {
        super(
                port,
                top.chorg.kernel.server.cmdServer.Receiver.class,
                top.chorg.kernel.server.cmdServer.Sender.class,
                (BufferedReader reader, PrintWriter writer) -> {        //Validation method
                    try {
                        String str = reader.readLine();
                        if (str == null) { throw new IOException(); }
                        Message raw = Message.decode(str);
                        if (raw == null || !raw.getMsgType().equals("login")) {
                            throw new IllegalArgumentException("1");
                        }
                        AuthInfo authMsg = raw.getContent(AuthInfo.class);
                        if (authMsg.method.equals("Normal")) {
                            if (authMsg.username == null || authMsg.password == null) {
                                throw new IllegalArgumentException("2");
                            }
                            User res = UserQueryState.validateUserNormal(
                                    authMsg.username,
                                    authMsg.password
                            );
                            if (res == null) {
                                writer.write((new Message(
                                        "login",
                                        Global.gson.toJson(new AuthResult(
                                                "Denied",
                                                "Username or password incorrect."
                                        ))
                                )).encode());
                                Sys.devInfo("Cmd Server", "A client has failed authentication.");
                                return 0;
                            } else {
                                writer.write((new Message(
                                        "login",
                                        Global.gson.toJson(new AuthResult("Granted", res))
                                )).encode());
                                writer.flush();
                                Sys.devInfo("Cmd Server", "A client has completed authentication.");
                                return res.getId();
                            }
                        } else if (authMsg.method.equals("Token")) {
                            if (authMsg.username == null || authMsg.token == null) {
                                throw new IllegalArgumentException("3");
                            }
                            User res = UserQueryState.validateUserToken(
                                    authMsg.username,
                                    authMsg.token
                            );
                            if (res == null) {
                                writer.write((new Message(
                                        "login",
                                        Global.gson.toJson(new AuthResult(
                                                "Denied",
                                                "Token expired or invalid."
                                        ))
                                )).encode());
                                Sys.devInfo("Cmd Server", "A client has failed authentication.");
                                return 0;
                            } else {
                                writer.write((new Message(
                                        "login",
                                        Global.gson.toJson(new AuthResult("Granted", res))
                                )).encode());
                                writer.flush();
                                Sys.devInfo("Cmd Server", "A client has completed authentication.");
                                return res.getId();
                            }
                        } else {
                            throw new IllegalArgumentException("3");
                        }


                    } catch (IOException e) {
                        Sys.devInfo("Cmd Server", "A broken connection occurred while authenticating.");
                        return -2;
                    } catch (ClassCastException | IllegalArgumentException e) {
                        writer.write((new Message(
                                "login",
                                Global.gson.toJson(new AuthResult(
                                        "Denied",
                                        "Invalid auth info."
                                ))
                        )).encode());
                        Sys.devInfoF("Cmd Server", "A client has sent invalid auth info (%s).", e);
                        return -1;
                    }
                }
        );
    }
}
