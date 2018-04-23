package gg.soc.wikicloggy;

/**
 * Created by userp on 2018-04-15.
 */

public class User {
    private String name;
    private int id;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public int getId() {
        return id;
    }
}
