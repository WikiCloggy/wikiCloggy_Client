package gg.soc.wikicloggy;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by userp on 2018-05-21.
 * 게시판 list view에 들어갈 item과 연결시켜주는 adapter
 */

public class BoardAdapter extends BaseAdapter{
    private static final String TAG = "BoardAdapter";

    Context context;
    ArrayList<Board_item> listItemArrayList;

    TextView nameTextView;
    TextView titleTextView;
    TextView dateTextView;
    ImageView previewImageView;

    public BoardAdapter(Context context, ArrayList<Board_item> listItemArrayList) {
        this.context = context;
        this.listItemArrayList = listItemArrayList;
    }

    /*
    * 리스트뷰가 몇개의 아이템을 가지고 있는지를 알려주는 함
    * arrayList의 size(갯수) 만큼
    * */
    @Override
    public int getCount() {
        return this.listItemArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.listItemArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.board_item, null);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            dateTextView = (TextView) view.findViewById(R.id.dateTextView);
            previewImageView = (ImageView) view.findViewById(R.id.previewImageView);
        }
        nameTextView.setText(listItemArrayList.get(i).getName());
        titleTextView.setText(listItemArrayList.get(i).getTitle());
        dateTextView.setText(listItemArrayList.get(i).getDate());
        GetImageFromServer getImageFromServer = new GetImageFromServer(listItemArrayList.get(i).getProfile_image(), previewImageView);
        getImageFromServer.execute();
        Log.d(TAG, listItemArrayList.get(i).getTitle());
        /*
        if(nameTextView.getText().toString().equals("name")) {
            nameTextView.setText(listItemArrayList.get(i).getName());
        }
        if(titleTextView.getText().toString().equals("Title")) {
            titleTextView.setText(listItemArrayList.get(i).getTitle());
        }
        if(dateTextView.getText().toString().equals("date")) {
            dateTextView.setText(listItemArrayList.get(i).getDate());
            GetImageFromServer getImageFromServer = new GetImageFromServer(listItemArrayList.get(i).getProfile_image(), previewImageView);
            getImageFromServer.execute();
        }
        */
        return view;
    }
    class GetImageFromServer extends AsyncTask<Void, Void, Bitmap> {
        HttpInterface httpInterface;
        String url;
        ImageView imageView;

        public GetImageFromServer(String url, ImageView imageView) {
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
            //Log.d(TAG, "bitmap is "+bitmap);
            imageView.setImageBitmap(null);
            imageView.setImageBitmap(bitmap);
            Log.d(TAG, "hello");
        }
    }
}
