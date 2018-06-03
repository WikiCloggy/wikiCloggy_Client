package gg.soc.wikicloggy;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserLogActivity extends Activity implements AbsListView.OnScrollListener {
    private static final String TAG = "UserLogActivity";

    private ListView listView;
    private ResultLogAdapter resultLogAdapter;
    private ArrayList<ResultItem> resultItemArrayList;

    private boolean lastItemVisibleFlag = false; //리스트 스크롤이 마지막 셀로 이동했는지 확인할 변수
    private int page = 0;                           //페이징 변수, 초기값은 0
    private final int OFFSET = 5;                  //한페이지마다 로드할 데이터 갯수
    private ProgressBar progressBar;
    private boolean mLockListView = false;        //데이터가 중복되지 않게 방지하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);

        listView = (ListView) findViewById(R.id.resultLogListView);

        getUserLogTask getUserLog = new getUserLogTask(page);
        getUserLog.execute();
        resultItemArrayList = new ArrayList<ResultItem>();
        progressBar = (ProgressBar) findViewById(R.id.userLogProgressBar);
        progressBar.setVisibility(View.GONE);
        listView.setOnScrollListener(this);

        resultLogAdapter = new ResultLogAdapter(UserLogActivity.this, resultItemArrayList);
        listView.setAdapter(resultLogAdapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            progressBar.setVisibility(View.VISIBLE);
            getUserLogTask getUserLogTask = new getUserLogTask(page);
            getUserLogTask.execute();
        }

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visivleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0)&&(firstVisibleItem+visivleItemCount >= totalItemCount);
    }
    private void getItem(JSONArray jsonArray) {
        //리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 lock을 걸음
        mLockListView = true;
        JSONObject jsonObject;
        String date;
        String image;
        String keyword;
        JSONArray keywordJSONArray;
        for(int i =0; i<OFFSET; i++) {
            //resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
            try {
                jsonObject = jsonArray.getJSONObject(i);
                date = jsonObject.getString("createdAt");
                image = jsonObject.getString("img_path");
                keywordJSONArray = jsonObject.getJSONArray("analysis");
                keyword = keywordJSONArray.getJSONObject(0).getString("keyword") + " "+ keywordJSONArray.getJSONObject(0).getString("probability")
                        +" "+keywordJSONArray.getJSONObject(1).getString("keyword")+" "+keywordJSONArray.getJSONObject(1).getString("probability")
                        +" "+keywordJSONArray.getJSONObject(2).getString("keyword")+" "+keywordJSONArray.getJSONObject(2).getString("probability");
                resultItemArrayList.add(new ResultItem(date, image,keyword));
                Log.d(TAG, "image is "+image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                resultLogAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mLockListView = false;
            }
        }, 1000);
    }

    public class getUserLogTask extends AsyncTask<Void,Void,JSONArray>{
        HttpInterface httpGetUserLog;
        int page;
        public getUserLogTask(int page)
        {
            httpGetUserLog = new HttpInterface("getLog");
            this.page = page;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            httpGetUserLog.addToUrl(String.valueOf("/"+page));
            Log.d(TAG, httpGetUserLog.getUrl());
            return httpGetUserLog.getJson();
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            super.onPostExecute(result);
            Log.d(TAG, result.toString());
            getItem(result);
        }
    }
}
