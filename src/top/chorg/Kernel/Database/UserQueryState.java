package top.chorg.Kernel.Database;

import top.chorg.Kernel.Server.Base.Auth.User;
import top.chorg.System.Global;
import top.chorg.System.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserQueryState {

    public static User validateUser(String name, String password) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id, username, classId, email, phone, birthday, sex, " +
                            "realName, nickname, grade, regTime FROM users WHERE username=? AND password=?"
            );
            state.setString(1, name);
            state.setString(2, password);
            var res = state.executeQuery();
            if (!res.next()) return null;
            // TODO: ADD QUERY ASSIGNMENT
            return new User(

            );
        } catch (SQLException e) {
            Sys.err("DB", "Error while validating user.");
            return null;
        }

    }

}
