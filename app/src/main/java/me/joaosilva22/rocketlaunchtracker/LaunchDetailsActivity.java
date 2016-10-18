package me.joaosilva22.rocketlaunchtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import me.joaosilva22.rocketlaunchtracker.fragments.UpcomingLaunchesFragment;

public class LaunchDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_details);

        toolbar = (Toolbar) findViewById(R.id.launch_details_toolbar);
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra(UpcomingLaunchesFragment.EXTRA_LAUNCH_NAME);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }
}
