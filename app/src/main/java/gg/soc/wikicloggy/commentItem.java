package gg.soc.wikicloggy;

/**
 * Created by userp on 2018-05-28.
 */

public class commentItem {
    private String name;
    private String body;
    private boolean adopted = false;
    private String keywords;

    public commentItem(String name, String body, boolean adopted, String keywords) {
        this.name = name;
        this.body = body;
        this.adopted = adopted;
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public boolean isAdopted() {
        return adopted;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAdopted(boolean adopted) {
        this.adopted = adopted;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
