package gg.soc.wikicloggy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
        setCustomActionbar();

        keywordSpinner = (Spinner) findViewById(R.id.createCommentKeywordSpinner);
        final ArrayAdapter keywordSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.targetKeyword, R.layout.spinner_item);
        keywordSpinner.setAdapter(keywordSpinnerAdapter);

        etcEditText = (EditText) findViewById(R.id.createCommentKeywordEditText);
        setUseableEditText(etcEditText, false);
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

        keywordSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(keywordSpinnerAdapter.getItem(i).toString().equals("기타")) {
                    setUseableEditText(etcEditText, true);
                } else {
                    setUseableEditText(etcEditText, false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = keywordSpinner.getSelectedItem().toString();
                PostCommentToServer postCommentToServer = new PostCommentToServer(postID, date);
                postCommentToServer.execute();
            }
        });
    }
    private void setUseableEditText(EditText etcEditText, boolean useable) {
        etcEditText.setClickable(useable);
        etcEditText.setEnabled(useable);
        etcEditText.setFocusable(useable);
        etcEditText.setFocusableInTouchMode(useable);
        if(useable == true) {
            etcEditText.setHintTextColor(getResources().getColor(R.color.gray));
        } else {
            etcEditText.setHintTextColor(getResources().getColor(R.color.white));
        }
    }
    class PostCommentToServer extends AsyncTask<Void, Void, Void> {
        HttpInterface httpInterface;
        String postID;
        JSONObject jsonObject = new JSONObject();
        String date;
        String result = null;
        public PostCommentToServer (String postID, String date) {
            httpInterface = new HttpInterface("postComment");
            this.postID = postID;
            this.date = date;
            httpInterface.addToUrl(postID);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, jsonObject.toString() + " length is "+jsonObject.length());
            if(jsonObject.length() == 0) {
                //필요한 내용이 부족한 경우
            } else {
                result = httpInterface.postJson(jsonObject);
                Log.d(TAG, "result is " + result);
                try {
                    JSONObject resultJSONObeject = new JSONObject(result);
                    result = resultJSONObeject.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DBController dbController = new DBController(getApplicationContext());
            String name = dbController.getUser(LoginActivity.currentUserID).getName();
            String body = bodyEditText.getText().toString();
            String keyword = null;
            if(keywordSpinner.getSelectedItem().toString().equals("기타")) {
                keyword = etcEditText.getText().toString();
            } else {
                keyword = keywordSpinner.getSelectedItem().toString();
            }
            if(keywordSpinner.getSelectedItem().toString().equals("기타") && etcEditText.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (body.equals("")) {
                Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
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
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result == null) {

            } else if(result.equals("ok")) {
                Toast.makeText(CreateCommentActivity.this, "댓글 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    public void setCustomActionbar() {
        ActionBar actionBar = getActionBar();

        //for custom actionbar, set customEnabled true
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.layout_actionbar, null);

        actionBar.setCustomView(actionbar);
    }
}
