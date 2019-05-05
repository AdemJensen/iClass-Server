package top.chorg.kernel.database;

import top.chorg.kernel.server.base.api.chat.ChatMsg;
import top.chorg.system.Global;
import top.chorg.system.Sys;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChatUpdateState {
    public static int insertChat(ChatMsg msg, int userId) {
        try {
            PreparedStatement state = Global.database.prepareStatement(
                    "INSERT INTO chats " +
                            "(fromId, type, toId, time, content)" +
                            "VALUES " +
                            "(?, ?, ?, NOW(), ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            state.setInt(1, userId);
            state.setInt(2, msg.type);
            state.setInt(3, msg.toId);
            state.setString(4, msg.content);
            if (state.executeUpdate() == 0) return -1;
            var res = state.getGeneratedKeys();
            res.next();
            return res.getInt(1);
        }  catch (SQLException e) {
            Sys.errF("DB", "Error while inserting chat (%s).", e.getMessage());
            return -1;
        }
    }
}
