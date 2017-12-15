package CitSciClasses;

/**
 * Created by manojsre on 8/19/2014.
 */

        import java.util.List;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import com.nrel.citsci.R;

public class ProjectListAdapter extends ArrayAdapter{

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public ProjectListAdapter ( Context context, int resourceId, List objects) {

        super( context, resourceId, objects );
        resource = resourceId;
        inflater = LayoutInflater.from( context );
        this.context=context;
    }

    @Override
    public View getView ( int index, View convertView, ViewGroup parent ) {

        /* create a new view of my layout and inflate it in the row */
        convertView = ( RelativeLayout ) inflater.inflate( resource, null );

        /* Extract the projects's object to show */
        Project project = (Project) this.getItem( index );

        /* Take the TextView from layout and set the city's name */
        TextView txtName = (TextView) convertView.findViewById(R.id.projectName);
        txtName.setText(project.getProjectName());

        return convertView;
    }
}