package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.vote.FetchInfoResult;
import top.chorg.kernel.server.base.api.vote.FetchListResult;
import top.chorg.support.DateTime;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class VoteQueryState {

    public static FetchListResult[] fetchList(int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT voteId, isVoted FROM vote_relations WHERE userId=?"
            );
            state.setInt(1, userId);
            ArrayList<FetchListResult> result = new ArrayList<>();
            var resObj = state.executeQuery();
            PreparedStatement subState = Global.database.prepareStatement(
                    "SELECT id, title, date, validity, method, classId, level, status, publisher " +
                            "FROM votes WHERE id=?"
            );
            while (resObj.next()) {
                subState.setInt(1, resObj.getInt("voteId"));
                var subRes = subState.executeQuery();
                if (subRes.next()) {
                    result.add(getFormattedListResult(subRes, resObj));
                }
            }
            FetchListResult[] res = new FetchListResult[result.size()];
            result.toArray(res);
            return res;
        }  catch (SQLException e) {
            Sys.errF("DB", "Error while fetching vote list (%s).", e.getMessage());
            return null;
        }
    }

    public static FetchInfoResult fetchInfo(int id, int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM votes WHERE id=?"
            );
            state.setInt(1, id);
            var res = state.executeQuery();
            if (!res.next()) return null;
            PreparedStatement subState = Global.database.prepareStatement(
                    "SELECT isVoted, ops FROM vote_relations WHERE voteId=? AND userId=?"
            );
            subState.setInt(1, id);
            subState.setInt(2, userId);
            var subRes = subState.executeQuery();
            if (!subRes.next()) return null;
            return new FetchInfoResult(
                    res.getInt("id"),
                    res.getString("title"),
                    res.getString("content"),
                    res.getString("selections"),
                    new DateTime(res.getString("date")),
                    new DateTime(res.getString("validity")),
                    res.getInt("method"),
                    res.getInt("classId"),
                    res.getInt("level"),
                    res.getInt("status"),
                    res.getInt("publisher"),
                    subRes.getInt("isVoted") == 1,
                    Global.gson.fromJson(subRes.getString("ops"), int[].class)
            );
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching vote by id.");
            return null;
        }
    }

    public static FetchListResult[] fetchPublishedList(int publisher) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT id, title, date, validity, method, classId, level, status, publisher " +
                            "FROM votes WHERE publisher=?"
            );
            state.setInt(1, publisher);
            var resObj = state.executeQuery();
            PreparedStatement subState = Global.database.prepareStatement(
                    "SELECT isVoted FROM vote_relations WHERE userId=? AND voteId=?"
            );
            ArrayList<FetchListResult> result = new ArrayList<>();
            while (resObj.next()) {
                subState.setInt(1, resObj.getInt(publisher));
                subState.setInt(2, resObj.getInt(resObj.getInt("voteId")));
                var subRes = subState.executeQuery();
                if (subRes.next()) {
                    result.add(getFormattedListResult(resObj, subRes));
                }
            }
            FetchListResult[] res = new FetchListResult[result.size()];
            result.toArray(res);
            return res;
        } catch (SQLException e) {
            Sys.err("DB", "Error while fetching published vote list.");
            return null;
        }

    }

    private static FetchListResult getFormattedListResult(ResultSet votesRes, ResultSet subRes) throws SQLException {
        return new FetchListResult(
                votesRes.getInt("id"),
                votesRes.getString("title"),
                new DateTime(votesRes.getString("date")),
                new DateTime(votesRes.getString("validity")),
                votesRes.getInt("method"),
                votesRes.getInt("classId"),
                votesRes.getInt("level"),
                votesRes.getInt("status"),    // Give the user's status rather than master
                votesRes.getInt("publisher"),
                subRes.getInt("isVoted") == 1
        );
    }

    public static int getSelectionNum(int voteId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT selections FROM votes WHERE id=?"
            );
            state.setInt(1, voteId);
            var res = state.executeQuery();
            if (!res.next()) return -1;
            return Global.gson.fromJson(res.getString("selections"), String[].class).length;
        } catch (SQLException e) {
            Sys.err("DB", "Error while counting selections.");
            return -1;
        }
    }

    public static int[][] queryOperations(int voteId) {
        try {
            // init
            int selectionNum = getSelectionNum(voteId);
            if (selectionNum == -1) return null;
            int[][] masterResult = new int[selectionNum][];
            ArrayList<ArrayList<Integer>> subResult = new ArrayList<>();
            for (int i = 0; i < selectionNum; i++) {
                subResult.add(new ArrayList<>());
            }
            // process master
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT userId, isVoted, ops FROM vote_relations WHERE voteId=?"
            );
            state.setInt(1, voteId);
            var res = state.executeQuery();
            while (res.next()) {
                if (res.getInt("isVoted") != 1) continue;
                int[] ops = Global.gson.fromJson(res.getString("ops"), int[].class);
                int user = res.getInt("userId");
                for (int op : ops) {
                    subResult.get(op).add(user);
                }
            }
            // process array list
            Integer[] subRes;
            for (int i = 0; i < selectionNum; i++) {
                subRes = new Integer[subResult.get(i).size()];
                subResult.get(i).toArray(subRes);
                masterResult[i] = Arrays.stream(subRes).mapToInt(Integer::valueOf).toArray();
            }
            return masterResult;
        } catch (SQLException e) {
            Sys.err("DB", "Error while fetching vote operation list.");
            return null;
        }
    }

}
