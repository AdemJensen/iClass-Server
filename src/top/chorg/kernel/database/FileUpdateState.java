package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.file.UploadRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileUpdateState {

    public static int insertUploadSlot(UploadRequest msg, int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "INSERT INTO files " +
                            "(name, uploader, date, classId, level, isUploaded)" +
                            "VALUES " +
                            "(?, ?, NOW(), ?, ?, 0)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            state.setString(1, msg.name);
            state.setInt(2, userId);
            state.setInt(3, msg.classId);
            state.setInt(4, msg.level);
            if (state.executeUpdate() == 0) return -1;
            var res = state.getGeneratedKeys();
            res.next();
            return res.getInt(1);
        }  catch (SQLException e) {
            Sys.errF("DB", "Error while adding file slot (%s).", e.getMessage());
            return -1;
        }
    }

    public static boolean completeUpload(String name, int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "UPDATE files SET isUploaded=1, name=? WHERE id=?"
            );
            state.setString(1, name);
            state.setInt(2, id);
            return state.executeUpdate() != 0;
        }  catch (SQLException e) {
            Sys.errF("DB", "Error while completing upload (%s).", e.getMessage());
            return false;
        }
    }
}
