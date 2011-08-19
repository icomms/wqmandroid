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

import android.content.Context;
import android.database.DataSetObserver;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.aquatest.dbinterface.tools.DataCache;
import com.aquatest.dbinterface.tools.DataIndicator;

/**
 * Render a DataList table/
 */
public class DataListAdapter implements ListAdapter {
	
	private Context mContext;
	public DataCache dataCache;
	private Vector <Vector <DataIndicator>> rows;
	
	
	/**
	 * Constructor
	 * 
	 * @param c
	 * @param _rows data to populate the table with.
	 */
	public DataListAdapter (Context c, Vector <Vector <DataIndicator>> _rows){
		
		//dataCache = dC;
		mContext = c;
		rows = _rows;
	}

	/**
	 * Number of rows.
	 */
	public int getCount() {
		// TODO Auto-generated method stub
		// Log.v("leo","datalistAdapter getCount"+rows.size());
        return rows.size();
	}

	/**
	 * Not implemented.
	 */
	public Object getItem(int position) {
		// TODO Auto-generated method stub
        return new Integer(position);

	}

	/**
	 * Not implemented.
	 */
	public long getItemId(int position) {
		// TODO Auto-generated method stub
        return position;

	}
	
	/**
	 * Not implemented.
	 */
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Not implemented.
	 */
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Not implemented.
	 */
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Not implemented.
	 */
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	/**
	 * Not implemented.
	 */
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Not implemented.
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Not implemented.
	 */
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	/**
	 * Not implemented.
	 */
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
	}

	/**
	 * Render an individual row.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// Log.v("leo","getView");
		
		Vector <DataIndicator> currentRow = rows.get(position);
		
		GridView gridView;
        if (convertView == null) {
        	gridView = new GridView(mContext);
            
            gridView.setVerticalScrollBarEnabled(false);          

            gridView.setNumColumns(currentRow.size());
            gridView.setPadding(1,1,1,1);
            //gridView.setLayoutParams(new LayoutParams (LayoutParams.FILL_PARENT, 40));
            gridView.setGravity(Gravity.CENTER);
            gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        } else {
        	gridView = (GridView) convertView;
        }

        DataListItemAdapter itemAdapter = new DataListItemAdapter(mContext, currentRow, false);
        
        gridView.setAdapter(itemAdapter); 
        return gridView;
	}
}
