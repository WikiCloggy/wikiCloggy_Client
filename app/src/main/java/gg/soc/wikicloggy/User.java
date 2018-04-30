package gg.soc.wikicloggy;

import android.graphics.Bitmap;

/**
 * Created by userp on 2018-04-15.
 * 사용자를 관리하기 위한 class
 *
 */

public class User {
    private String name;
    private long id;
    private String imagePath;
    private Bitmap bitmapImg;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }
    public User(long id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }
    public User(long id, String name, Bitmap bitmapImg) {
        this.id = id;
        this.name = name;
        this.bitmapImg = bitmapImg;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getImagePath() { return imagePath; }
    public long getId() {
        return id;
    }
}
