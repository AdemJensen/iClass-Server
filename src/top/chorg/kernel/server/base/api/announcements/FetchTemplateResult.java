package top.chorg.kernel.server.base.api.announcements;

public class FetchTemplateResult {
    public int id;
    public String name, title, content;

    public FetchTemplateResult(int id, String name, String title, String content) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.content = content;
    }
}
