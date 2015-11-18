package com.marcoslopez7.stormy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcoslopez7.stormy.R;
import com.marcoslopez7.stormy.weather.Day;

/**
 * Created by user on 16/11/2015.
 */
public class DayAdapter extends BaseAdapter {
    private Context context;
    private Day[] days;

    public DayAdapter(Context context, Day[] days){
        context = context;
        days = days;
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int position) {
        return days[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.daily_list_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView)convertView.findViewById(R.id.icon);
            holder.temperature = (TextView) convertView.findViewById(R.id.temperature);
            holder.day = (TextView) convertView.findViewById(R.id.dayName);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        Day day = days[position];

        holder.icon.setImageResource(day.getIconID());
        holder.temperature.setText(day.getTemperatureMax() + "");


        if (position == 0){
            holder.day.setText("Today");
        }
        else
        {
            holder.day.setText(day.getDayOfTheWeek());
        }

        return convertView;
    }

    private static class ViewHolder{
        ImageView icon;
        TextView temperature;
        TextView day;
    }
}
