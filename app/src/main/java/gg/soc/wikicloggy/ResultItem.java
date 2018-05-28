package gg.soc.wikicloggy;

import android.graphics.Bitmap;
import android.graphics.Interpolator;

import java.util.Date;

/**
 * Created by userp on 2018-05-28.
 */

public class ResultItem {
    private Date date;
    private Bitmap image;
    private String keyword;

    public ResultItem(Date date, Bitmap image, String keyword) {
        this.date = date;
        this.image = image;
        this.keyword = keyword;
    }
    public ResultItem(Date date, String keyword) {
        this.date = date;
        this.keyword = keyword;
    }

    public Date getDate() {
        return date;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setKeyword(String keyword) {
        keyword = keyword;
    }
}
