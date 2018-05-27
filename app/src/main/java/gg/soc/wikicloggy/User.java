package gg.soc.wikicloggy;

import android.graphics.Bitmap;

/**
 * Created by userp on 2018-04-15.
 * 사용자를 관리하기 위한 class
 *
 */

public class User {
    private String name = null;
    private long id;
    private Bitmap bitmapImg = null;
    private String avatarPath = null;

    public User(long id)
    {
        this.id = id;
    }

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(long id, Bitmap bitmap) {
        this.id = id;
        this.bitmapImg = bitmap;
    }

    public User(long id, String name, Bitmap bitmapImg) {
        this.id = id;
        this.name = name;
        this.bitmapImg = bitmapImg;
    }

    public User(long id, String name, String avatarPath) {
        this.id = id;
        this.name = name;
        this.avatarPath = avatarPath;
    }

    public User(long id, String name, String avatarPath, Bitmap bitmapImg) {
        this.id = id;
        this.name = name;
        this.avatarPath = avatarPath;
        this.bitmapImg = bitmapImg;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setBitmapImg(Bitmap bitmapImg) { this.bitmapImg = bitmapImg; }
    public Bitmap getBitmapImg () { return bitmapImg; }
    public long getId() {
        return id;
    }

    public void setAvatarPath (String avatarPath) {this.avatarPath = avatarPath; }
    public String getAvatarPath() {return avatarPath;}
}
