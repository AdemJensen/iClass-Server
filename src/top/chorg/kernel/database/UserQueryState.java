package top.chorg.kernel.database;

import top.chorg.kernel.server.base.dataClass.auth.User;
import top.chorg.support.Date;
import top.chorg.support.DateTime;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserQueryState {

    public static User UpdateUserInfo(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id, username, classId, email, phone, birthday, sex, " +
                            "realName, nickName, grade, regTime FROM users WHERE id=?"
            );
            state.setInt(1, id);
            return assignUserInfo(state);
        } catch (SQLException e) {
            Sys.err("DB", "Error while validating user.");
            return null;
        }

    }

    public static User validateUserNormal(String name, String password) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id, username, classId, email, phone, birthday, sex, " +
                            "realName, nickname, grade, regTime FROM users WHERE username=? AND password=?"
            );
            state.setString(1, name);
            state.setString(2, password);
            return assignUserInfo(state);
        } catch (SQLException e) {
            Sys.err("DB", "Error while validating user.");
            return null;
        }

    }

    public static User validateUserToken(String name, String token) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id, username, classId, email, phone, birthday, sex, " +
                            "realName, nickname, grade, regTime FROM users WHERE username=? AND loginToken=?"
            );
            state.setString(1, name);
            state.setString(2, token);
            return assignUserInfo(state);
        } catch (SQLException e) {
            Sys.err("DB", "Error while validating user.");
            return null;
        }

    }

    private static User assignUserInfo(PreparedStatement state) throws SQLException {
        var res = state.executeQuery();
        if (!res.next()) return null;
        return new User(
                res.getInt("id"),
                res.getInt("classId"),
                res.getInt("sex"),
                res.getInt("grade"),
                res.getString("username"),
                res.getString("realName"),
                res.getString("nickName"),
                res.getString("email"),
                res.getString("phone"),
                res.getString("birthday") == null ? null : new Date(res.getString("birthday")),
                new DateTime(res.getString("regTime"))
        );
    }

}
