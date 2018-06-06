package gg.soc.wikicloggy;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class PostLogActivity extends Activity implements AbsListView.OnScrollListener{
    private static final String TAG = "PostLogActivity";

    private ListView listView;
    private BoardAdapter boardAdapter;
    private ArrayList<Board_item> itemArrayList;

    private boolean lastItemVisibleFlag = false; //리스트 스크롤이 마지막 셀로 이동했는지 확인할 변수
    private int page = 0;                           //페이징 변수, 초기값은 0
    private final int OFFSET = 5;                  //한페이지마다 로드할 데이터 갯수
    private ProgressBar progressBar;
    private boolean mLockListView = false;        //데이터가 중복되지 않게 방지하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_log);

        listView = (ListView) findViewById(R.id.postLogListView);

        itemArrayList = new ArrayList<Board_item>();

        progressBar = (ProgressBar) findViewById(R.id.postLogProgressBar);
        progressBar.setVisibility(View.GONE);
        listView.setOnScrollListener(this);


        boardAdapter = new BoardAdapter(PostLogActivity.this, itemArrayList);
        listView.setAdapter(boardAdapter);

        GetPostLogTask getPostLogTask = new GetPostLogTask(page);
        getPostLogTask.execute();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView ==false) {
            progressBar.setVisibility(View.VISIBLE);
            GetPostLogTask getPostLogTask = new GetPostLogTask(page);
            getPostLogTask.execute();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        lastItemVisibleFlag = (i2 > 0) && (i+i1>=i2);

    }
    private void getItem(JSONArray jsonArray) {
        mLockListView = true;
        JSONObject jsonObject;
        String date = null;
        String image = null;
        String title = null;
        String name = null;
        for (int i=0; i<OFFSET; i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                date = jsonObject.getString("createdAt");
                name = jsonObject.getString("author");
                title = jsonObject.getString("title");
                image = jsonObject.getString("img_path");
                Log.d(TAG, "image path is "+image);
                if(image != null && date != null && title != null && name != null) {
                    Log.d(TAG, "is in here?");
                    itemArrayList.add(new Board_item(image, title, name, date));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                boardAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mLockListView = false;
            }
        }, 1000);
    }
    class GetPostLogTask extends AsyncTask<Void, Void, JSONArray> {
        HttpInterface httpInterface;
        int page;
        public GetPostLogTask(int page) {
            httpInterface = new HttpInterface("getBoardLog");
            this.page = page;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            httpInterface.addToUrl(LoginActivity.currentUserID+"/"+page);
            Log.d(TAG, "url is "+httpInterface.getUrl());
            return httpInterface.getJson();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            Log.d(TAG, "result is "+jsonArray.toString());
            getItem(jsonArray);
        }
    }
}
