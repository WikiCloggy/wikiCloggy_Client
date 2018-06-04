package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class BoardActivity extends Activity {
    private static final String TAG = "BoardActivity";

    ListView listView;
    BoardAdapter boardAdapter;
    ArrayList<Board_item> listItemArrayList;
    Spinner searchSpinner;

    private Button createPostBtn;
    private Button checkPostLogBtn;
    private Button searchBtn;

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

        listItemArrayList.add(new Board_item(R.drawable.main_cloggy, "보라돌이", "현정", new Date(System.currentTimeMillis()).toString()));
        listItemArrayList.add(new Board_item(R.drawable.main_cloggy, "보라돌이", "현정", new Date(System.currentTimeMillis()).toString()));
        listItemArrayList.add(new Board_item(R.drawable.main_cloggy, "보라돌이", "현정", new Date(System.currentTimeMillis()).toString()));
        listItemArrayList.add(new Board_item(R.drawable.main_cloggy, "보라돌이", "현정", new Date(System.currentTimeMillis()).toString()));
        listItemArrayList.add(new Board_item(R.drawable.main_cloggy, "보라돌이", "현정", new Date(System.currentTimeMillis()).toString()));
        listItemArrayList.add(new Board_item(R.drawable.main_cloggy, "보라돌이", "현정", new Date(System.currentTimeMillis()).toString()));
        listItemArrayList.add(new Board_item(R.drawable.main_cloggy, "보라돌이", "현정", new Date(System.currentTimeMillis()).toString()));

        boardAdapter = new BoardAdapter(BoardActivity.this, listItemArrayList);
        listView.setAdapter(boardAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(BoardActivity.this, PostActivity.class);
                intent.putExtra("id", String.valueOf(i));
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
}
