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

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

/**
 * {@link SimpleAdapter} subclass which can set different rows to different
 * colours.
 */
public class ColoredRowsAdapter extends SimpleAdapter
{
	/** stores the colour for each row */
	private int[] colorForRow;

	/**
	 * Creates a {@link ColoredRowsAdapter} with each row's colours defined by
	 * the <code>colorForRow</code> array.
	 * 
	 * @param context
	 *        Current context
	 * @param data
	 *        Data to show in the view
	 * @param colorForRow
	 *        Array containing colours for each row. The index into this array
	 *        is calculated as the remainder after the row index is divided by
	 *        the length of this array. i.e. the colours wrap around if there
	 *        are fewer colours than rows. If no colours are provided then a
	 *        default colour will be used for all rows.
	 * @param resource
	 *        xml file reference
	 * @param from
	 *        columns to map from
	 * @param to
	 *        columns to map to in an xml file
	 */
	public ColoredRowsAdapter(Context context,
		List<? extends Map<String, ?>> data, int[] colorForRow, int resource,
		String[] from, int[] to)
	{
		super(context, data, resource, from, to);

		// set row colours if they have been provided, else use defaults
		if ((colorForRow == null) || (colorForRow.length == 0))
		{
			this.colorForRow = new int[] { 0x60FF0000, 0x6000FF00 };
		}
		else
		{
			this.colorForRow = colorForRow;
		}
	}

	/**
	 * Returns the view of the current row. All this method does is add colour
	 * to the default {@link SimpleAdapter} implementation.
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		int colorPos = position % this.colorForRow.length;
		view.setBackgroundColor(this.colorForRow[colorPos]);
		
		return view;
	}


}
