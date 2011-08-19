/**
 * Water Quality Manager for Android
 * Copyright (C) 2011 iCOMMS (University of Cape Town)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aquatest.ui;

import java.util.Vector;

import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Render a DataList table row
 */
public class DataListItemAdapter extends BaseAdapter {
	
	private Context mContext;
	private Vector <DataIndicator> dataIndicators;
	private boolean header;

	
	/**
	 * Constructor
	 * 
	 * @param c
	 * @param dI data to use in displaying the row
	 * @param _header whether this is a header row or not
	 */
	public DataListItemAdapter (Context c, Vector <DataIndicator> dI, boolean _header) {///, DataListAdapter pA){
		
		//parentAdapter = pA;
		mContext = c;
		dataIndicators = dI;
		header = _header;
		//dataCache = dC;
		
	}

	/**
	 * Number of cells
	 */
	public int getCount() {
		// TODO Auto-generated method stub
        return dataIndicators.size();

	}

	/**
	 * Not implemented.
	 */
	public Object getItem(int position) {
		// TODO Auto-generated method stub
        return position;

	}

	/**
	 *  Not implemented.
	 */
	public long getItemId(int position) {
		// TODO Auto-generated method stub
        return position;
	}

	/**
	 * Render an individual cell.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView textView;
        if (convertView == null) {
            textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, 44));//LayoutParams.WRAP_CONTENT));//34));//LayoutParams.FILL_PARENT));
        } else {
        	textView = (TextView) convertView;
        }
        //textView.set
        textView.setBackgroundColor(DataUtils.getColor(dataIndicators.get(position).colour));
        if (dataIndicators.get(position).colour.compareTo(TestIndicators.NONE) != 0)
        	textView.setTextColor(TestIndicators.WHITE_COLOR);

        textView.setText(dataIndicators.get(position).name);

        
        if (header) {
        	textView.setTextSize(14);
        	textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else textView.setTextSize(12);

        return textView;
	}
}
