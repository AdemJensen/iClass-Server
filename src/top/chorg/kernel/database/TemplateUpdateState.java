package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.announcements.AddTemplateRequest;
import top.chorg.kernel.server.base.api.announcements.AlterTemplateRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TemplateUpdateState {

    public static boolean addTemplate(AddTemplateRequest request, int owner) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "INSERT INTO templates " +
                            "(name, title, content, owner) " +
                            "VALUES " +
                            "(?, ?, ?, ?)"
            );
            state.setString(1, request.name);
            state.setString(2, request.title);
            state.setString(3, request.content);
            state.setInt(4, owner);
            return state.executeUpdate() != 0;
        }  catch (SQLException e) {
            Sys.err("DB", "Error while adding template (1).");
            return false;
        }
    }

    public static boolean alterTemplate(AlterTemplateRequest request) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "UPDATE templates SET " +
                            "name=?, title=?, content=?" +
                            "WHERE " +
                            "id=?"
            );
            state.setString(1, request.name);
            state.setString(2, request.title);
            state.setString(3, request.content);
            state.setInt(4, request.id);
            return state.executeUpdate() != 0;
        }  catch (SQLException e) {
            Sys.err("DB", "Error while altering template (1).");
            return false;
        }
    }

    public static boolean deleteTemplate(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "DELETE FROM templates WHERE id=?"
            );
            state.setInt(1, id);
            return state.executeUpdate() != 0;
        }  catch (SQLException e) {
            Sys.err("DB", "Error while deleting template (1).");
            return false;
        }
    }
}
