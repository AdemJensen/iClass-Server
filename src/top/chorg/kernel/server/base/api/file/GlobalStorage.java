package top.chorg.kernel.server.base.api.file;

public class GlobalStorage {
    public int userId;
    public String token;

    public GlobalStorage(int userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
