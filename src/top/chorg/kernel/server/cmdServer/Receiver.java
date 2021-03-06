package top.chorg.kernel.server.cmdServer;

import top.chorg.kernel.server.base.Client;
import top.chorg.kernel.server.base.ClientReceiverBase;
import top.chorg.kernel.server.base.api.Message;
import top.chorg.system.Global;
import top.chorg.system.Sys;

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
                Message msg = Message.decode(str);
                if (msg == null || !Global.cmdManPrivate.cmdExists(msg.getMsgType()))
                    throw new IllegalArgumentException();
                Global.cmdManPrivate.execute(
                        msg.getMsgType(),
                        Integer.toString(clientObj.clientId),
                        msg.getContent()
                );
            } catch (IOException e) {
                if (Global.cmdServer.isOnline(clientObj.clientId)) {
                    Global.cmdServer.bringOffline(clientObj.clientId, true);
                }
                return;
            } catch (ClassCastException | IllegalArgumentException e) {
                Sys.devInfo("Cmd Server", "Received invalid message.");
            }
        }
    }
}
