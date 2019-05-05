package top.chorg.kernel.database;

import top.chorg.support.DateTime;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogUpdateState {
    public static boolean addLog(int userId, int classId, String action) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "INSERT INTO `logs` (`userId`, `classId`, `action`, `time`) VALUES (?, ?, ?, NOW());"
            );
            state.setInt(1, userId);
            state.setInt(2, classId);
            state.setString(3, action);
            return state.executeUpdate() > 0;
        } catch (SQLException e) {
            Sys.err("DB", "Error while adding log.");
            return false;
        }
    }
}
