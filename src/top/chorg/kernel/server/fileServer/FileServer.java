package top.chorg.kernel.server.fileServer;

import top.chorg.kernel.database.FileQueryState;
import top.chorg.kernel.database.FileUpdateState;
import top.chorg.kernel.server.base.api.file.FileInfo;
import top.chorg.kernel.server.base.api.file.GlobalStorage;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer extends Thread {

    private int port;
    public FileServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                if (socket == null) {
                    Sys.warn("File Server", "Read null connection.");
                    continue;
                }
                Uploader upload = new Uploader(socket);
                upload.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Uploader extends Thread {
        private Socket socket;
        public Uploader(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                switch (br.readLine()) {
                    case "upload":
                        upload(pw, br);
                        break;
                    case "download":
                        download(pw, br);
                        break;
                    default:
                        this.socket.close();
                        Sys.warnF("File server", "Client has sent invalid request.");
                }
            } catch (Exception e) {
                Sys.err("File server", "Error while processing socket.");
            }
        }

        private void upload(PrintWriter pw, BufferedReader br) throws Exception {
            String name = br.readLine();
            int id = Integer.parseInt(br.readLine());
            var fo = new FileOutputStream(String.format("fileLib/%d.file", id));
            pw.println("READY");
            pw.flush();
            var bytes = new byte[1024];
            for (int length; (length = socket.getInputStream().read(bytes)) != -1; ) {
                fo.write(bytes, 0, length);
            }
            fo.close();
            socket.close();
            FileUpdateState.completeUpload(name, id);
            Sys.infoF("File server", "File %d.file has been saved.", id);
        }

        private void download(PrintWriter pw, BufferedReader br) throws Exception {
            int userId = Integer.parseInt(br.readLine());
            int fileId = Integer.parseInt(br.readLine());
            String token = br.readLine();
            String key = Global.gson.toJson(new GlobalStorage(userId, token));
            Object store = Global.getVar(key);
            Global.dropVar(key);
            if (store == null) {
                pw.println("No data to be download.");
                pw.flush();
                socket.close();
                return;
            }
            int storeInt = (int) store;
            if (storeInt != fileId) {
                pw.println("Download data incorrect.");
                pw.flush();
                socket.close();
                return;
            }
            FileInfo info = FileQueryState.getFileInfo(fileId);
            if (info == null) {
                pw.println("Download file not exist.");
                pw.flush();
                socket.close();
                return;
            }
            pw.println("READY");
            pw.println(info.name);
            pw.flush();
            var fin = new FileInputStream(String.format("fileLib/%d.file", fileId));
            var bytes = new byte[1024];
            var os = socket.getOutputStream();
            for (int length; (length = fin.read(bytes)) != -1; ) {
                os.write(bytes, 0, length);
            }
            socket.close();
            fin.close();
            Sys.infoF("File server", "File %d.file has been downloaded.", fileId);
        }
    }
}
