package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class CreateCommentActivity extends Activity {
    private static final String TAG = "CreateCommentActivity";

    private TextView userName;
    private TextView dateTextView;
    private Spinner keywordSpinner;
    private EditText etcEditText;
    private EditText bodyEditText;
    private Button saveBtn;

    private String postID;

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

        keywordSpinner = (Spinner) findViewById(R.id.createCommentKeywordSpinner);
        ArrayAdapter keywordSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.targetKeyword, R.layout.spinner_item);
        keywordSpinner.setAdapter(keywordSpinnerAdapter);

        etcEditText = (EditText) findViewById(R.id.createCommentKeywordEditText);
        bodyEditText = (EditText) findViewById(R.id.createCommentBodyEditText);
        userName = (TextView) findViewById(R.id.createCommentUserNameTextView);
        dateTextView = (TextView) findViewById(R.id.createCommentDateTextView);

        saveBtn = (Button) findViewById(R.id.createCommentSaveBtn);

        DBController dbController = new DBController(this);

        userName.setText(dbController.getUser(LoginActivity.currentUserID).getName());

        date = (new Date(System.currentTimeMillis())).toString();
        dateTextView.setText(date);
        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = keywordSpinner.getSelectedItem().toString();
                PostCommentToServer postCommentToServer = new PostCommentToServer(postID, date);
                postCommentToServer.execute();
            }
        });
    }
    class PostCommentToServer extends AsyncTask<Void, Void, Void> {
        HttpInterface httpInterface;
        String postID;
        JSONObject jsonObject = new JSONObject();
        String date;
        String result;
        public PostCommentToServer (String postID, String date) {
            httpInterface = new HttpInterface("postComment");
            this.postID = postID;
            this.date = date;
            httpInterface.addToUrl(postID);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, jsonObject.toString());
            result = httpInterface.postJson(jsonObject);
            Log.d(TAG, "result is "+result);
            try {
                JSONObject resultJSONObeject = new JSONObject(result);
                result = resultJSONObeject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DBController dbController = new DBController(getApplicationContext());
            String name = dbController.getUser(LoginActivity.currentUserID).getName();
            String body = bodyEditText.getText().toString();
            String keyword = keywordSpinner.getSelectedItem().toString();
            try {
                jsonObject.put("commenter", LoginActivity.currentUserID);
                jsonObject.put("name", name);
                jsonObject.put("body", body);
                jsonObject.put("adopted", false);
                jsonObject.put("keyword", keyword);
                jsonObject.put("createdAt", date);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result.equals("ok")) {
                Toast.makeText(CreateCommentActivity.this, "댓글 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
