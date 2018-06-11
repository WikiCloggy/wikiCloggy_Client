package gg.soc.wikicloggy;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Layout;
import android.util.Log;
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
    private static final String TAG = "ResultLogAdapter";

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
        if(dateTextView.getText().toString().equals("Date")) {
            dateTextView.setText(resultItemArrayList.get(i).getDate());
            keywordTextView.setText(resultItemArrayList.get(i).getKeyword());
            HttpInterface httpInterface = new HttpInterface();
            GetImageFromServer getImageFromServer = new GetImageFromServer(resultItemArrayList.get(i).getImage(), imageView);
            getImageFromServer.execute();
        }
        return view;
    }
    class GetImageFromServer extends AsyncTask<Void, Void, Bitmap> {
        HttpInterface httpInterface;
        String url;
        ImageView imageView;
        public GetImageFromServer (String url, ImageView imageView) {
            this.url = url;
            httpInterface = new HttpInterface();
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            return httpInterface.getBitmapImage(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}
