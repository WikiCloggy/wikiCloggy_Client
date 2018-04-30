package gg.soc.wikicloggy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by userp on 2018-04-15.
 * 내부적으로 사용되는 local DB를 관리하기 위해서 사용됨
 */

public class DBController extends SQLiteOpenHelper {
    private static final String TAG = "DBController";

    private Context context;
    private static final String DATABASE_NAME = "WIKICLOGGY";

    private static final String TABLE_USER_LIST = "UserList";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_IMAGE = "image";

    private static final int DATABASE_VERSION = 1;

    public DBController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    /*
    * Database가 존재하지 않을때, 딱 한번 실행됨
    * DB를 만드는 역할
    */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("DATABASE", "onCreate()");
        String CREATE_TABLE_USERLIST =
                "CREATE TABLE "+TABLE_USER_LIST+" ("+
                        KEY_USER_ID + " LONG NOT NULL, "+
                        KEY_USER_NAME+" TEXT NOT NULL, "+
                        KEY_USER_IMAGE+" BLOB"+
                        ");";
        sqLiteDatabase.execSQL(CREATE_TABLE_USERLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
    public void addUser(User user) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getId());
        values.put(KEY_USER_NAME, user.getName());

        database.insert(TABLE_USER_LIST, null, values);
        database.close();
    }

    public User getUser(long id) {
        User user = null;
        SQLiteDatabase database = getReadableDatabase();
        String SELECT_USER = "SELECT * FROM "+TABLE_USER_LIST+" WHERE "+KEY_USER_ID+"="+id;
        Log.d(TAG, SELECT_USER);
        Cursor cursor = database.rawQuery(SELECT_USER, null);
        while(cursor.moveToNext()) {
            long _id = cursor.getLong(0);
            String _name = cursor.getString(1);
            user = new User(_id, _name);
        }
        database.close();
        return user;
    }
    public void updateUser(User user) {
        SQLiteDatabase database = getWritableDatabase();
        String MODIFY_USER = "UPDATE " + TABLE_USER_LIST + " SET " + KEY_USER_NAME + " = '" + user.getName() + "' WHERE " + KEY_USER_ID + " = " + user.getId();
        //Log.d(TAG, MODIFY_USER);
        database.execSQL(MODIFY_USER);
    }
}
