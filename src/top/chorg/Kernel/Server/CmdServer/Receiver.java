package top.chorg.Kernel.Server.CmdServer;

import top.chorg.Kernel.Server.Base.Client;
import top.chorg.Kernel.Server.Base.ClientReceiverBase;
import top.chorg.Kernel.Server.Base.Message;
import top.chorg.Support.SerializeUtils;
import top.chorg.System.Global;
import top.chorg.System.Sys;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver extends ClientReceiverBase {

    public Receiver(Client clientObj, BufferedReader br) {
        super(clientObj, br);
    }

    @Override
    public void run() {
        String str;
        while (true) {
            try {
                if (!clientObj.isConnected()) throw new IOException();
                str = br.readLine();
                if (str == null) {
                    throw new IOException();
                }
                Message msg = (Message) SerializeUtils.deserialize(str);
                if (!Global.cmdManPrivate.cmdExists(msg.msgType)) throw new IllegalArgumentException();
                Global.cmdManPrivate.execute(msg);
            } catch (IOException e) {
                Sys.warn("Cmd Server", "A client connection has lost.");
                return;
            } catch (ClassNotFoundException | ClassCastException | IllegalArgumentException e) {
                Sys.devInfo("Cmd Server", "Received invalid message.");
            }
        }
    }
}
