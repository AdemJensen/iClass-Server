package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.announcements.FetchListResult;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class AnnounceQueryState {

    public static FetchListResult[] fetchList(int classId, int level) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM announcements WHERE classId=? AND level=?"
            );
            state.setInt(1, classId);
            state.setInt(2, level);
            return assignData(state);
        } catch (SQLException e) {
            Sys.err("DB", "Error while fetching announcement list.");
            return null;
        }

    }

    public static FetchListResult[] fetchList(int publisher) {
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
