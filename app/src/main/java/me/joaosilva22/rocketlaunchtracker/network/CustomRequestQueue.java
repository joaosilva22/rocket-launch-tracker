package me.joaosilva22.rocketlaunchtracker.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class CustomRequestQueue {

    private static CustomRequestQueue instance;
    private RequestQueue requestQueue;
    private static Context context;

    private CustomRequestQueue(Context context) {
        CustomRequestQueue.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized CustomRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new CustomRequestQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void add(Request<T> request) {
        getRequestQueue().add(request);
    }
}
