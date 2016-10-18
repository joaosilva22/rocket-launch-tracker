package me.joaosilva22.rocketlaunchtracker.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

public class LaunchDetailsLoader implements Response.Listener<JSONObject>, Response.ErrorListener {

    private static final String ERROR_MESSAGE = "Network error. Try again later.";
    private static final String URL_0 = "https://launchlibrary.net/1.2/launch?id=";
    private static final String URL_1 = "&mode=verbose";

    private Context context;
    private LaunchDatabaseHelper helper;
    private SQLiteDatabase database;
    private List<LaunchDetailsLoaderObserver> observers;

    public LaunchDetailsLoader(Context context) {
        this.context = context;
        helper = new LaunchDatabaseHelper(context);
        database = helper.getWritableDatabase();
        observers = new ArrayList<>();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray launches = response.getJSONArray("launches");
            ContentValues values = parseResponse(launches.getJSONObject(0));
            int id = launches.getJSONObject(0).getInt("id");
            database.update(LaunchDatabaseContract.DetailsEntry.TABLE_NAME, values,
                    LaunchDatabaseContract.DetailsEntry.COLUMN_LAUNCH_ID + "=" + id, null);
            database.insertWithOnConflict(LaunchDatabaseContract.DetailsEntry.TABLE_NAME, null,
                    values, SQLiteDatabase.CONFLICT_IGNORE);
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

    private ContentValues parseResponse(JSONObject response) throws JSONException {
        ContentValues values = new ContentValues();

        values.put(LaunchDatabaseContract.DetailsEntry.COLUMN_LAUNCH_ID,
                response.getInt("id"));
        values.put(LaunchDatabaseContract.DetailsEntry.COLUMN_WINDOW_START,
                response.getString("windowstart"));
        values.put(LaunchDatabaseContract.DetailsEntry.COLUMN_WINDOW_END,
                response.getString("windowend"));

        JSONArray missions = response.getJSONArray("missions");
        JSONObject primaryMission = missions.getJSONObject(0);
        values.put(LaunchDatabaseContract.DetailsEntry.COLUMN_MISSION_NAME,
                primaryMission.getString("name"));
        values.put(LaunchDatabaseContract.DetailsEntry.COLUMN_MISSION_DESCRIPTION,
                primaryMission.getString("description"));

        JSONObject location = response.getJSONObject("location");
        JSONArray pads = location.getJSONArray("pads");
        JSONObject launchPad = pads.getJSONObject(0);
        values.put(LaunchDatabaseContract.DetailsEntry.COLUMN_PAD_NAME,
                launchPad.getString("name"));

        JSONObject rocket = response.getJSONObject("rocket");
        values.put(LaunchDatabaseContract.DetailsEntry.COLUMN_ROCKET_NAME,
                rocket.getString("name"));

        return values;
    }

    public void load(int id) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_0 + id + URL_1,
                null, this, this);
        CustomRequestQueue.getInstance(context).add(request);
    }

    public void register(LaunchDetailsLoaderObserver observer) {
        observers.add(observer);
    }

    public void unregister(LaunchDetailsLoaderObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (LaunchDetailsLoaderObserver observer : observers) {
            observer.update();
        }
    }
}
