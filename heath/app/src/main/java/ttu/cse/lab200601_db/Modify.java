package ttu.cse.lab200601_db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Modify extends Activity implements View.OnClickListener {
    EditText nameET, sexET, addressET;
    String id;
    private static final String DB_FILE = "friends.db",
            DB_TABLE = "friends";
    private FriendDbOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motify);
        nameET=findViewById(R.id.editText1);
        sexET=findViewById(R.id.editText2);

        Button btn=findViewById(R.id.button1);
        btn.setOnClickListener(this);
        btn=findViewById(R.id.button2);
        btn.setOnClickListener(this);
        dbOpenHelper = new FriendDbOpenHelper(this, DB_FILE, null, 1);
        db = dbOpenHelper.getWritableDatabase();
        Bundle bdl=getIntent().getExtras();
        id=bdl.getString("id","0");
        //資料查詢，條件為_id等於上一個活動視窗傳遞過來的資料
        Cursor c = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE " + "_id='" + id + "'", null);
        c.moveToFirst();
        nameET.setText(c.getString(2));
        sexET.setText(c.getString(3));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                db = dbOpenHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                if (nameET != null) cv.put("sex", nameET.getText().toString());
                if (sexET != null) cv.put("address", sexET.getText().toString());
                db.update(DB_TABLE, cv, "_id"+ "="+id, null);
                db.close();
                break;
            case R.id.button2:
                db = dbOpenHelper.getWritableDatabase();
                db.delete(DB_TABLE,"_id "+ " =" + id, null);//資料表中的資料刪除
                db.close();
                break;
        }
        finish();//關閉修改刪除活動視窗
    }
}
