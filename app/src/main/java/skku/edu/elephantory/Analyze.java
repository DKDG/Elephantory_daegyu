package skku.edu.elephantory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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
import java.util.List;
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

    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        editText = findViewById(R.id.editTextURL);

        Button button = findViewById(R.id.buttonRequest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "Connect to Hadoop history server";
                String message = "Would you like to get the results?";
                String titleButtonYes = "Yes";
                String titleButtonNo = "No";

                AlertDialog dialog = makeRequestDialog(title, message, titleButtonYes, titleButtonNo);
                dialog.show();

                Toast.makeText(Analyze.this, "Connecting...", Toast.LENGTH_SHORT).show();


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

        // delete error
        MySwipeHelper swipeHelper= new MySwipeHelper(Analyze.this, recyclerView,300) {
            @Override
            public void instantiatrMyButton(final RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(Analyze.this,
                        "Delete",
                        30,
                        R.drawable.ic_delete_black_24dp,
                        Color.parseColor("#FF3C30"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Toast.makeText(Analyze.this, "Delete click", Toast.LENGTH_SHORT).show();
                                Log.d("TAG",viewHolder.getAdapterPosition() + "");
                                jobList.jobResult.jobResultList.remove(viewHolder.getAdapterPosition());                // 해당 항목 삭제
                                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());    // Adapter에 알려주기.
                                //viewHolder.itemView.setVisibility(View.GONE);

                            }
                        }));
                /*
                buffer.add(new MyButton(Analyze.this,
                        "Update",
                        30,
                        R.drawable.ic_edit_white_24dp,
                        Color.parseColor("#03DAC5"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Toast.makeText(Analyze.this, "edit click", Toast.LENGTH_SHORT).show();
                                //TODO: 편집할 코드
                            }
                        }));
                 */
            }
        };// swipeHelper()

    }// onCreate()


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
             sql = String.format("INSERT OR IGNORE INTO Job VALUES(NULL, '%s', '%s', '%s', '%s');",
                    tmpJob.job_id, tmpJob.name, tmpJob.user, tmpJob.elapsed_time);
            db.execSQL(sql);
        }
        Toast.makeText(Analyze.this, "Insert to DB: Success", Toast.LENGTH_SHORT).show();
    }


    private AlertDialog makeRequestDialog(CharSequence title, CharSequence message,
                                          CharSequence titleButtonYes, CharSequence titleButtonNo) {
        AlertDialog.Builder requestDialog = new AlertDialog.Builder(this);
        requestDialog.setTitle(title);
        requestDialog.setMessage(message);
        requestDialog.setPositiveButton(titleButtonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Analyze.this, "It will take 5 seconds.", Toast.LENGTH_SHORT).show();

                /*
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Analyze.this, "Request completed.", Toast.LENGTH_SHORT).show();
                    }
                }, 5000);
                 */
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                makeRequest();
            }
        });



        requestDialog.setNegativeButton(titleButtonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        return requestDialog.create();
    }


    public void printDebug(String data) {
        Log.d("Analyze", data);
    }

}