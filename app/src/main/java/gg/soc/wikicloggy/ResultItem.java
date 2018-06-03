package gg.soc.wikicloggy;

import android.graphics.Bitmap;
import android.graphics.Interpolator;

import java.util.Date;

/**
 * Created by userp on 2018-05-28.
 */

public class ResultItem {
    private String date;
    private String image;
    private String keyword;

    public ResultItem(String date, String image, String keyword) {
        this.date = date;
        this.image = image;
        this.keyword = keyword;
    }
    public ResultItem(String date, String image) {
        this.date = date;
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setKeyword(String keyword) {
        keyword = keyword;
    }
}
