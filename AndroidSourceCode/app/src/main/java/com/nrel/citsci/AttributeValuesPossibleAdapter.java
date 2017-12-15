package com.nrel.citsci;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import CitSciClasses.AttributeValuesPossible;

/**
 * Created by Manoj on 1/20/2015.
 */
public class AttributeValuesPossibleAdapter  extends ArrayAdapter<AttributeValuesPossible> {

    Context context;
    List<AttributeValuesPossible> attributeValuesPossibleList;
    public AttributeValuesPossibleAdapter(Context context, int listItemResourceId,List<AttributeValuesPossible> attributeValuesPossibleList) {
        super(context, listItemResourceId, attributeValuesPossibleList);
        this.context = context;
        this.attributeValuesPossibleList= attributeValuesPossibleList;
    }

    public View getCustomView(int position, View convertView,ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.entry_list_item, parent, false);
        TextView attributeName = (TextView) item.findViewById(R.id.organismName);
        attributeName.setText(attributeValuesPossibleList.get(position).getName());
        attributeName.setTag(String.valueOf(attributeValuesPossibleList.get(position).getId()));
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

