package skku.edu.elephantory;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Database extends AppCompatActivity {

    TextView textView;
    DBHelper dbHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        textView = findViewById(R.id.textViewQuery);

        Button buttonSearch = findViewById(R.id.buttonSearchDB);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQuery();
            }
        });

        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteQuery();
            }
        });
    }

    public void searchQuery() {
        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM Job";
        Cursor cursor = db.rawQuery(sql, null);

        printDebug("///// searchQuery()");


        int recordCount = cursor.getCount();
        println("Total number of record: " + recordCount);
        for(int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String job_id = cursor.getString(0);
            String name = cursor.getString(1);
            String user = cursor.getString(2);
            String elapsed_time = cursor.getString(3);

            println("Record #: " + job_id + ", " + name + ", " + user + ", " + elapsed_time);
        }

        cursor.close();
    }

    public void deleteQuery() {
        printDebug("///// deleteQuery()");
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        String sql = "delete from Job;";
        db.execSQL(sql);
        Toast.makeText(Database.this, "Delete all records in table;", Toast.LENGTH_SHORT).show();
    }

    public void println(String data) {
        textView.append(data + "\n");
    }

    public void printDebug(String data) {
        Log.d("Database", data);
    }

}
