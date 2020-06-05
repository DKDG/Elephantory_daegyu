package skku.edu.elephantory;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private View contentView;
    private View loadingView;
    private int animationDuration;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        contentView = findViewById(R.id.textView_result);
        // Initially hide the content view.
        contentView.setVisibility(View.GONE);

        loadingView = (ProgressBar) findViewById(R.id.loading_spinner); // Final so we can access it from the other thread
        loadingView.setVisibility(View.VISIBLE);

// Create a Handler instance on the main thread
        final Handler handler = new Handler();

// Create and start a new Thread
        new Thread(new Runnable() {
            public void run() {
                try{
                    Thread.sleep(5000);
                }
                catch (Exception e) { } // Just catch the InterruptedException

                // Now we use the Handler to post back to the main thread
                handler.post(new Runnable() {
                    public void run() {
                        // Set the View's visibility back on the main UI Thread
                        loadingView.setVisibility(View.INVISIBLE);
                        contentView.setVisibility(View.VISIBLE);
                    }
                });
            }

        }).start();

        // Retrieve and cache the system's default "short" animation time.
        animationDuration = getResources().getInteger(
                android.R.integer.config_longAnimTime);

        String id = "";
        String korean = "";
        String english = "";

        Bundle extras = getIntent().getExtras();

        id = extras.getString("id");
        korean = extras.getString("korean");
        english = extras.getString("english");


        TextView textView = (TextView) findViewById(R.id.textView_result);

        String str = id + '\n' + english + '\n' + korean;
        textView.setText(str);
    }

    private void crossfade() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        contentView.setAlpha(0f);
        contentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        contentView.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        loadingView.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }
                });
    }
}

