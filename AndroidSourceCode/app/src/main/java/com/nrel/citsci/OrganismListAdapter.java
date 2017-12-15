package com.nrel.citsci;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import CitSciClasses.OrganismList;

/**
 * Created by Manoj on 1/19/2015.
 */
public class OrganismListAdapter extends ArrayAdapter<OrganismList> {

    Context context;
    List<OrganismList> organismList;
    public OrganismListAdapter(Context context, int listItemResourceId,List<OrganismList> organisms) {
        super(context, listItemResourceId, organisms);
        this.context = context;
        this.organismList= organisms;
    }

    public View getCustomView(int position, View convertView,ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.entry_list_item, parent, false);
        TextView organismName = (TextView) item.findViewById(R.id.organismName);
        organismName.setTypeface(null, Typeface.BOLD);
        organismName.setText(organismList.get(position).getName());
        organismName.setTag(String.valueOf(organismList.get(position).getId()));
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
