package com.cookandroid.lswtest_a;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    CalendarView calView;
    TextView tvYear, tvMonth, tvDay, test;
    //DB에 넣기위해서 따로가져옴
    int selectYear, selectMonth, selectDay;
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    //추가버튼
    Button btnSec; //수정하기버튼
    //SECOND로 인텐트 할꺼
    String YMDD ,ampm;

    ListView listView1;
    String[] array; //시간,분으로 String split함수 사용하기위해서 선언
    DbAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("캘린더앱 테스트");
        // 캘린더뷰
        calView = (CalendarView) findViewById(R.id.calendarView1);
        // 텍스트뷰 중에서 연,월,일 숫자
        tvYear = (TextView) findViewById(R.id.tvYear);
        tvMonth = (TextView) findViewById(R.id.tvMonth);
        tvDay = (TextView) findViewById(R.id.tvDay);
        test = (TextView) findViewById(R.id.testtest);
        //세컨드로 넘어가는버튼(일정입력) 일정삭제하는대 사용하려고 만들어둔 버튼
        btnSec = (Button) findViewById(R.id.btnSec);
        //리스트뷰
        listView1=(ListView)findViewById(R.id.ListView1);
        //DB없을시 생성하기위해서 만든함수
        myHelper = new myDBHelper(this);
        //리스트뷰 관련 함수모음
        adapter=new DbAdapter();
        ampm = "오전";

        //캘린더뷰에서 날짜 눌렀을때 연,월,일 가져오는 함수
        calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectYear =  year;
                selectMonth = month + 1;
                selectDay = dayOfMonth;
                //아래 레이아웃 에있는 연,월,일 텍스트에 복사함
                tvYear.setText(Integer.toString(selectYear));
                tvMonth.setText(Integer.toString(selectMonth));
                tvDay.setText(Integer.toString(selectDay));
                //SELECT 문할때 10월 / 10일이전이면 9,8 이렇게 저장됨 이걸 09 08 이렇게 저장되게 바꿈
                if(selectMonth < 10  )
                    YMDD ="'"+ selectYear + "/0" + selectMonth ;
                else
                    YMDD ="'"+ selectYear + "/" + selectMonth ;

                if(selectDay < 10 )
                    YMDD = YMDD + "/0" + selectDay +"'";
                else
                    YMDD = YMDD + "/" + selectDay +"'";
                adapter.clear(); //리스트초기화
                //이러면 YMDD 는 ex)'2021/01/01' 이렇게 저장
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT content, st FROM ICDD WHERE YMD ="  +YMDD+  "ORDER BY YMD;" , null);
                String strNames ;
                String strNumbers ;
                while (cursor.moveToNext()) {
                    strNames= (cursor.getString(0));
                    strNumbers=(cursor.getString(1));
                    array = strNumbers.split(":");
                    if(Integer.parseInt(array[0])>12){
                        ampm = "오후";
                        array[0] =String.valueOf(Integer.parseInt(array[0]) - 12);
                    }
                    adapter.addItem(new DBItem(strNames+"",ampm+"" ,array[0]+"시"+array[1]+"분" ));
                    array[0]="";
                    array[1]=""; //초기화
                }
                listView1.setAdapter(adapter); //리스트뷰 활성화
                cursor.close();
                sqlDB.close();

            }
        });
        //second로 인텐트할꺼 여기다 넣음
        btnSec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        SecondActivity.class);
                intent.putExtra("YMD",YMDD);
                startActivity(intent);
            }
        });
    }

    //DB사용하기위해서 만들어둔거
    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "ICDD", null, 1);
        }
        //DB만들때
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE ICDD (YMD date, content char(15), st time);");
        }
        //DB지우고 다시할때
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS ICDD");
            onCreate(db);
        }
    }

    public class DBItem {

        String content;   //일정내용
        String ampm;      //오전오후
        String timep;     //시간

        //모든정보입력생성자
        public DBItem(String content, String ampm, String timep){
            this.content=content;
            this.ampm=ampm;
            this.timep=timep;
        }

        public String getcontent() {
            return content;
        }

        public void setcontent(String content) {
            this.content = content;
        }

        public String getampm() {
            return ampm;
        }

        public void setampm(String ampm) {
            this.ampm = ampm;
        }

        public String gettimep() {
            return timep;
        }

        public void settimep(String timep) {
            this.timep = timep;
        }
    }

    public class DBItemView extends LinearLayout {
        TextView textView;      //일정내용 담을 textView
        TextView textView2;     //오전오후 담을 textView
        TextView textView3;     //시간을 담을 textView
        Button btnsec2;        //수정버튼
        String[] Larray;       //ThirdPage인텐트용

        //객체의 생성자
        public DBItemView(Context context){
            super(context);
            init(context);
        }
        public DBItemView(Context context, AttributeSet attrs){
            super(context,attrs);
            init(context);
        }

        //객체 생성시 실행할 메소드 (onCreate() 과 같은 역할로 사용되었다)
        public void init(Context context){

            //우리가 만들었던 db_layout.xml 파일을 객체화 시키기위한 인플레이션 리스트레이아웃쪽 관련은 전부여기서 해야함
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.db_layout,this,true);
            textView=(TextView)findViewById(R.id.textView);
            textView2=(TextView)findViewById(R.id.textView2);
            textView3=(TextView)findViewById(R.id.textView3);
            btnsec2=(Button)findViewById(R.id.btnsec2);
            btnsec2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Larray = textView3.getText().toString().split("시");
                    Larray[1] = Larray[1].substring(0,Larray[1].length()-1);

                    if(textView2.getText().toString().equals("오후"))
                        Larray[0] = String.valueOf(Integer.parseInt(Larray[0]) + 12);
                   //Third페이지로 넘어가는 자료들
                    Intent intent = new Intent(getApplicationContext(),ThirdActivity.class);
                    intent.putExtra("YMD",YMDD); //연월일 ex) '2021/01/01'
                    intent.putExtra("name",textView.getText()); //일정내용
                    intent.putExtra("time",Larray[0]); //일정시간
                    intent.putExtra("minute",Larray[1]); //일정분
                  //Toast.makeText(MainActivity.this, Larray[0]+"te", Toast.LENGTH_SHORT).show();
                    //테스트용 토스트메세지
                    startActivity(intent);
                }
            });
        }
        //각각의 텍스트 뷰에 내용을 삽입하기 위한 setter 메소드들
        public void setContent(String content){
            textView.setText(content);
        }
        public void setampm(String ampm){
            textView2.setText(ampm);
        }
        public void settimep(String timep){
            textView3.setText(String.valueOf(timep));
        }
    }
    //아이템과 아이템을 합치는 어댑터 구현 (BaseAdapter)
    class DbAdapter extends BaseAdapter {
        //아이템들을 담을 ArrayList 생성
        ArrayList<DBItem> items = new ArrayList<DBItem>();
        @Override
        //리스트 항목의 개수를 안드로이드 OS에게 알려주는 중요한 메소드
        public int getCount() {
            return items.size();
        }
        //리스트에 아이템 담기
        public void addItem(DBItem item){
            items.add(item);
        }
        //리스트 비우기
        public void clear(){
            items.clear();
        }
        @Override
        public Object getItem(int i) {
            return items.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        // 아이템을 뷰로 보여주는 중요한 메소드
        public View getView(int i, View view, ViewGroup viewGroup) {
            DBItemView itemView = new DBItemView(getApplicationContext());
            DBItem item = items.get(i);
            itemView.setContent(item.getcontent());
            itemView.setampm(item.getampm());
            itemView.settimep(item.gettimep());
            return itemView;
        }
    }
}

