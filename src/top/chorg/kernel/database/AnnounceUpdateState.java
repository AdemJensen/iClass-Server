package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.announcements.*;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AnnounceUpdateState {

    public static boolean addAnnounce(AddRequest request, int publisher) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "INSERT INTO announcements " +
                            "(title, content, date, validity, classId, level, publisher, status) " +
                            "VALUES " +
                            "(?, ?, NOW(), ?, ?, ?, ?, ?)"
            );
            state.setString(1, request.title);
            state.setString(2, request.content);
            state.setString(3, request.validity.toString());
            state.setInt(4, request.classId);
            state.setInt(5, request.level);
            state.setInt(6, publisher);
            state.setInt(7, request.status);
            return state.executeUpdate() != 0;
        }  catch (SQLException e) {
            Sys.err("DB", "Error while adding announcement (1).");
            return false;
        }
    }

    public static boolean alterAnnounce(AlterRequest request) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "UPDATE announcements SET " +
                            "title=?, content=?, validity=?, classId=?, level=?, status=? " +
                            "WHERE " +
                            "id=?"
            );
            state.setString(1, request.title);
            state.setString(2, request.content);
            state.setString(3, request.validity.toString());
            state.setInt(4, request.classId);
            state.setInt(5, request.level);
            state.setInt(6, request.status);
            state.setInt(7, request.id);
            return state.executeUpdate() != 0;
        }  catch (SQLException e) {
            Sys.err("DB", "Error while adding announcement (1).");
            return false;
        }
    }

    public static boolean deleteAnnounce(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "DELETE FROM announcements WHERE id=?"
            );
            state.setInt(1, id);
            return state.executeUpdate() != 0;
        }  catch (SQLException e) {
            Sys.err("DB", "Error while deleting announce (1).");
            return false;
        }
    }

}
