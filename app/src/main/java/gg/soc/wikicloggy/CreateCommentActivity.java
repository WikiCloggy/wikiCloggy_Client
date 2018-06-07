package gg.soc.wikicloggy;

import android.app.Activity;
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

import java.util.Date;

public class CreateCommentActivity extends Activity {
    private static final String TAG = "CreateCommentActivity";

    private TextView userName;
    private TextView date;
    private Spinner keywordSpinner;
    private EditText etcEditText;
    private EditText bodyEditText;
    private Button saveBtn;

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
        date = (TextView) findViewById(R.id.createCommentDateTextView);

        saveBtn = (Button) findViewById(R.id.createCommentSaveBtn);

        DBController dbController = new DBController(this);

        userName.setText(dbController.getUser(LoginActivity.currentUserID).getName());
        date.setText((new Date(System.currentTimeMillis())).toString());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = keywordSpinner.getSelectedItem().toString();

            }
        });
    }
}
