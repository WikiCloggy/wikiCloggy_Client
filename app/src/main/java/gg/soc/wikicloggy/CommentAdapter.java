package gg.soc.wikicloggy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by userp on 2018-05-29.
 */

public class CommentAdapter extends BaseAdapter {
    private static final String TAG = "CommentAdapter";

    Context context;
    ArrayList<commentItem> commentItemArrayList;

    TextView nameTextView;
    TextView bodyTextView;
    TextView keywordTextView;
    ImageView boneImageView;

    public CommentAdapter (Context context, ArrayList<commentItem> commentItemArrayList) {
        this.context = context;
        this.commentItemArrayList = commentItemArrayList;
    }

    @Override
    public int getCount() {
        return this.commentItemArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.commentItemArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
            nameTextView = (TextView)view.findViewById(R.id.commentItemIdTextView);
            bodyTextView = (TextView) view.findViewById(R.id.commentItemBodyTextView);
            keywordTextView = (TextView)view.findViewById(R.id.commentItemKeywordTextView);
            boneImageView = (ImageView) view.findViewById(R.id.commentItemBoneImageView);
        }
        nameTextView.setText(commentItemArrayList.get(i).getName());
        bodyTextView.setText(commentItemArrayList.get(i).getBody());
        keywordTextView.setText(commentItemArrayList.get(i).getKeywords());

        //Log.d(TAG, "is here?");
        //Drawable drawable;
        Log.d(TAG, commentItemArrayList.get(i).getKeywords()+" "+commentItemArrayList.get(i).isAdopted());
        if(commentItemArrayList.get(i).isAdopted()) {
            boneImageView.setImageResource(R.drawable.bone1);
        } else if(!commentItemArrayList.get(i).isAdopted()) {
            boneImageView.setImageResource(R.drawable.bone0);
        }

        return view;
    }
}
