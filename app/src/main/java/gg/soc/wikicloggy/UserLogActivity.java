package gg.soc.wikicloggy;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserLogActivity extends Activity {
    ListView listView;
    ResultLogAdapter resultLogAdapter;
    ArrayList<ResultItem> resultItemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);

        listView = (ListView) findViewById(R.id.resultLogListView);

        getUserLogTask getUserLog = new getUserLogTask();
        getUserLog.execute();
        resultItemArrayList = new ArrayList<ResultItem>();

        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));

        resultLogAdapter = new ResultLogAdapter(UserLogActivity.this, resultItemArrayList);
        listView.setAdapter(resultLogAdapter);
    }

    public class getUserLogTask extends AsyncTask<Void,Void,JSONObject>{
        HttpInterface httpGetUserLog;
        public getUserLogTask() {
            httpGetUserLog = new HttpInterface("getLog");
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Log.d("hyeon", httpGetUserLog.getUrl());
            JSONObject response = httpGetUserLog.getJson();
            Log.d("hyeon", response.toString());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            // result 서버에 저장되어 있는 유저 로그 정보야.

        }
    }
}
