package me.joaosilva22.rocketlaunchtracker.fragments;

import android.app.Notification;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Date;

import me.joaosilva22.rocketlaunchtracker.R;
import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseContract;
import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseHelper;
import me.joaosilva22.rocketlaunchtracker.network.LaunchDetailsLoader;
import me.joaosilva22.rocketlaunchtracker.network.LaunchDetailsLoaderObserver;
import me.joaosilva22.rocketlaunchtracker.utils.CustomDateFormatter;
import me.joaosilva22.rocketlaunchtracker.utils.NotificationHelper;

public class LaunchDetailsFragment extends Fragment implements LaunchDetailsLoaderObserver {

    private static final String SQL_QUERY =
            "SELECT * FROM " + LaunchDatabaseContract.DetailsEntry.TABLE_NAME + " WHERE " +
            LaunchDatabaseContract.DetailsEntry.COLUMN_LAUNCH_ID + " = ?";

    private LaunchDetailsLoader loader;
    private LaunchDatabaseHelper helper;
    private SQLiteDatabase database;
    private SwipeRefreshLayout refresh;
    private ProgressBar progress;
    private Switch switc;
    private int id;

    private NotificationHelper notifier;
    private String start;

    public LaunchDetailsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader = new LaunchDetailsLoader(this.getContext());
        loader.register(this);
        helper = new LaunchDatabaseHelper(this.getContext());
        database = helper.getReadableDatabase();

        Bundle extras = getActivity().getIntent().getExtras();
        id = extras.getInt(UpcomingLaunchesFragment.EXTRA_LAUNCH_ID);
        loader.load(id);

        notifier = new NotificationHelper(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launch_details, container, false);

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.launch_details_refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loader.load(id);
            }
        });

        progress = (ProgressBar) view.findViewById(R.id.launch_details_progress_bar);
        progress.setIndeterminate(true);

        switc = (Switch) view.findViewById(R.id.launch_details_notification);
        switc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setNotify(b);
            }
        });

        refresh.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void update() {
        Cursor c = database.rawQuery(SQL_QUERY, new String[] {Integer.toString(id)});
        c.moveToFirst();

        fillMissionDescription(c);
        fillLaunchVehicle(c);
        fillLaunchPad(c);
        fillWindowStart(c);
        fillWindowEnd(c);
        updateSwitch(c);

        refresh.setRefreshing(false);
        refresh.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void fillMissionDescription(Cursor c) {
        int indexn = c.getColumnIndex(LaunchDatabaseContract.DetailsEntry.COLUMN_MISSION_NAME);
        String name = c.getString(indexn);

        int indexd = c.getColumnIndex(LaunchDatabaseContract.DetailsEntry.
                COLUMN_MISSION_DESCRIPTION);
        String description = c.getString(indexd);

        Spanned text = Html.fromHtml("<b>" + name + "</b> " + description);
        TextView t = (TextView) getView().findViewById(R.id.launch_details_mission_description);
        t.setText(text);
    }

    private void fillLaunchVehicle(Cursor c) {
        int index = c.getColumnIndex(LaunchDatabaseContract.DetailsEntry.COLUMN_ROCKET_NAME);
        String text = c.getString(index);

        TextView t = (TextView) getView().findViewById(R.id.launch_details_launch_vehicle);
        t.setText(text);
    }

    private void fillLaunchPad(Cursor c) {
        int index = c.getColumnIndex(LaunchDatabaseContract.DetailsEntry.COLUMN_PAD_NAME);
        String text = c.getString(index);

        TextView t = (TextView) getView().findViewById(R.id.launch_details_launch_pad);
        t.setText(text);
    }

    private void fillWindowStart(Cursor c) {
        int index = c.getColumnIndex(LaunchDatabaseContract.DetailsEntry.COLUMN_WINDOW_START);
        String s = c.getString(index);
        start = CustomDateFormatter.toSQL(s, CustomDateFormatter.Formats.DISPLAY,
                CustomDateFormatter.TimeZones.UTC);

        TextView t = (TextView) getView().findViewById(R.id.launch_details_window_start);
        t.setText(start);
    }

    private void fillWindowEnd(Cursor c) {
        int index = c.getColumnIndex(LaunchDatabaseContract.DetailsEntry.COLUMN_WINDOW_END);
        String e = c.getString(index);
        String end = CustomDateFormatter.toSQL(e, CustomDateFormatter.Formats.DISPLAY,
                CustomDateFormatter.TimeZones.UTC);

        TextView t = (TextView) getView().findViewById(R.id.launch_details_window_end);
        t.setText(end);
    }

    private void updateSwitch(Cursor c) {
        int index = c.getColumnIndex(LaunchDatabaseContract.DetailsEntry.COLUMN_NOTIFY);
        Log.d("whatever-dude", "c.getInt(index)="+c.getInt(index));
        Boolean notify = (c.getInt(index) != 0);
        switc.setChecked(notify);
    }

    private void setNotify(Boolean notify) {
        SQLiteDatabase write = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LaunchDatabaseContract.DetailsEntry.COLUMN_NOTIFY, (notify)? 1: 0);
        write.update(LaunchDatabaseContract.DetailsEntry.TABLE_NAME, values,
                LaunchDatabaseContract.DetailsEntry.COLUMN_LAUNCH_ID + "=" + id, null);

        if (notify) {
            Notification notification = notifier.getNotification("Launch", "Launch will begin shortly");
            Long time = CustomDateFormatter.toMillis(start, CustomDateFormatter.Formats.SQL) -
                    20 * 60 * 1000;
            notifier.scheduleNotification(notification, id, time);
        } else {
            notifier.unscheduleNotification(id);
        }
    }
}
