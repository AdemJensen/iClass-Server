package top.chorg.kernel.database;

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

}
