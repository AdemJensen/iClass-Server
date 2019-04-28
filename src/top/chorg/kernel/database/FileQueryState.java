package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.file.FileInfo;
import top.chorg.support.DateTime;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileQueryState {
    public static FileInfo getFileInfo(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT name, uploader, date, classId, level FROM files WHERE id=? AND isUploaded=1"
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
                    res.getInt("level")
            );
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching chat by id.");
            return null;
        }
    }
}
