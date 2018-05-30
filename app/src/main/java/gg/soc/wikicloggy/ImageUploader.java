package gg.soc.wikicloggy;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by userp on 2018-05-31.
 */

public class ImageUploader extends AsyncTask<String, String, String> {
    HttpInterface httpInterface;
    String response;
    String DBId;
    String imgPath;

    JSONObject jsonObject = new JSONObject();

    public ImageUploader(String imgPath) {
        this.imgPath = imgPath;
        httpInterface = new HttpInterface("log");
    }
    @Override
    protected String doInBackground(String... strings) {
        try {
            jsonObject.put("user_code", LoginActivity.currentUserID);
            response = httpInterface.postJson(jsonObject);
            JSONObject logJSONObject = new JSONObject(response);
            DBId = logJSONObject.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return DBId;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Analysis analysis = new Analysis(imgPath, DBId);
        analysis.execute();
    }
}
class Analysis extends AsyncTask<Void, Void, Void> {
    HttpInterface httpInterface;
    String response;
    String realPath;

    public Analysis(String imgPath, String DB_id) {
        this.httpInterface = new HttpInterface("analysis");
        httpInterface.addToUrl(DB_id);
        this.realPath = imgPath;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
