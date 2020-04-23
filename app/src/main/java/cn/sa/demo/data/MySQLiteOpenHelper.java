package cn.sa.demo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;


/**
 * Created by yzk on 2020/4/14
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {


    private Context mContext;

    public MySQLiteOpenHelper(Context context) {
        super(context, "test.db", null, 1);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
