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


//import java.util.Calendar;

//import com.aquatest.dbinterface.tools.DatabaseAdaptor;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.aquatest.dbinterface.tools.DatabaseUpdater;

/**
 * Dialog box to ask the user if they would like to update the data.
 */
public class DataUpdateCheckDialog extends Dialog implements OnClickListener {
	
	private Context mContext;
	private Button okButton;
	private Button cancelButton;
	
	/**
	 * Constructor
	 */
	public DataUpdateCheckDialog(Context context) {
		super(context);
		this.mContext = context;
		
		init();
	}

	/**
	 * Initialisation
	 */
	private void init() {
		View v = LayoutInflater.from(mContext).inflate(R.layout.data_update_check_dialog, null, true);
		this.setContentView(v, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		// get last update time from user preferences
		SharedPreferences settingsPref = getContext().getSharedPreferences(AquaTestApp.PREF, 0);
		
		long lastUpdate = settingsPref.getLong(AquaTestApp.PREF_LAST_UPDATE_TIME, 
				DatabaseUpdater.DEFAULT_LAST_UPDATE);
		
		// check if user wants to update
		Time t = new Time();
		t.set(lastUpdate);
		
		String format = ("%Y/%m/%d %H:%M");
		TextView tV = (TextView) v.findViewById(R.id.dialog_text);
		tV.setText("Data has not been updated since " + t.format(format) + ". Would you like to update now?");
		
		okButton = (Button) v.findViewById(R.id.OKButton);
		cancelButton = (Button) v.findViewById(R.id.CancelButton);

		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}
	

	/**
	 * Called on a button click.
	 */
	public void onClick(View v) {
		if (v == okButton) {
			
			try {	
				AquaTest activity = (AquaTest) this.getOwnerActivity();
				if (activity.application.updateThread.isAlive())
					activity.application.updateThread.interrupt();
				
				activity.application.updateThread = new DatabaseUpdater(activity.application.getLastUpdate(), activity.application.dbAdapter);
				
				AquaTest.ACTIVE_INSTANCE.showDialogSmart(2);

				activity.application.updateThread.start();
				
			} catch (Exception ex) {
				// Log.v("leo","exception");
				ex.printStackTrace();
			}

			AquaTest.ACTIVE_INSTANCE.removeDialog(AquaTest.DIALOG_UPDATE_CHECK);
	        
		} else {
			// Log.v("button pressed","cancel");
			
			AquaTest.ACTIVE_INSTANCE.removeDialog(AquaTest.DIALOG_UPDATE_CHECK);
		}

	}

}