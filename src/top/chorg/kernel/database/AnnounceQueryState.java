package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.announcements.FetchListResult;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AnnounceQueryState {

    public static FetchListResult[] fetchListInClass(int userId, int classId) {
        try {
            int level = UserQueryState.getLevelInClass(userId, classId);
            if (level == -2) throw new SQLException();
            if (level == -1) return null;
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM announcements WHERE classId=? AND (level < ? OR level = ?)"
            );
            state.setInt(1, classId);
            state.setInt(2, level);
            state.setInt(3, level);
            return assignData(state);
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching announcement list.");
            return null;
        }
    }

    public static FetchListResult[] fetchAllList(int userId) {
        int[] classList = UserQueryState.getClasses(userId);
        if (classList == null) return null;
        ArrayList<FetchListResult> result = new ArrayList<>();
        for (int i : classList) {
            result.addAll(Arrays.asList(Objects.requireNonNull(fetchListInClass(userId, i))));
        }
        result.addAll(Arrays.asList(Objects.requireNonNull(fetchListInClass(userId, 0))));
        FetchListResult[] res = new FetchListResult[result.size()];
        result.toArray(res);
        return res;
    }

    public static FetchListResult fetchListById(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM announcements WHERE id=?"
            );
            state.setInt(1, id);
            var res = state.executeQuery();
            if (!res.next()) return null;
            return new FetchListResult(
                    res.getInt("id"),
                    res.getString("title"),
                    res.getString("content"),
                    res.getString("date"),
                    res.getString("validity"),
                    res.getInt("classId"),
                    res.getInt("level"),
                    res.getInt("publisher"),
                    res.getInt("status")
            );
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching announcement by id.");
            return null;
        }
    }

    public static FetchListResult[] fetchPublishedList(int publisher) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM announcements WHERE publisher=?"
            );
            state.setInt(1, publisher);
            return assignData(state);
        } catch (SQLException e) {
            Sys.err("DB", "Error while fetching announcement list.");
            return null;
        }

    }

    private static FetchListResult[] assignData(PreparedStatement state) throws SQLException {
        var res = state.executeQuery();
        ArrayList<FetchListResult> resultList = new ArrayList<>();
        while (res.next()) {
            resultList.add(new FetchListResult(
                    res.getInt("id"),
                    res.getString("title"),
                    res.getString("content"),
                    res.getString("date"),
                    res.getString("validity"),
                    res.getInt("classId"),
                    res.getInt("level"),
                    res.getInt("publisher"),
                    res.getInt("status")
            ));
        }
        FetchListResult[] result = new FetchListResult[resultList.size()];
        resultList.toArray(result);
        return result;
    }

}
