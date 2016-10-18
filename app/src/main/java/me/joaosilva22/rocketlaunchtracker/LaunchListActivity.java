package me.joaosilva22.rocketlaunchtracker;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import me.joaosilva22.rocketlaunchtracker.fragments.PastLaunchesFragment;
import me.joaosilva22.rocketlaunchtracker.fragments.UpcomingLaunchesFragment;
import me.joaosilva22.rocketlaunchtracker.views.adapters.ViewPagerAdapter;

public class LaunchListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_list);

        toolbar = (Toolbar) findViewById(R.id.launch_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Launch Schedule");

        viewPager = (ViewPager) findViewById(R.id.launch_list_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.launch_list_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UpcomingLaunchesFragment(), "Upcoming");
        adapter.addFragment(new PastLaunchesFragment(), "Past");
        viewPager.setAdapter(adapter);
    }
}
