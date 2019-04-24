package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.auth.User;
import top.chorg.support.Date;
import top.chorg.support.DateTime;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class UserQueryState {

    public static User getUser(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id, username, email, phone, birthday, sex, " +
                            "realName, nickName, grade, regTime, userGroup FROM users WHERE id=?"
            );
            state.setInt(1, id);
            return assignUserInfo(state);
        } catch (SQLException e) {
            Sys.err("DB", "Error while getting user.");
            return null;
        }

    }

    public static boolean hasUser(String name) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id FROM users WHERE username=?"
            );
            state.setString(1, name);
            var res = state.executeQuery();
            return res.next();
        } catch (SQLException e) {
            Sys.err("DB", "Error while validating user existence.");
            return false;
        }

    }

    public static User validateUserNormal(String name, String password) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id, username, email, phone, birthday, sex, " +
                            "realName, nickname, grade, regTime, userGroup FROM users WHERE username=? AND password=?"
            );
            state.setString(1, name);
            state.setString(2, password);
            return assignUserInfo(state);
        } catch (SQLException e) {
            Sys.err("DB", "Error while validating user (Normal).");
            return null;
        }

    }

    public static User validateUserToken(String name, String token) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id, username, email, phone, birthday, sex, " +
                            "realName, nickname, grade, regTime, userGroup FROM users WHERE username=? AND loginToken=?"
            );
            state.setString(1, name);
            state.setString(2, token);
            return assignUserInfo(state);
        } catch (SQLException e) {
            Sys.err("DB", "Error while validating user (Token).");
            return null;
        }

    }

    public static char getUserGroup(int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT userGroup FROM users WHERE id=?"
            );
            state.setInt(1, userId);
            var res = state.executeQuery();
            if (!res.next()) return 'B';
            else return res.getString("userGroup").charAt(0);
        } catch (SQLException e) {
            Sys.err("DB", "Error while getting user group.");
            return 'B';
        }
    }

    public static int[] getClasses(int userId) {
        try {
            char group = getUserGroup(userId);
            if (group == 'B') {
                return null;
            }
            PreparedStatement state;
            ArrayList<Integer> results = new ArrayList<>();
            if (group == 'S') {
                state = Global.database.prepareStatement(
                        "SELECT id FROM classes"
                );
                var queryRes = state.executeQuery();
                while (queryRes.next()) {
                    results.add(queryRes.getInt("id"));
                }
            } else {
                state = Global.database.prepareStatement(
                        "SELECT classId FROM class_relations WHERE userId=?"
                );
                state.setInt(1, userId);
                var queryRes = state.executeQuery();
                while (queryRes.next()) {
                    results.add(queryRes.getInt("classId"));
                }
            }
            Integer[] res = new Integer[results.size()];
            results.toArray(res);
            return Arrays.stream(res).mapToInt(Integer::valueOf).toArray();
        } catch (SQLException e) {
            Sys.err("DB", "Error while getting user classes.");
            return null;
        }
    }

    public static int getLevelInClass(int userId, int classId) {
        char group = getUserGroup(userId);
        if (group == 'B') return -2;
        if (group == 'S') return 2;
        if (classId == 0) return 0;
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT level FROM class_relations WHERE userId=? AND classId=?"
            );
            state.setInt(1, userId);
            state.setInt(2, classId);
            var queryRes = state.executeQuery();
            if (!queryRes.next()) return -1;
            return queryRes.getInt("level");
        } catch (SQLException e) {
            Sys.err("DB", "Error while getting user level.");
            return -2;
        }

    }

    public static int[] getClassmates(int classId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT userId FROM class_relations WHERE classId=?"
            );
            state.setInt(1, classId);
            var res = state.executeQuery();
            ArrayList<Integer> result = new ArrayList<>();
            while (res.next()) {
                result.add(res.getInt("userId"));
            }
            Integer[] re = new Integer[result.size()];
            //result.toArray(re);
            return Arrays.stream(result.toArray(re)).mapToInt(Integer::valueOf).toArray();
        } catch (SQLException e) {
            Sys.err("DB", "Error while fetching classmates.");
            return null;
        }
    }

    private static User assignUserInfo(PreparedStatement state) throws SQLException {
        var res = state.executeQuery();
        if (!res.next()) return null;
        int userId = res.getInt("id");
        return new User(
                userId,
                getClasses(userId),
                res.getInt("sex"),
                res.getInt("grade"),
                res.getString("username"),
                res.getString("realName"),
                res.getString("nickName"),
                res.getString("email"),
                res.getString("phone"),
                res.getString("birthday") == null ? null : new Date(res.getString("birthday")),
                new DateTime(res.getString("regTime")),
                res.getString("userGroup").charAt(0)
        );
    }

}
