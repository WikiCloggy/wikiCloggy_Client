package gg.soc.wikicloggy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;

public class BoardActivity extends AppCompatActivity {
    ListView listView;
    BoardAdapter boardAdapter;
    ArrayList<Board_item> listItemArrayList;
    Spinner searchSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

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
    }
}
