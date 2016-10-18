package me.joaosilva22.rocketlaunchtracker.views.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import me.joaosilva22.rocketlaunchtracker.R;
import me.joaosilva22.rocketlaunchtracker.models.LaunchDatabaseContract;
import me.joaosilva22.rocketlaunchtracker.utils.CustomDateFormatter;

public class LaunchListCursorAdapter extends CursorAdapter{

    public LaunchListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.view_list_entry, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndexOrThrow(
                LaunchDatabaseContract.LaunchEntry.COLUMN_NAME));
        String net = cursor.getString(cursor.getColumnIndexOrThrow(
                LaunchDatabaseContract.LaunchEntry.COLUMN_NET));
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(
                LaunchDatabaseContract.LaunchEntry.COLUMN_LAUNCH_ID));

        TextView textViewName = (TextView) view.findViewById(R.id.list_entry_launch_name);
        TextView textViewNet = (TextView) view.findViewById(R.id.list_entry_launch_net);
        TextView textViewId = (TextView) view.findViewById(R.id.list_entry_launch_id);

        textViewName.setText(name);
        textViewNet.setText(CustomDateFormatter.toDisplay(net,
                CustomDateFormatter.Formats.SQL));
        textViewId.setText(Integer.toString(id));

        String letter = String.valueOf(name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable = TextDrawable.builder().buildRound(letter, generator.getColor(name));
        ImageView round = (ImageView) view.findViewById(R.id.list_entry_round_image);
        round.setImageDrawable(drawable);
    }
}
