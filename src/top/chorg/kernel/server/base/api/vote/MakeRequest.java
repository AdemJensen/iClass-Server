package top.chorg.kernel.server.base.api.vote;

public class MakeRequest {
    public int voteId;
    public int[] ops;
    public String addition;

    public MakeRequest(int voteId, int[] ops, String addition) {
        this.voteId = voteId;
        this.ops = ops;
        this.addition = addition;
    }
}
