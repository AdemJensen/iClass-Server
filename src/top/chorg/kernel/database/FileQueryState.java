package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.file.FileInfo;
import top.chorg.support.DateTime;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class FileQueryState {
    public static FileInfo getFileInfo(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM files WHERE id=? AND isUploaded=1 ORDER BY date DESC "
            );
            state.setInt(1, id);
            var res = state.executeQuery();
            if (!res.next()) return null;
            return new FileInfo(
                    id,
                    res.getString("name"),
                    res.getInt("uploader"),
                    new DateTime(res.getString("date")),
                    res.getInt("classId"),
                    res.getInt("level"),
                    res.getBigDecimal("size")
            );
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching chat by id.");
            return null;
        }
    }

    public static FileInfo[] getClassList(int classId, int userId) {
        int userLevel = UserQueryState.getLevelInClass(userId, classId);
        if (userLevel < 0) return null;
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM files WHERE classId=? AND (level<? OR level=?) AND isUploaded=1 " +
                            "ORDER BY date DESC "
            );
            state.setInt(1, classId);
            state.setInt(2, userLevel);
            state.setInt(3, userLevel);
            return extractFileList(state);
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching file list by class.");
            return null;
        }
    }

    public static FileInfo[] getSelfList(int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM files WHERE uploader=? AND isUploaded=1 ORDER BY date DESC "
            );
            state.setInt(1, userId);
            return extractFileList(state);
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching file list by class.");
            return null;
        }
    }

    private static FileInfo[] extractFileList(PreparedStatement state) throws SQLException {
        var res = state.executeQuery();
        ArrayList<FileInfo> list = new ArrayList<>();
        while (res.next()) {
            list.add(new FileInfo(
                    res.getInt("id"),
                    res.getString("name"),
                    res.getInt("uploader"),
                    new DateTime(res.getString("date")),
                    res.getInt("classId"),
                    res.getInt("level"),
                    res.getBigDecimal("size")
            ));
        }
        FileInfo[] result = new FileInfo[list.size()];
        list.toArray(result);
        return result;
    }
}
