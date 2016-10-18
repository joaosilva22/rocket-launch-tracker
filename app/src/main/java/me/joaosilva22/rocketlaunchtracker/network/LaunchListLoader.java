package me.joaosilva22.rocketlaunchtracker.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseContract;
import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseHelper;
import me.joaosilva22.rocketlaunchtracker.utils.CustomDateFormatter;

public class LaunchListLoader implements Response.Listener<JSONObject>, Response.ErrorListener {

    private static final String ERROR_MESSAGE = "Network error. Try again later.";
    private static final String URL = "https://launchlibrary.net/1.2/launch?next=10";

    private Context context;
    private JsonObjectRequest request;
    private LaunchDatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private List<LaunchListLoaderObserver> observers;

    public LaunchListLoader(Context context) {
        this.context = context;
        request = new JsonObjectRequest(Request.Method.GET, URL, null, this, this);
        databaseHelper = new LaunchDatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        observers = new ArrayList<>();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray launches = response.getJSONArray("launches");
            for (int i = 0; i < launches.length(); i++) {
                JSONObject launch = launches.getJSONObject(i);
                ContentValues values = parseResponse(launch);
                database.replace(LaunchDatabaseContract.LaunchEntry.TABLE_NAME, null, values);
            }
            notifyObservers();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast toast = Toast.makeText(context, ERROR_MESSAGE, Toast.LENGTH_SHORT);
        toast.show();
        notifyObservers();
    }

    private ContentValues parseResponse(JSONObject response) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(LaunchDatabaseContract.LaunchEntry.COLUMN_LAUNCH_ID, response.getInt("id"));
        values.put(LaunchDatabaseContract.LaunchEntry.COLUMN_NAME, response.getString("name"));
        String date = CustomDateFormatter.toSQL(response.getString("net"),
                CustomDateFormatter.Formats.DEFAULT, CustomDateFormatter.TimeZones.UTC);
        values.put(LaunchDatabaseContract.LaunchEntry.COLUMN_NET, date);
        return values;
    }

    public void load() {
        CustomRequestQueue.getInstance(context).add(request);
    }

    public void register(LaunchListLoaderObserver observer) {
        observers.add(observer);
    }

    public void unregister(LaunchListLoaderObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (LaunchListLoaderObserver observer : observers) {
            observer.update();
        }
    }
}
