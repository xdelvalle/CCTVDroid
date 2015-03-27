package es.cctvdroid.gui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import es.cctvdroid.R;
import es.cctvdroid.bd.model.Camara;

public class ListaCamarasArrayAdapter extends ArrayAdapter<Camara> {

	Activity activity;
	private int[] colors = new int[] { 0x41414141, 0x00000000 };
	
	ArrayList<Camara> items;
	
	public ListaCamarasArrayAdapter(Activity a, Context context, int textViewResourceId, ArrayList<Camara> objects) {
		super(context, textViewResourceId, objects);
		this.activity = a;
		this.items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		if(view == null) {
			LayoutInflater in = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = in.inflate(R.layout.row, null);
		}

		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);
		
		Camara o = items.get(position);
		if (o != null) {
			TextView tt = (TextView) view.findViewById(R.id.toptext);
			TextView bt = (TextView) view.findViewById(R.id.bottomtext);
			if (tt != null) {
				tt.setText(o.getNombreCamara());
			}
			if (bt != null) {
				bt.setText(o.getIp());
			}
		}
		
		return view;
	}
}
