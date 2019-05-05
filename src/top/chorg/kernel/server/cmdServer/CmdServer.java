package top.chorg.kernel.server.cmdServer;

import com.google.gson.JsonSyntaxException;
import top.chorg.kernel.database.UserQueryState;
import top.chorg.kernel.database.UserUpdateState;
import top.chorg.kernel.server.base.api.auth.AuthInfo;
import top.chorg.kernel.server.base.api.auth.AuthResult;
import top.chorg.kernel.server.base.api.auth.User;
import top.chorg.kernel.server.base.api.Message;
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
                        switch (authMsg.method) {
                            case "Normal": {
                                if (authMsg.username == null || authMsg.password == null) {
                                    throw new IllegalArgumentException("2");
                                }
                                User res = UserQueryState.validateUserNormal(
                                        authMsg.username,
                                        authMsg.password
                                );
                                if (res == null) {
                                    writer.println((new Message(
                                            "R-login",
                                            Global.gson.toJson(new AuthResult(
                                                    "Denied",
                                                    "Username or password incorrect"
                                            ))
                                    )).encode());
                                    writer.flush();
                                    Sys.devInfo("Cmd Server", "A client has failed authentication.");
                                    return 0;
                                } else {
                                    if (Global.cmdServer.isOnline(res.getId())) {
                                        Global.cmdServer.sendMessage(res.getId(), new Message(
                                                "forceOffline",
                                                "You have logged in somewhere else"
                                        ));
                                        Global.cmdServer.bringOffline(res.getId());
                                    }
                                    writer.println((new Message(
                                            "R-login",
                                            Global.gson.toJson(new AuthResult(
                                                    "Granted",
                                                    Global.gson.toJson(res)
                                            ))
                                    )).encode());
                                    writer.flush();
                                    Sys.devInfo("Cmd Server", "A client has completed authentication.");
                                    return res.getId();
                                }
                            }
                            case "Token": {
                                if (authMsg.username == null || authMsg.token == null) {
                                    throw new IllegalArgumentException();
                                }
                                User res = UserQueryState.validateUserToken(
                                        authMsg.username,
                                        authMsg.token
                                );
                                if (res == null) {
                                    writer.println((new Message(
                                            "login",
                                            Global.gson.toJson(new AuthResult(
                                                    "Denied",
                                                    "Token expired or invalid."
                                            ))
                                    )).encode());
                                    writer.flush();
                                    Sys.devInfo("Cmd Server", "A client has failed authentication.");
                                    return 0;
                                } else {
                                    writer.println((new Message(
                                            "R-login",
                                            Global.gson.toJson(new AuthResult(
                                                    "Granted",
                                                    Global.gson.toJson(res)
                                            ))
                                    )).encode());
                                    writer.flush();
                                    Sys.devInfo("Cmd Server", "A client has completed authentication.");
                                    return res.getId();
                                }
                            }
                            case "Register":
                                if (authMsg.username == null || authMsg.password == null) {
                                    throw new IllegalArgumentException("12");
                                }
                                if (UserQueryState.hasUser(authMsg.username)) {
                                    writer.println((new Message(
                                            "R-register",
                                            Global.gson.toJson(new AuthResult(
                                                    "Denied",
                                                    "Username already exists"
                                            ))
                                    )).encode());
                                    writer.flush();
                                    Sys.devInfo("Cmd Server", "A register action failed (Username exists).");
                                    return 0;
                                }
                                if (authMsg.password.length() != 32) {
                                    writer.println((new Message(
                                            "R-register",
                                            Global.gson.toJson(new AuthResult(
                                                    "Denied",
                                                    "Password invalid (hashcode incorrect)"
                                            ))
                                    )).encode());
                                    writer.flush();
                                    Sys.devInfo("Cmd Server", "A register action failed (hashcode incorrect).");
                                    return 0;
                                }
                                if (UserUpdateState.addUser(authMsg.username, authMsg.password)) {
                                    writer.println((new Message(
                                            "R-register",
                                            Global.gson.toJson(new AuthResult(
                                                    "Granted",
                                                    "Successfully registered"
                                            ))
                                    )).encode());
                                    writer.flush();
                                    Sys.devInfo("Cmd Server", "A successful register occurred.");
                                    return 0;
                                } else {
                                    writer.println((new Message(
                                            "R-register",
                                            Global.gson.toJson(new AuthResult(
                                                    "Denied",
                                                    "Unknown error"
                                            ))
                                    )).encode());
                                    writer.flush();
                                    Sys.devInfo("Cmd Server", "A register action failed (Unknown error).");
                                    return 0;
                                }
                            default:
                                throw new IllegalArgumentException("3");
                        }
                    } catch (IOException e) {
                        Sys.devInfo("Cmd Server", "A broken connection occurred while authenticating.");
                        return -2;
                    } catch (ClassCastException | IllegalArgumentException | JsonSyntaxException e) {
                        writer.println((new Message(
                                "login",
                                Global.gson.toJson(new AuthResult(
                                        "Denied",
                                        "Invalid auth info"
                                ))
                        )).encode());
                        writer.flush();
                        Sys.devInfoF("Cmd Server", "A client has sent invalid auth info (%s).", e);
                        return -1;
                    }
                }
        );
    }
}
