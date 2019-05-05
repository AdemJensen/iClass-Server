package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.auth.AlterUserRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserUpdateState {

    public static boolean addUser(String name, String password) {
        if (password.length() != 32) return false;
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "INSERT INTO `users` (`username`, `password`, `regTime`) VALUES (?, ?, NOW());"
            );
            state.setString(1, name);
            state.setString(2, password);
            return state.executeUpdate() > 0;
        } catch (SQLException e) {
            Sys.err("DB", "Error while adding user.");
            return false;
        }
    }

    public static boolean joinClass(int userId, int classId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "INSERT INTO `class_relations` (userId, classId, date) VALUES (?, ?, NOW());"
            );
            state.setInt(1, userId);
            state.setInt(2, classId);
            return state.executeUpdate() > 0;
        } catch (SQLException e) {
            Sys.err("DB", "Error while joining class.");
            return false;
        }
    }

    public static boolean exitClass(int userId, int classId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "DELETE FROM `class_relations` WHERE userId=? AND classId=?"
            );
            state.setInt(1, userId);
            state.setInt(2, classId);
            return state.executeUpdate() > 0;
        } catch (SQLException e) {
            Sys.err("DB", "Error while exiting class.");
            return false;
        }
    }

    public static boolean alterUserInfo(AlterUserRequest request, int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "UPDATE `users` SET " +
                            "sex=?, grade=?, avatar=?, realName=?, nickName=?, email=?, phone=?, birthday=? " +
                            "WHERE id=?"
            );
            state.setInt(1, request.sex);
            state.setInt(2, request.grade);
            state.setInt(3, request.avatar);
            state.setString(4, request.realName);
            state.setString(5, request.nickname);
            state.setString(6, request.email);
            state.setString(7, request.phone);
            state.setString(8, request.birthday == null ? null : request.birthday.toString());
            state.setInt(9, userId);
            return state.executeUpdate() > 0;
        } catch (SQLException e) {
            Sys.errF("DB", "Error while altering user information (%d).", userId);
            return false;
        }
    }

    public static boolean changePassword(String pre, String latest, int userId) {
        try {
            if (pre.length() != 32 || latest.length() != 32) return false;
            PreparedStatement state = Global.database.prepareStatement(
                    "UPDATE users SET password=? WHERE id=? AND password=?"
            );
            state.setString(1, latest);
            state.setInt(2, userId);
            state.setString(3, pre);
            return state.executeUpdate() > 0;
        } catch (SQLException e) {
            Sys.errF("DB", "Error while altering user password (%d).", userId);
            return false;
        }
    }

}
