package me.joaosilva22.rocketlaunchtracker.fragments;

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
import android.widget.ListView;

import me.joaosilva22.rocketlaunchtracker.R;
import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseContract;
import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseHelper;
import me.joaosilva22.rocketlaunchtracker.views.adapters.LaunchListCursorAdapter;

public class PastLaunchesFragment extends Fragment {

    private static final String SQL_QUERY =
            "SELECT * FROM " + LaunchDatabaseContract.LaunchEntry.TABLE_NAME + " WHERE " +
            LaunchDatabaseContract.LaunchEntry.COLUMN_NET + " < datetime('now', 'localtime')" +
            "ORDER BY datetime(" + LaunchDatabaseContract.LaunchEntry.COLUMN_NET + ") DESC";

    private LaunchDatabaseHelper helper;
    private SQLiteDatabase database;
    private LaunchListCursorAdapter adapter;
    private SwipeRefreshLayout refresh;

    public PastLaunchesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new LaunchDatabaseHelper(this.getContext());
        database = helper.getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_launches, container, false);

        Cursor cursor = database.rawQuery(SQL_QUERY, null);
        adapter = new LaunchListCursorAdapter(this.getContext(), cursor);
        ListView list = (ListView) view.findViewById(R.id.past_launches_list);
        list.setEmptyView(view.findViewById(R.id.past_launches_empty));
        list.setAdapter(adapter);

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.past_launches_refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requery();
            }
        });

        return view;
    }

    public void requery() {
        Cursor cursor = database.rawQuery(SQL_QUERY, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
        refresh.setRefreshing(false);
    }
}
