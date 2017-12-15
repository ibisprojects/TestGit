package com.nrel.citsci;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Manoj on 1/19/2015.
 */
public class ImagesListAdapter extends ArrayAdapter<ImageFile> {

    Context context;
    ArrayList<ImageFile> imagesList;
    public ImagesListAdapter(Context context, int listItemResourceId, ArrayList<ImageFile> images) {
        super(context, listItemResourceId, images);
        this.context = context;
        this.imagesList= images;
    }

    public View getCustomView(int position, View convertView,ViewGroup parent) {

        CheckBox checkBox;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.image_list_item, parent, false);
            TextView imageFileName = (TextView) convertView.findViewById(R.id.imageFileName);
            imageFileName.setText(imagesList.get(position).getFile().getName());
            ImageView imageThumbnail = (ImageView) convertView.findViewById(R.id.imageThumbnail);

            Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagesList.get(position).getFile().getPath()), 120,120);
            imageThumbnail.setImageBitmap(resized);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkImageFileBox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    ImageFile imageFile = (ImageFile) cb.getTag();
                    imageFile.setChecked(cb.isChecked());
                }
            });
        }
        else{
            checkBox = (CheckBox) convertView.findViewById(R.id.checkImageFileBox);
        }
        checkBox.setTag(imagesList.get(position));
        return convertView;
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
