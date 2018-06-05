package gg.soc.wikicloggy;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class PostLogActivity extends Activity {
    ListView listView;
    BoardAdapter boardAdapter;
    ArrayList<Board_item> itemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_log);

        listView = (ListView) findViewById(R.id.postLogListView);

        itemArrayList = new ArrayList<Board_item>();

        //itemArrayList.add(new Board_item(R.drawable.main_cloggy, "뚜비", "현정", String.valueOf(new Date(System.currentTimeMillis()))));
       // itemArrayList.add(new Board_item(R.drawable.main_cloggy, "뚜비", "현정", String.valueOf(new Date(System.currentTimeMillis()))));
        //itemArrayList.add(new Board_item(R.drawable.main_cloggy, "뚜비", "현정", String.valueOf(new Date(System.currentTimeMillis()))));
        //itemArrayList.add(new Board_item(R.drawable.main_cloggy, "뚜비", "현정", String.valueOf(new Date(System.currentTimeMillis()))));
        //itemArrayList.add(new Board_item(R.drawable.main_cloggy, "뚜비", "현정", String.valueOf(new Date(System.currentTimeMillis()))));
        //itemArrayList.add(new Board_item(R.drawable.main_cloggy, "뚜비", "현정", String.valueOf(new Date(System.currentTimeMillis()))));


        boardAdapter = new BoardAdapter(PostLogActivity.this, itemArrayList);
        listView.setAdapter(boardAdapter);
    }
}
