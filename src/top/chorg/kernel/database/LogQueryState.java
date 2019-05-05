package top.chorg.kernel.database;

import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogQueryState {
    public static String getLog(int classId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM logs WHERE classId=?"
            );
            state.setInt(1, classId);
            var res = state.executeQuery();
            StringBuilder str = new StringBuilder();
            while (res.next()) {
                str.append(String.format("[%s] User %s(%d) (%s)\n",
                        res.getString("time"),
                        UserQueryState.getUserNameSet(new int[]{ res.getInt("userId") })[0],
                        res.getInt("userId"),
                        res.getString("action")
                ));
            }
            if (str.length() == 0) str.append("No data");
            return str.toString();
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching log.");
            return null;
        }
    }
}
