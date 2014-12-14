/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Sliding Menu stuff
 * @author Jonathan
 *
 */
public class NavDrawerListAdapter extends BaseAdapter {
	
	private Activity context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	public NavDrawerListAdapter(Activity context, ArrayList<NavDrawerItem> navDrawerItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		
		NavDrawerItem item = navDrawerItems.get(position);
		
		switch(navDrawerItems.get(position).SLIDING_MENU_TAB) {
		case MainActivity.TYPE_IC:
			convertView = inflater.inflate(R.layout.sliding_tab_ic, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			Font.applyFont(context, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAB.TTF");
			
			if(navDrawerItems.get(position).isImage)
				((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(context.getResources().getDrawable(item.icon));
			else
				((ImageView) convertView.findViewById(R.id.icon)).setVisibility(View.GONE);
			
			break;		
		
		case MainActivity.TYPE_NORMAL:
			convertView = inflater.inflate(R.layout.sliding_tab, parent, false);
			
			((TextView) convertView.findViewById(R.id.title)).setText(item.title);
			Font.applyFont(context, ((TextView) convertView.findViewById(R.id.title)), "fonts/MYRIADAM.TTF");
			
			if(navDrawerItems.get(position).onCheckedChangeListener!=null) {
				((ToggleButton) convertView.findViewById(R.id.toggle)).setVisibility(View.VISIBLE);
				((ToggleButton) convertView.findViewById(R.id.toggle)).setChecked(item.initChecked);
				((ToggleButton) convertView.findViewById(R.id.toggle)).setOnCheckedChangeListener(item.onCheckedChangeListener);
			}
			
			break;		
		}
        
        return convertView;
	}
}
