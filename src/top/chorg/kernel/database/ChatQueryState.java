package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.chat.ChatMsg;
import top.chorg.kernel.server.base.api.chat.FetchHistoryRequest;
import top.chorg.kernel.server.base.api.chat.History;
import top.chorg.support.DateTime;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChatQueryState {
    public static History[] fetchHistory(FetchHistoryRequest request, int userId) {
        try {
            PreparedStatement state;
            if (request.type == 1) {
                state = Global.database.prepareStatement(
                        "SELECT * FROM chats WHERE type=1 AND (toId=? OR (fromId=? AND toId=?)) " +
                                "ORDER BY time ASC "
                );
                state.setInt(1, request.toId);
                state.setInt(2, request.toId);
                state.setInt(3, userId);
            } else {
                state = Global.database.prepareStatement(
                        "SELECT * FROM chats WHERE type=2 AND ((fromId=? AND toId=?) OR (fromId=? AND toId=?)) " +
                                "ORDER BY time ASC"
                );
                state.setInt(1, request.toId);
                state.setInt(2, userId);
                state.setInt(3, userId);
                state.setInt(4, request.toId);
            }
            ArrayList<History> result = new ArrayList<>();
            var resObj = state.executeQuery();
            while (resObj.next()) {
                result.add(new History(
                        resObj.getInt("id"),
                        resObj.getInt("fromId"),
                        resObj.getInt("type"),
                        resObj.getInt("toId"),
                        new DateTime(resObj.getString("time")),
                        resObj.getString("content")
                ));
            }
            History[] res = new History[result.size()];
            result.toArray(res);
            return res;
        }  catch (SQLException e) {
            Sys.errF("DB", "Error while fetching chat history (%s).", e.getMessage());
            return null;
        }
    }

    public static ChatMsg getMsgById(int id) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "SELECT * FROM chats WHERE id=?"
            );
            state.setInt(1, id);
            var res = state.executeQuery();
            if (!res.next()) return null;
            return new ChatMsg(
                    res.getInt("id"),
                    res.getInt("fromId"),
                    res.getInt("type"),
                    res.getInt("toId"),
                    new DateTime(res.getString("time")),
                    res.getString("content")
            );
        }  catch (SQLException e) {
            Sys.err("DB", "Error while fetching chat by id.");
            return null;
        }
    }
}
