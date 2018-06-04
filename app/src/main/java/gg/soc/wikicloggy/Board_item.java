package gg.soc.wikicloggy;

import java.util.Date;

/**
 * Created by userp on 2018-05-21.
 */

public class Board_item {
    private String profile_image;
    private String title;
    private String name;
    private String date;

    public Board_item(String profile_image, String title, String name, String date) {
        this.profile_image = profile_image;
        this.title = title;
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getTitle() {
        return title;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
