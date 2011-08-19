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

import com.aquatest.dbinterface.tools.DatabaseAdaptor;
import com.aquatest.dbinterface.tools.DatabaseUpdater;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Dialog box displayed when updating data.
 */
public class DataUpdateDialog extends Dialog implements OnClickListener {
	
	private Context mContext;
	private Button updatingButton;
	private TextView tV;
	private ProgressBar pB;
	private DatabaseUpdater thread;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param _dbAdaptor DatabaseAdaptor to interact with the database
	 * @param _thread the update thread
	 */
	public DataUpdateDialog(Context context, DatabaseAdaptor _dbAdaptor, DatabaseUpdater _thread) {
		super(context);
		this.mContext = context;
		init();
		thread = _thread;
	}

	
	/**
	 * Initialisation
	 */
	private void init() {
		View v = LayoutInflater.from(mContext).inflate(R.layout.data_updating_dialog, null, true);
		this.setContentView(v, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		tV = (TextView) v.findViewById(R.id.dialog_text);
		tV.setText("Updating...");
		
		updatingButton = (Button) v.findViewById(R.id.updating_button);
		updatingButton.setOnClickListener(this);	
		
		pB = (ProgressBar) v.findViewById(R.id.dialog_progressbar);
	}
	

	/**
	 * Called when a button on the dialog is clicked.
	 */
	public void onClick(View v) {
		if (v == updatingButton) {
			
			if (((Button) v).getText().toString().compareTo("Cancel") == 0) {
				thread.interrupt();
				
				
				
				tV.setText("Cancelling update process. This may take a few minutes, please wait while the application completes receiving the most recent response" +
						" from the update server.");
				
				//android.os.Thread..killProcess(thread.pID);
			}
			else {
				AquaTest.ACTIVE_INSTANCE.removeDialog(AquaTest.DIALOG_UPDATE);
			}
		}
	}
	
	/**
	 * Called on key press. Used to handle back key press.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		else
			return false;	
	}
	
	

	/**
	 * Update dialog box to display error message.
	 * 
	 * @param _message message to display
	 */
	public void error(String _message) {
		updatingButton.setVisibility(Button.VISIBLE);
		tV.setText(_message);
		updatingButton.setText("Ok");
		pB.setVisibility(ProgressBar.GONE);
	}

	/**
	 * Update dialog box to display an item processed message.
	 * 
	 * @param _message message to display
	 */
	public void itemProcessed(String _message) {
		tV.setText(_message);
	}
	
	/**
	 * Hide the cancel button.
	*/
	public void hideCancelButton() {
		updatingButton.setVisibility(Button.GONE);
	}
	
	/**
	 * Update dialog box to display a row processed message.
	 * 
	 * @param _message message to display
	 */
	public void rowProcessed(String _message) {
	
		String oldText = tV.getText().toString();
		int i = oldText.indexOf("[");

		if (i != -1)
			tV.setText(oldText.substring(0, i-1) + " " + _message);
		else tV.setText(oldText + " " + _message);
	}

	/**
	 * Update dialog box to display an update complete message.
	 * 
	 * @param _message message to display
	 * @param _updateTime time that the update took place.
	 */
	public void updateComplete(String _message, long _updateTime) {
		updatingButton.setVisibility(Button.VISIBLE);
		
		SharedPreferences settingsPref = this.mContext.getSharedPreferences(AquaTestApp.PREF, 0);
		settingsPref.edit()
			.putLong(AquaTestApp.PREF_LAST_UPDATE_TIME, _updateTime)
			.commit();
				
		tV.setText(_message);
		updatingButton.setText("Ok");
		pB.setVisibility(ProgressBar.GONE);
		
		 AquaTest.ACTIVE_INSTANCE.application.dbAdapter.reset();
		 AquaTest.ACTIVE_INSTANCE.resetView();
	}
	
	/**
	 * Update dialog box to display an update cancelled message.
	 * 
	 * @param _message message to display
	 */
	public void updateCancelled(String _message) {			
		tV.setText(_message);
		updatingButton.setText("Ok");
		pB.setVisibility(ProgressBar.GONE);
	}
	
	
	/**
	 * Handler to listen for messages from the update thread.
	 */
	public volatile Handler messageHandler = new Handler() {

		public final int ERROR = 0;
		public final int COMPLETE = 1;
		public final int ITEM_COMPLETE = 2;
		public final int ROW_COMPLETE = 3;
		public final int CANCELLED = 4;
		public final int QUERY_PROCESSING = 5;
		
		/**
		 * Handle a message from the update thread.
		 */
		@Override
      	public void handleMessage(Message msg) {  
    	  	super.handleMessage(msg);
    	  
    	  	Bundle b = msg.getData();
    	  	String msgString = b.getString("msg");
    	  	long updateTime = b.getLong("time");
    	  	
    	  	switch(msg.what) {
    	  		case ERROR : 
    	  			DataUpdateDialog.this.error(msgString);
    	  			break;
    	  		case COMPLETE : 
    	  			DataUpdateDialog.this.updateComplete(msgString, updateTime);
    	  			break;
    	  		case ITEM_COMPLETE : 
    	  			DataUpdateDialog.this.hideCancelButton();
    	  			DataUpdateDialog.this.itemProcessed(msgString);
    	  			break;
    	  		case ROW_COMPLETE : 
    	  			DataUpdateDialog.this.rowProcessed(msgString);
    	  			break;
    	  		case CANCELLED : 
    	  			DataUpdateDialog.this.updateCancelled(msgString);
    	  			break;
    	  		case QUERY_PROCESSING :
    	  			DataUpdateDialog.this.hideCancelButton();
    	  			DataUpdateDialog.this.itemProcessed(msgString);
    	  			
    	  		default : 
    	  			break;
    	  	}
      	}
	};
}

