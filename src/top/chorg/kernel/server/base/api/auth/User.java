package top.chorg.kernel.server.base.api.auth;

import top.chorg.kernel.database.UserQueryState;
import top.chorg.support.Date;
import top.chorg.support.DateTime;

public class User {

    private int id, classId, sex, grade;
    private String username, realName, nickname, email, phone;
    private Date birthday;
    private DateTime regTime;

    public User(int id, int classId, int sex, int grade, String username, String realName,
                String nickname, String email, String phone, Date birthday, DateTime regTime) {
        this.assign(id, classId, sex, grade, username, realName, nickname, email, phone, birthday, regTime);
    }

    public void assign(int id, int classId, int sex, int grade, String username, String realName,
                String nickname, String email, String phone, Date birthday, DateTime regTime) {
        this.id = id;
        this.classId = classId;
        this.sex = sex;
        this.grade = grade;
        this.username = username;
        this.realName = realName;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.regTime = regTime;
    }

    public void assign(User alt) {
        this.assign(
                alt.id,
                alt.classId,
                alt.sex,
                alt.grade,
                alt.username,
                alt.realName,
                alt.nickname,
                alt.email,
                alt.phone,
                alt.birthday,
                alt.regTime
        );
    }

    public boolean updateUserInfo() {
        // TODO: Power overflow?
        User res = UserQueryState.UpdateUserInfo(this.id);
        if (res == null) return false;
        this.assign(res);
        return true;
    }

    public int getId() {
        return id;
    }

    public int getClassId() {
        return classId;
    }

    public int getSex() {
        return sex;
    }

    public int getGrade() {
        return grade;
    }

    public String getUsername() {
        return this.username;
    }

    public String getRealName() {
        return realName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Date getBirthday() {
        return birthday;
    }

    public DateTime getRegTime() {
        return regTime;
    }
}
