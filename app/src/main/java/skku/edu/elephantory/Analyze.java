package skku.edu.elephantory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Analyze extends AppCompatActivity {
    EditText editText;

    static RequestQueue requestQueue;
    JobList jobList;

    RecyclerView recyclerView;
    JobAdapter adapter;

    DBHelper dbHelper;
    String dbName = "job_history";
    String tableName;
    SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        editText = findViewById(R.id.editTextURL);

        Button button = findViewById(R.id.buttonRequest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });

        Button button2 = findViewById(R.id.buttonDB);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDB = new Intent(getApplicationContext(), Database.class);
                startActivity(intentDB);
            }
        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        recyclerView = findViewById(R.id.recyclerViewAnalyze); // XML 레이아웃에 정의한 리싸이클러뷰 객체 참조

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new JobAdapter();
        recyclerView.setAdapter(adapter); // 리싸이클러뷰에 어댑터 설정

        createDatabase(dbName);

    }

    public void makeRequest() {
        String url = editText.getText().toString();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        printDebug("응답 -> " + response);

                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        printDebug("에러 -> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();

                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        printDebug("요청 보냄.");
    }

    public void processResponse(String response) {
        Gson gson = new Gson();
        jobList = gson.fromJson(response, JobList.class);

        printDebug("Total number of jobs : " + jobList.jobResult.jobResultList.size());

        for (int i = 0; i < jobList.jobResult.jobResultList.size(); i++) {
            Job job = jobList.jobResult.jobResultList.get(i);

            adapter.addItem(job);
        }

        adapter.notifyDataSetChanged();

        // After get data from web, then insert data to DB
        insertDB();
    }

    private void createDatabase(String name) {
        printDebug("///// createDatabase()");
        // DBHelper 객체 생성
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        printDebug("///// Database=" + name);
    }

    private void insertDB() {
        Job tmpJob;
        String sql;

        for(int i = 0; i < jobList.jobResult.jobResultList.size(); i++) {
             tmpJob = jobList.jobResult.jobResultList.get(i);
             sql = String.format("INSERT INTO Job VALUES(NULL, '%s', '%s', '%s', '%s');",
                    tmpJob.job_id, tmpJob.name, tmpJob.user, tmpJob.elapsed_time);
            db.execSQL(sql);
        }
        Toast.makeText(Analyze.this, "Insert to DB: Success", Toast.LENGTH_SHORT).show();
    }

    public void printDebug(String data) {
        Log.d("Analyze", data);
    }

}