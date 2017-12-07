package database;

public class Score {
    private long id;
    private long score;

    public Score(long id, long score) {
        this(score);
        this.id = id;
    }

    public Score(long score) {
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
