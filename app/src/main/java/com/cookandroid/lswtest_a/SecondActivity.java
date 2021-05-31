package com.cookandroid.lswtest_a;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SecondActivity extends Activity {
    EditText name;
    String time, minute;
    TimePicker tPicker;
    Button save;
    ImageButton back;
    SQLiteDatabase sqlDB;
    myDBHelper myHelper;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        setTitle("일정 추가");

        name = (EditText) findViewById(R.id.schName);
        save = (Button) findViewById(R.id.schSave);
        back = (ImageButton) findViewById(R.id.schBack);
        tPicker = (TimePicker)findViewById(R.id.timePicker);

        intent = getIntent();
        final String YearMonthDay = intent.getStringExtra("YMD");

        time = Integer.toString(tPicker.getCurrentHour());
        minute = Integer.toString(tPicker.getCurrentMinute());

        myHelper = new myDBHelper(this);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                time = Integer.toString(tPicker.getCurrentHour());
                minute = Integer.toString(tPicker.getCurrentMinute());
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO ICDD VALUES("+YearMonthDay+",'"+ name.getText().toString()+"', '"+time+":"+minute+":00');");
                sqlDB.close();
                Toast.makeText(getApplicationContext(), "저장됨", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK,intent);
                finish();

            }
        });

    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "ICDD", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE ICDD (YMD date, content char(15), st time);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS ICDD");
            onCreate(db);
        }
    }
}
