package database;

public class Score {
    private User user;
    private Integer score;

    public Score(User user, Integer score) {
        this.user = user;
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
