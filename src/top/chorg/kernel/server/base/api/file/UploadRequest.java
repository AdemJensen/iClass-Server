package top.chorg.kernel.server.base.api.file;

public class UploadRequest {
    public String name;
    public int level, classId;

    public UploadRequest(String name, int classId, int level) {
        this.name = name;
        this.classId = classId;
        this.level = level;
    }
}
