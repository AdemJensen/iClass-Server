package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.Message;
import top.chorg.kernel.server.base.api.vote.AddRequest;
import top.chorg.kernel.server.base.api.vote.AlterRequest;
import top.chorg.kernel.server.base.api.vote.FetchInfoResult;
import top.chorg.kernel.server.base.api.vote.MakeRequest;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

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
            addRelation(voteId, publisher);
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
                addRelation(request.id, client);
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
        // TODO: Add notification to the client.
        PreparedStatement state;
        int[] classmates = UserQueryState.getClassmates(classId);
        if (classmates == null) return false;
        state = Global.database.prepareStatement(
                "SELECT userId FROM vote_relations WHERE voteId=?"
        );
        state.setInt(1, voteId);
        var res = state.executeQuery();
        int remain = classmates.length;
        for (int i = 0; i < remain; i++) {
            if (UserQueryState.getLevelInClass(classmates[i], classId) < level) {
                classmates[i] = classmates[remain - 1];
                remain--;
            }
        }
        while (res.next()) {
            int now = res.getInt("userId");
            for (int i = 0; i < remain; i++) {
                if (classmates[i] == now) {
                    classmates[i] = classmates[remain - 1];
                    remain--;
                    break;
                }
            }
        }
        ArrayList<Integer> arr = new ArrayList<>();
        if (remain <= 5) {
            for (int i = 0; i < remain; i++) arr.add(classmates[i]);
        } else {
            boolean[] isUsed = new boolean[remain];
            int last = 5;
            Random rand = new Random();
            while (last > 0) {
                int got = rand.nextInt(remain);
                if (isUsed[got]) continue;
                last--;
                arr.add(classmates[got]);
                isUsed[got] = true;
            }
        }
        Integer[] selected = new Integer[arr.size()];
        arr.toArray(selected);
        for (int classmate : selected) {
            addRelation(voteId, classmate);
        }
        return true;
    }

    private static boolean addRelation(int voteId, int userId) throws SQLException {
        PreparedStatement state = Global.database.prepareStatement(
                "INSERT INTO vote_relations " +
                        "(voteId, userId, isVoted, ops) " +
                        "VALUES " +
                        "(?, ?, 0, '[]')"
        );
        state.setInt(1, voteId);
        state.setInt(2, userId);
        if (state.executeUpdate() == 0) return false;
        if (Global.cmdServer.isOnline(userId)) {
            Global.cmdServer.sendMessage(userId, new Message(
                    "onNewVote",
                    Global.gson.toJson(VoteQueryState.fetchInfo(voteId, userId))
            ));
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
                    "UPDATE vote_relations SET isVoted=1, ops=?, addition=? WHERE userId=? AND voteId=?"
            );
            state.setString(1, Global.gson.toJson(request.ops));
            state.setString(2, request.addition);
            state.setInt(3, userId);
            state.setInt(4, request.voteId);
            if (state.executeUpdate() == 0) return false;
            FetchInfoResult vote = VoteQueryState.fetchInfo(request.voteId, userId);
            if (vote == null) return false;
            addRelations(request.voteId, vote.classId, vote.level);
            return true;
        } catch (SQLException e) {
            Sys.err("DB", "Error while making vote action.");
            return false;
        }
    }
}
