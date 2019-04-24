package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.vote.AddRequest;
import top.chorg.kernel.server.base.api.vote.AlterRequest;
import top.chorg.kernel.server.base.api.vote.FetchInfoResult;
import top.chorg.kernel.server.base.api.vote.MakeRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class VoteUpdateState {
    public static boolean addVote(AddRequest request, int publisher) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "INSERT INTO votes " +
                            "(title, content, selections, date, validity, method, classId, level, status, publisher) " +
                            "VALUES " +
                            "(?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            state.setString(1, request.title);
            state.setString(2, request.content);
            state.setString(3, request.selections);
            state.setString(4, request.validity.toString());
            state.setInt(5, request.method);
            state.setInt(6, request.classId);
            state.setInt(7, request.level);
            state.setInt(8, request.status);
            state.setInt(9, publisher);
            state.executeUpdate();
            var temp = state.getGeneratedKeys();
            int voteId;
            if (temp.next()) {
                voteId = temp.getInt(1); // TODO: Unknown issue: cannot use 'id' instead of 1.
            } else {
                return false;
            }
            return addRelations(voteId, request.classId, request.level);
            // TODO: Vote should be added to new users.
        }  catch (SQLException e) {
            Sys.errF("DB", "Error while adding vote (%s).", e.getMessage());
            return false;
        }
    }

    public static boolean alterVote(AlterRequest request, int client) {
        try {
            FetchInfoResult vote = VoteQueryState.fetchInfo(request.id, client);
            if (vote == null) return false;
            boolean needsRefresh = false;
            boolean needsRedo = false;
            if (!vote.selections.equals(request.selections) || vote.method != request.method) needsRefresh = true;
            if (vote.level != request.level || vote.classId != request.classId) needsRedo = true;
            PreparedStatement state = Global.database.prepareStatement(
                    "UPDATE votes SET " +
                            "title=?, content=?, selections=?, validity=?, classId=?, method=?, status=?, level=? " +
                            "WHERE id=?"
            );
            state.setString(1, request.title);
            state.setString(2, request.content);
            state.setString(3, request.selections);
            state.setString(4, request.validity.toString());
            state.setInt(5, request.classId);
            state.setInt(6, request.method);
            state.setInt(7, request.status);
            state.setInt(8, request.level);
            state.setInt(9, request.id);
            if (state.executeUpdate() == 0) return false;
            if (needsRedo) {
                state = Global.database.prepareStatement(
                        "DELETE FROM vote_relations WHERE voteId=?"
                );  // Clear all the vote info to match.
                state.setInt(1, request.id);
                state.executeUpdate();
                return addRelations(request.id, request.classId, request.level);
            } else if (needsRefresh) {
                state = Global.database.prepareStatement(
                        "UPDATE vote_relations SET isVoted=0, ops='[]' WHERE voteId=?"
                );  // Clear all the vote info to match.
                state.setInt(1, request.id);
                state.executeUpdate();
            }
            return true;
        }  catch (SQLException e) {
            Sys.err("DB", "Error while altering vote.");
            return false;
        }
    }

    private static boolean addRelations(int voteId, int classId, int level) throws SQLException {
        PreparedStatement state;
        int[] classmates = UserQueryState.getClassmates(classId);
        if (classmates == null) return false;
        state = Global.database.prepareStatement(
                "INSERT INTO vote_relations " +
                        "(voteId, userId, isVoted, ops) " +
                        "VALUES " +
                        "(?, ?, 0, '[]')"
        );
        state.setInt(1, voteId);
        for (int classmate : classmates) {
            if (UserQueryState.getLevelInClass(classmate, classId) < level) continue;
            state.setInt(2, classmate);
            state.executeUpdate();
        }
        return true;
    }

    public static boolean deleteVote(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "DELETE FROM votes WHERE id=?"
            );
            state.setInt(1, id);
            if (state.executeUpdate() == 0) return false;
            state = Global.database.prepareStatement(
                    "DELETE FROM vote_relations WHERE voteId=?"
            );
            state.setInt(1, id);
            state.executeUpdate();
            return true;
        } catch (SQLException e) {
            Sys.err("DB", "Error while deleting vote.");
            return false;
        }
    }

    public static boolean makeVote(MakeRequest request, int userId) {
        try {
            // TODO: Lack time validation
            PreparedStatement state = Global.database.prepareStatement(
                    "UPDATE vote_relations SET isVoted=1, ops=? WHERE userId=? AND voteId=?"
            );
            state.setString(1, Global.gson.toJson(request.ops));
            state.setInt(2, userId);
            state.setInt(3, request.voteId);
            return state.executeUpdate() != 0;
        } catch (SQLException e) {
            Sys.err("DB", "Error while making vote action.");
            return false;
        }
    }
}
