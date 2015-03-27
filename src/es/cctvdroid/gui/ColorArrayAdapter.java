package es.cctvdroid.gui;

import java.util.List;

import es.cctvdroid.bd.model.Camara;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

@SuppressWarnings("hiding")
public class ColorArrayAdapter<Camara> extends ArrayAdapter<Camara> {

	private int[] colors = new int[] { 0x6E6E6E6E, 0x00000000 };
	
	public ColorArrayAdapter(Context context, int textViewResourceId, List<Camara> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);
		return view;
	}
}
