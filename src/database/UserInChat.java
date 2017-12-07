package database;

public class UserInChat {
    private long id;
    private long chatId;
    private long userId;

    public UserInChat(long id, long chatId, long userId) {
        this(chatId, userId);
        this.id = id;
    }

    public UserInChat(long chatId, long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
