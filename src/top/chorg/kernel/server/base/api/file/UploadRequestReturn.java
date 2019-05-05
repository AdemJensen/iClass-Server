package top.chorg.kernel.server.base.api.file;

public class UploadRequestReturn {
    public int id;
    public String path;

    public UploadRequestReturn(int id, String path) {
        this.id = id;
        this.path = path;
    }
}
