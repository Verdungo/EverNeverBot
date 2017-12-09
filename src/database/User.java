package database;

import org.jetbrains.annotations.NotNull;

public class User{
    private long id;
    private String Name;

    public User(long id, String name) {
        this.id = id;
        Name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof User) && (this.id == ((User) obj).getId());
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
