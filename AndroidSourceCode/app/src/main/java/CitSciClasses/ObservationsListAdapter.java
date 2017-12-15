package CitSciClasses;

/**
 * Created by manojsre on 8/19/2014.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.nrel.citsci.R;

public class ObservationsListAdapter extends ArrayAdapter{

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public ObservationsListAdapter(Context context, int resourceId, List objects) {

        super( context, resourceId, objects );
        resource = resourceId;
        inflater = LayoutInflater.from( context );
        this.context=context;
    }

    @Override
    public View getView ( int index, View convertView, ViewGroup parent ) {

        /* Extract the projects's object to show */
        ObservationFile observationFile = (ObservationFile) this.getItem(index);

        CheckBox checkBox;
        if(convertView == null) {
            convertView = (RelativeLayout) inflater.inflate(resource, null);

            TextView txtName = (TextView) convertView.findViewById(R.id.observationName);
            txtName.setText(observationFile.getDisplayName());


            TextView txtId = (TextView) convertView.findViewById(R.id.observationTime);
            txtId.setText(String.valueOf(observationFile.getFileDate()));

            checkBox = (CheckBox) convertView.findViewById(R.id.checkFileBox);


            observationFile.setCheckBox(checkBox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    ObservationFile observationFile = (ObservationFile) cb.getTag();
                    observationFile.setIsChecked(cb.isChecked());
                }
            });
        }
        else{
            checkBox = (CheckBox) convertView.findViewById(R.id.checkFileBox);
        }

        checkBox.setTag(observationFile);
        return convertView;
    }
}