package top.chorg.kernel.server.base.api.auth;

public class AuthResult {
    String result;
    Object obj;

    public AuthResult(String result, Object obj) {
        this.result = result;
        this.obj = obj;
    }
}
