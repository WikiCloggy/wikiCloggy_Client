package gg.soc.wikicloggy;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

        resultItemArrayList = new ArrayList<ResultItem>();

        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));
        resultItemArrayList.add(new ResultItem(new Date(System.currentTimeMillis()), "배고픔"));

        resultLogAdapter = new ResultLogAdapter(UserLogActivity.this, resultItemArrayList);
        listView.setAdapter(resultLogAdapter);
    }
}
