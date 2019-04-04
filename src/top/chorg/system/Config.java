package top.chorg.system;

import java.io.*;

public class Config {
    public int Cmd_Server_Port;
    public int File_Server_Port;
    public String DB_Url;
    public int DB_Port;
    public String DB_Username;
    public String DB_Password;

    public void assign(Config c) {
        Cmd_Server_Port = c.Cmd_Server_Port;
        File_Server_Port = c.File_Server_Port;
        DB_Url = c.DB_Url;
        DB_Port = c.DB_Port;
        DB_Username = c.DB_Username;
        DB_Password = c.DB_Password;
    }

    public boolean load() {
        return loadFromFile((String) Global.getVar("CONF_FILE"));
    }

    public boolean loadDefault() {
        return loadFromFile((String) Global.getVar("DEFAULT_CONF_FILE"));
    }

    public boolean save() {
        return saveToFile((String) Global.getVar("CONF_FILE"));
    }

    public boolean saveDefault() {
        return saveToFile((String) Global.getVar("DEFAULT_CONF_FILE"));
    }

    public boolean loadFromFile(String fileName) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(Global.getVar("CONF_ROUTE") + "/" + fileName),"GBK"));
            String s = in.readLine();
            assign(Global.gson.fromJson(s, Config.class));
            in.close();
            Sys.devInfoF("Config Loader", "Data read from '%s'.", fileName);
        } catch(IOException i) {
            Sys.errF("Config Loader", "Unable to open conf file for loading (%s)!", fileName);
            return false;
        }
        return true;
    }

    public boolean saveToFile(String fileName) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(Global.getVar("CONF_ROUTE") + "/" + fileName)));
            out.write(Global.gson.toJson(this));
            out.flush();
            out.close();
            Sys.devInfoF("Config Saver", "Serialized data is saved in '%s'.", fileName);
        } catch(IOException i) {
            Sys.errF("Config Saver", "Unable to open conf file for saving (%s)!", fileName);
            return false;
        }
        return true;
    }
}
