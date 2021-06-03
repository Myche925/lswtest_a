package com.cookandroid.lswtest_a;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

public class ThirdActivity extends Activity {
    EditText name;
    String time, minute;
    String newName;
    TimePicker tPicker;
    Button save;
    ImageButton back;
    SQLiteDatabase sqlDB;
    myDBHelper myHelper;
    int hours, minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third);
        setTitle("일정 수정");

        name = (EditText) findViewById(R.id.schName);
        save = (Button) findViewById(R.id.schSave);
        back = (ImageButton) findViewById(R.id.schBack);
        tPicker = (TimePicker)findViewById(R.id.timePicker);

        final Intent intent = getIntent();
        final String YearMonthDay = intent.getStringExtra("YMD");
        final String scName = intent.getStringExtra("name");
        final String scHour = intent.getStringExtra("time");
        final String scMinute = intent.getStringExtra("minute");

        name.setText(scName);
        hours = Integer.parseInt(scHour);
        minutes = Integer.parseInt(scMinute);
        tPicker.setHour(hours);
        tPicker.setMinute(minutes);

        myHelper = new myDBHelper(this);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newName = name.getText().toString();
                time = Integer.toString(tPicker.getCurrentHour());
                minute = Integer.toString(tPicker.getCurrentMinute());
                sqlDB = myHelper.getWritableDatabase();

                sqlDB.execSQL("UPDATE ICDD SET content='"+newName+"' WHERE YMD ="+YearMonthDay+" AND st='"+scHour+":"+scMinute+":00';");
                sqlDB.execSQL("UPDATE ICDD SET st='"+time+":"+minute+":00' WHERE YMD ="+YearMonthDay+" AND content='"+newName+"';");
                Toast.makeText(getApplicationContext(), "수정됨", Toast.LENGTH_SHORT).show();
                //name.setText("UPDATE ICDD SET st='"+time+":"+minute+":00' WHERE YMD ="+YearMonthDay+" AND WHERE content='"+newName+"';");
                sqlDB.close();
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