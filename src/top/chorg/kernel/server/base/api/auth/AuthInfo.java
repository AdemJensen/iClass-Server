package top.chorg.kernel.server.base.api.auth;

public class AuthInfo {
    public String method;      // "Normal" or "Token"
    public String username;
    public String password;
    public String token;       // Only avail if method = "Token"
}
