package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class BoardActivity extends Activity implements AbsListView.OnScrollListener{
    private static final String TAG = "BoardActivity";

    ListView listView;
    BoardAdapter boardAdapter;
    ArrayList<Board_item> listItemArrayList;
    Spinner searchSpinner;

    private Button createPostBtn;
    private Button checkPostLogBtn;
    private Button searchBtn;

    private boolean lastItemVisibleFlag = false; //리스트 스크롤이 마지막 셀로 이동했는지 확인할 변수
    private int page = 0;                           //페이징 변수, 초기값은 0
    private final int OFFSET = 5;                  //한페이지마다 로드할 데이터 갯수
    private ProgressBar progressBar;
    private boolean mLockListView = false;        //데이터가 중복되지 않게 방지하는 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        createPostBtn = (Button)findViewById(R.id.createPostBtn);
        checkPostLogBtn = (Button)findViewById(R.id.checkPostLogBtn);
        searchBtn = (Button) findViewById(R.id.boardSearchBtn);

        listView = (ListView) findViewById(R.id.boardListView);
        searchSpinner = (Spinner) findViewById(R.id.searchSpinner);
        ArrayAdapter searchSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.searchTarget, android.R.layout.simple_spinner_item);
        searchSpinner.setAdapter(searchSpinnerAdapter);

        listItemArrayList = new ArrayList<Board_item>();

        listView.setOnScrollListener(this);

        GetUserLogTask getUserLogTask = new GetUserLogTask(page);
        getUserLogTask.execute();


        boardAdapter = new BoardAdapter(BoardActivity.this, listItemArrayList);
        listView.setAdapter(boardAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(BoardActivity.this, PostActivity.class);
                //Log.d(TAG, "post id is "+listItemArrayList.get(i).getPostId());
                //Log.d(TAG, "image is "+listItemArrayList.get(i).getProfile_image());
                intent.putExtra("id", listItemArrayList.get(i).getPostId());
                startActivity(intent);
            }
        });

        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BoardActivity.this, CreatePostActivity.class));
            }
        });

        checkPostLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BoardActivity.this, PostLogActivity.class));
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(BoardActivity.this, BoardActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            //progressBar.setVisibility(View.VISIBLE);
            GetUserLogTask getUserLogTask = new GetUserLogTask(page);
            getUserLogTask.execute();
        }

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        lastItemVisibleFlag = (i2 > 0 )&&(i+i1>=i2);
    }

    class GetUserLogTask extends AsyncTask<Void, Void, JSONArray> {
        HttpInterface httpInterface;
        int page;
        public GetUserLogTask(int page) {
            httpInterface = new HttpInterface("getBoardList");
            this.page = page;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            httpInterface.addToUrl(String.valueOf(page));
            return httpInterface.getJson();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            Log.d(TAG, jsonArray.toString());
            getItem(jsonArray);
        }
    }
    private void getItem(JSONArray jsonArray) {
        Log.d(TAG, "page is "+page);
        Log.d(TAG, "json array is "+jsonArray.toString());
        mLockListView = true;
        JSONObject jsonObject;
        String title = null;
        String date = null;
        String author = null;
        String image = null;
        String postId = null;
        for(int i=0; i< OFFSET; i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                postId = jsonObject.getString("_id");
                title = jsonObject.getString("title");
                image = jsonObject.getString("img_path");
                author = jsonObject.getString("author");
                date = jsonObject.getString("createdAt");
                if(title != null && image != null && author != null && date != null) {
                    Log.d(TAG, "title is "+title+" image is "+image+" author is "+author+" date is "+date+" postId is "+postId);
                    listItemArrayList.add(new Board_item(image, title, author, date, postId));
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
                //progressBar.setVisibility(View.GONE);
                mLockListView = false;
            }
        }, 1000);
    }
}
