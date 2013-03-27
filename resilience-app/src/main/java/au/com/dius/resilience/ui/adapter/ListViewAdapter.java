package au.com.dius.resilience.ui.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import au.com.dius.resilience.R;
import au.com.dius.resilience.model.Category;
import au.com.dius.resilience.model.Incident;
import au.com.dius.resilience.persistence.repository.impl.PreferenceAdapter;
import au.com.justinb.open311.model.ServiceRequest;

import java.util.Date;
import java.util.List;

/**
 * @author georgepapas
 */
public class ListViewAdapter extends ArrayAdapter<ServiceRequest> {

  public static final String DRAWABLE = "drawable";
  public static final String DARK_THEME_SUFFIX = "_white";
  private PreferenceAdapter preferenceAdapter;

  public ListViewAdapter(Context context, int textViewResourceId, List<ServiceRequest> incidents) {
    super(context, textViewResourceId, incidents);
    preferenceAdapter = new PreferenceAdapter(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View rowView = inflater.inflate(R.layout.incident_list_view_item, null);

    ServiceRequest serviceRequest = getItem(position);

    TextView nameField = (TextView) rowView.findViewById(R.id.list_view_item_name);
    nameField.setText(serviceRequest.getServiceName());

    TextView reportedTime = (TextView) rowView.findViewById(R.id.list_view_item_time_reported);
    reportedTime.setText(DateUtils.getRelativeDateTimeString(
            getContext(),
            serviceRequest.getRequestedDatetime().getTime(),
            DateUtils.SECOND_IN_MILLIS,
            DateUtils.YEAR_IN_MILLIS, 0));

    return rowView;
  }

  public void setData(List<ServiceRequest> incidents) {
    clear();
    if (incidents == null) {
      return;
    }

    addAll(incidents);
  }
}
