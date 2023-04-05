package ttu.cse.lab200601_db;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.BLOB;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String DB_FILE = "friends.db",
            DB_TABLE = "friends";

    private FriendDbOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private Cursor c;
    private int i;
    private Spinner spinner;
    private EditText mEdtName, mEdtSex, mEdtAddr;
    private TextView mTxtList;
   // private TextView sum;
    private ImageView image;
    final String[] school = {"增加","消耗"};
    SimpleCursorAdapter adapter = null;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);

        mEdtName = findViewById(R.id.edtName);
        mEdtSex = findViewById(R.id.edtSex);
        //mEdtAddr = findViewById(R.id.edtAddr);
        mTxtList = findViewById(R.id.txtList);
        //sum=findViewById(R.id.sum);
        image = findViewById(R.id.imageView);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> lunchList = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, school);
        spinner.setAdapter(lunchList);
        i = spinner.getSelectedItemPosition();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i = spinner.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //image.setImageResource(R.drawable.d);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnQuery = findViewById(R.id.btnQuery);
        Button btnList = findViewById(R.id.btnList);

        btnAdd.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnList.setOnClickListener(this);
        lv.setOnItemClickListener(this);
        dbOpenHelper = new FriendDbOpenHelper(this, DB_FILE, null, 1);
        db = dbOpenHelper.getWritableDatabase();
        // openOrCreateDatabase("freinds", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT exists " + DB_TABLE + " (" + "_id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," + "sex TEXT," + "address TEXT);");

        c = db.rawQuery("select * from " + DB_TABLE, null);
        db.close();
    }
    protected void onResume() {
        db = dbOpenHelper.getWritableDatabase();
        c = db.query(DB_TABLE,new String[]{"_id","name", "sex", "address"},null ,null,null,null,null);
        if (adapter == null) {
            if(c==null)
                return;
            adapter = new SimpleCursorAdapter(this, R.layout.list_item, c,
                    new String[]{"name", "sex", "address"},
                    new int[]{R.id.textView3, R.id.textView2, R.id.textView},
                    0);
            lv.setAdapter(adapter);
        } else
            adapter.changeCursor(c);//更新ListView呈現的資料
        super.onResume();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // 設置要用哪個menu檔做為選單
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // 取得點選項目的id
        int id = item.getItemId();

        // 依照id判斷點了哪個項目並做相應事件
        if (id == R.id.action_settings) {
            // 按下「設定」要做的事
            Toast.makeText(this, "選擇旁邊選項帶你了解熱量增加和消耗", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_help) {
            Uri uri = Uri.parse("https://www.hpa.gov.tw/Pages/Detail.aspx?nodeid=571&pid=9738");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_about) {
            // 按下「關於」要做的事
            Uri uri = Uri.parse("http://www.scu.edu.tw/health/Work/form1.php");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_gg) {
            // 按下「關於」要做的事
            Uri uri = Uri.parse("https://www.youtube.com/channel/UCSSjn1X6yMBC3AyJ2azeG7A");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_ga) {
            // 按下「關於」要做的事
            Uri uri = Uri.parse("https://www.youtube.com/channel/UCLwFOT4tHGaK9kqXXExhPFQ");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                //String name = Integer.toString(i);
                String name = school[i];
                String sex = mEdtSex.getText().toString();
                String addr = mEdtName.getText().toString();
                addData(name,  sex, addr);
                break;
            case R.id.btnQuery:
                queryDB();
                break;
            case R.id.btnList:
                listAll();
                break;
            case R.id.select:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                //dialog.setTitle("基本訊息對話按鈕");
                dialog.setMessage("此APP主要目的是幫助你紀錄熱量，想知道運動消耗和食物增加的熱量請點選APP右上角");
                dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Toast.makeText(MainActivity.this, "我還尚未了解",Toast.LENGTH_SHORT).show();
                    }

                });
                dialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Toast.makeText(MainActivity.this, "我了解了",Toast.LENGTH_SHORT).show();
                    }

                });
                dialog.setNeutralButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(MainActivity.this, "取消",Toast.LENGTH_SHORT).show();
                    }

                });
                dialog.show();
        }
    }

    private void listAll() {
        db = dbOpenHelper.getWritableDatabase();
        c = db.rawQuery("SELECT * FROM " + DB_TABLE , null);
        int n=0;
        if (c == null)
            return;
        if (c.getCount() == 0) {
            mTxtList.setText("");
            Toast.makeText(MainActivity.this, "沒有這筆資料", Toast.LENGTH_LONG).show();
        }
        else {
            adapter.changeCursor(c);
            //n+=Integer.parseInt(c.getString(2));
        }
        //sum.setText(Integer.toString(n));
        db.close();
    }

    private void queryDB() {
        db = dbOpenHelper.getWritableDatabase();
        //String name = Integer.toString(i);
        String name = school[i];
        String sex = mEdtSex.getText().toString();
        //String addr = mEdtAddr.getText().toString();
        String addr = mEdtName.getText().toString();
        if (!name.equals(""))
            c = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE " + "name='" + name + "'", null);
        else if (!sex.equals(""))
            c = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE " + "sex='" + sex + "'", null);
        else if (!addr.equals(""))
            c = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE " + "address='" + addr + "'", null);
        //image.setImageResource(R.drawable.woman);
        if (c == null)
            return;

        if (c.getCount() == 0) {
            mTxtList.setText("");
            Toast.makeText(MainActivity.this, "沒有這筆資料", Toast.LENGTH_LONG).show();
        }
        else
            adapter.changeCursor(c);

        db.close();
    }

    private void addData(String n, String s, String addr) {
        db = dbOpenHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", n);
        cv.put("sex", s);
        cv.put("address", addr);
        db.insert(DB_TABLE, null, cv);
        db.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        intent.setClass(this, Modify.class); //設定新活動視窗類別
        Bundle bdl = new Bundle();
        bdl.putString("id", l + "");//將arg3傳遞到新的活動視窗中
        intent.putExtras(bdl);
        startActivity(intent); //開啟新的活動視窗
    }
}

