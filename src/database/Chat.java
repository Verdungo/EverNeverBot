package database;

public class Chat {
    private long id;
    private String name;

    public Chat(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return  (obj instanceof Chat) && this.id == ((Chat)obj).getId();
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
