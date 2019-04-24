package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.announcements.FetchTemplateResult;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class TemplateQueryState {

    public static FetchTemplateResult[] fetchTemplate(int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM templates WHERE owner=?"
            );
            state.setInt(1, userId);
            var res = state.executeQuery();
            ArrayList<FetchTemplateResult> result = new ArrayList<>();
            while (res.next()) {
                result.add(new FetchTemplateResult(
                        res.getInt("id"),
                        res.getString("name"),
                        res.getString("title"),
                        res.getString("content")
                ));
            }
            FetchTemplateResult[] resA = new FetchTemplateResult[result.size()];
            result.toArray(resA);
            return resA;
        } catch (SQLException e) {
            Sys.err("DB", "Error while fetching template list.");
            return null;
        }
    }

    public static FetchTemplateResult fetchTemplateById(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM templates WHERE id=?"
            );
            state.setInt(1, id);
            var res = state.executeQuery();
            if (!res.next()) return null;
            return new FetchTemplateResult(
                    res.getInt("id"),
                    res.getString("name"),
                    res.getString("title"),
                    res.getString("content")
            );
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching template by id.");
            return null;
        }
    }

    public static boolean belongsTo(int id, int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM templates WHERE id=? AND owner=?"
            );
            state.setInt(1, id);
            state.setInt(2, userId);
            var res = state.executeQuery();
            return res.next();
        }  catch (SQLException e) {
            Sys.err("DB", "Error while judging template relationship.");
            return false;
        }
    }
}
