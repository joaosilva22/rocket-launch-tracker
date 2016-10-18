package me.joaosilva22.rocketlaunchtracker.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import me.joaosilva22.rocketlaunchtracker.LaunchDetailsActivity;
import me.joaosilva22.rocketlaunchtracker.R;
import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseContract;
import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseHelper;
import me.joaosilva22.rocketlaunchtracker.network.LaunchListLoader;
import me.joaosilva22.rocketlaunchtracker.network.LaunchListLoaderObserver;
import me.joaosilva22.rocketlaunchtracker.views.adapters.LaunchListCursorAdapter;

public class UpcomingLaunchesFragment extends Fragment implements LaunchListLoaderObserver {

    public static final String EXTRA_LAUNCH_ID = "me.joaosilva22.volleytest.LAUNCH_ID";
    public static final String EXTRA_LAUNCH_NAME = "me.joaosilva22.volleytest.LAUNCH_NAME";

    private static final String SQL_QUERY =
            "SELECT * FROM " + LaunchDatabaseContract.LaunchEntry.TABLE_NAME + " WHERE " +
            LaunchDatabaseContract.LaunchEntry.COLUMN_NET + " > datetime('now', 'localtime')" +
            "ORDER BY datetime(" + LaunchDatabaseContract.LaunchEntry.COLUMN_NET + ") ASC";

    private LaunchListLoader loader;
    private LaunchDatabaseHelper helper;
    private SQLiteDatabase database;
    private LaunchListCursorAdapter adapter;
    private SwipeRefreshLayout refresh;

    public UpcomingLaunchesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader = new LaunchListLoader(this.getContext());
        loader.register(this);
        helper = new LaunchDatabaseHelper(this.getContext());
        database = helper.getReadableDatabase();

        loader.load();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_launches, container, false);

        Cursor cursor = database.rawQuery(SQL_QUERY, null);
        adapter = new LaunchListCursorAdapter(this.getContext(), cursor);
        ListView list = (ListView) view.findViewById(R.id.upcoming_launches_list);
        list.setEmptyView(view.findViewById(R.id.upcoming_launches_empty));
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), LaunchDetailsActivity.class);
                intent.putExtra(EXTRA_LAUNCH_NAME, getLaunchName(view));
                intent.putExtra(EXTRA_LAUNCH_ID, getLaunchId(view));
                startActivity(intent);
            }
        });

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.upcoming_launches_refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loader.load();
            }
        });
        return view;
    }

    @Override
    public void update() {
        Cursor cursor = database.rawQuery(SQL_QUERY, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
        refresh.setRefreshing(false);
    }

    private int getLaunchId(View view) {
        TextView t = (TextView) view.findViewById(R.id.list_entry_launch_id);
        return Integer.parseInt(t.getText().toString());
    }

    private String getLaunchName(View view) {
        TextView t = (TextView) view.findViewById(R.id.list_entry_launch_name);
        return t.getText().toString();
    }
}
