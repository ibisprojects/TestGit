package com.nrel.citsci;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import CitSciClasses.Location;

/**
 * Created by Manoj on 1/19/2015.
 */
public class LocationListAdapter extends ArrayAdapter<Location> {

    Context context;
    List<Location> locationList;
    public LocationListAdapter(Context context, int listItemResourceId, List<Location> locations) {
        super(context, listItemResourceId, locations);
        this.context = context;
        this.locationList= locations;
    }

    public View getCustomView(int position, View convertView,ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.entry_list_item, parent, false);
        TextView organismName = (TextView) item.findViewById(R.id.organismName);
        organismName.setText(locationList.get(position).getAreaName());
        organismName.setTag(String.valueOf(locationList.get(position).getAreadId()));
        return item;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
