package top.chorg.Kernel.Server.Base.Auth;

import java.io.Serializable;
import top.chorg.Support.Date;
import top.chorg.Support.DateTime;

public class User implements Serializable {
    private int id;
    private String username, realName, nickname;
    private int classId;
    private String email;
    private String phone;
    private Date birthday;
    int sex, grade;
    private DateTime regTime;

    public String getUsername() {
        return this.username;
    }
}
