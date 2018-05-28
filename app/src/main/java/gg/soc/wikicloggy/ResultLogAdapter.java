package gg.soc.wikicloggy;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by userp on 2018-05-28.
 */

public class ResultLogAdapter extends BaseAdapter {
    Context context;
    ArrayList<ResultItem> resultItemArrayList;

    ImageView imageView;
    TextView dateTextView;
    TextView keywordTextView;

    public ResultLogAdapter(Context context, ArrayList<ResultItem> resultItemArrayList) {
        this.context = context;
        this.resultItemArrayList = resultItemArrayList;
    }

    @Override
    public int getCount() {
        return this.resultItemArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.resultItemArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.result_item, null);
            dateTextView = (TextView) view.findViewById(R.id.resultLogDateTextView);
            imageView = (ImageView) view.findViewById(R.id.resultLogImageView);
            keywordTextView = (TextView) view.findViewById(R.id.resultLogKeywordTextView);
        }

        dateTextView.setText(resultItemArrayList.get(i).getDate().toString());
        imageView.setImageBitmap(resultItemArrayList.get(i).getImage());
        keywordTextView.setText(resultItemArrayList.get(i).getKeyword());

        return view;
    }
}
