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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aquatest.dbinterface.objects.Municipality;
import com.aquatest.dbinterface.tools.DatabaseUpdater;

/**
 * Entry point activity for the application. Displays a list of municipalities.
 */
public class AquaTest extends Activity implements OnItemClickListener,
		DialogListener
{
	private static final int ACTIVITY_RESULT_SHOW_DIALOGS = 911;
	private boolean MUST_SHOW_UPDATE_DIALOGS;

	public static final int DIALOG_DEFAULT_MUNICIPALITY = 0;
	public static final int DIALOG_UPDATE_CHECK = 1;
	public static final int DIALOG_UPDATE = 2;

	public static AquaTest ACTIVE_INSTANCE;

	public AquaTestApp application;

	Vector<Municipality> municipalities;

	private static final String MUNICIPALITY_NAME = "MUNICIPALITY_NAME";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// set the flag to ask about updating the db in this method
		MUST_SHOW_UPDATE_DIALOGS = (savedInstanceState == null);

		application = (AquaTestApp) this.getApplication();

		/*
		 * setContentView(R.layout.main); application = (AquaTestApp)
		 * this.getApplication();
		 * 
		 * ListView lv = (ListView) findViewById(R.id.HomeScreenList);
		 * lv.setOnItemClickListener(this);
		 */

		// first screen to show is municipalities
		setContentView(R.layout.municipalities);

		// display the municipalities
		municipalities = application.dbAdapter.getOrderedMunicipalities();
		String[] colFrom = { MUNICIPALITY_NAME };
		int[] colTo = { R.id.MunicipalityItemText };

		SimpleAdapter sa = new SimpleAdapter(this, getMunicipalities(),
				R.layout.municipality_list_item, colFrom, colTo);

		ListView lv = (ListView) findViewById(R.id.MunicipalityList);
		lv.setAdapter(sa);
		lv.setOnItemClickListener(this);

		if (municipalities.size() == 0)
			((TextView) findViewById(R.id.welcome_text))
					.setText("No data available. Try updating data from the menu options.");

		ACTIVE_INSTANCE = this;

		// show splash screen - sends current Activity to background
		if (application.loadSplash)
		{
			Intent intent = new Intent(this, Splash.class);

			// start the activity with a callback
			startActivityForResult(intent, ACTIVITY_RESULT_SHOW_DIALOGS);

			/*
			 * Note: there is a callback after the splash screen which will call
			 * the showUpdateDialogs() method. This is to make sure this
			 * activity is active when the dialogs are shown. In the case that
			 * no splash screen is displayed, we call that method explicitly as
			 * below.
			 */

			application.loadSplash = false;
		}
		else
		{
			// call this explicitly
			showUpdateDialogs();
		}

	}

	/**
	 * Workaround method to show the update dialogs after the splash screen.
	 */
	private void showUpdateDialogs()
	{
		// check for updates to the database
		if (MUST_SHOW_UPDATE_DIALOGS)
		{
			long lastUpdateTime = application.getLastUpdate();

			if (lastUpdateTime == 0)
			{
				application.updateThread = new DatabaseUpdater(lastUpdateTime,
						application.dbAdapter);

				showDialogSmart(DIALOG_UPDATE);

				application.updateThread.start();
			}
			else if ((System.currentTimeMillis() - lastUpdateTime) >= DatabaseUpdater.UPDATE_CHECK_PERIOD)
			{
				showDialogSmart(DIALOG_UPDATE_CHECK);
			}
		}
	}


	/**
	 * Reset the activity view according to any updated data.
	 */
	public void resetView()
	{
		municipalities = application.dbAdapter.getOrderedMunicipalities();
		String[] colFrom = { MUNICIPALITY_NAME };
		int[] colTo = { R.id.MunicipalityItemText };

		SimpleAdapter sa = new SimpleAdapter(this, getMunicipalities(),
				R.layout.municipality_list_item, colFrom, colTo);

		ListView lv = (ListView) findViewById(R.id.MunicipalityList);
		lv.setAdapter(sa);
		lv.setOnItemClickListener(this);

		if (municipalities.size() > 0)
			((TextView) findViewById(R.id.welcome_text))
					.setText("Select municipality:");
		else
			((TextView) findViewById(R.id.welcome_text))
					.setText("No data available. Try updating data from the menu options.");
	}


	/**
	 * Get a list of the municipalities to display.
	 * 
	 * @return map of municipalities
	 */
	private List<Map<String, Object>> getMunicipalities()
	{
		List<Map<String, Object>> oRet = new ArrayList<Map<String, Object>>(
				municipalities.size());
		Map<String, Object> m;
		for (int i = 0; i < municipalities.size(); i++)
		{
			Municipality municipality = municipalities.elementAt(i);
			m = new HashMap<String, Object>();
			m.put(MUNICIPALITY_NAME, municipality.name);
			oRet.add(m);
		}

		return oRet;
	}

	/**
	 * Called when a municipality is clicked
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		application.setCurrentMunicipality(municipalities.elementAt(arg2));
		Intent intent = new Intent(this, MunicipalityOptions.class);
		startActivity(intent);
	}

	/**
	 * Called on creation of options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Called on preparation of options menu.
	 */
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();
		boolean bRet = super.onCreateOptionsMenu(menu);
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu, menu);

		menu.getItem(0).setVisible(false);

		return bRet;
	}

	/**
	 * Called on selection of an options menu item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		// Log.v("leo","options selected");
		// Log.v("Steve", item.getItemId() + "");
		switch (item.getItemId())
		{
			case R.id.menu_set_default_municipality:
				// Log.v("leo","menu_set_default_municipality");
				// showSetDefaultMunicipality();
				showDialogSmart(DIALOG_DEFAULT_MUNICIPALITY);
				break;
			case R.id.menu_update_data:
				// Log.v("steve","menu_update_data");
				// showCheckForUpdate();
				showDialogSmart(DIALOG_UPDATE_CHECK);
				break;
			case R.id.menu_clear_database:
				// Log.v("steve","menu_clear_data");

				application.dbAdapter.clearDatabase();
				application.dbAdapter.reset();
				resetView();

				Toast.makeText(this.getApplicationContext(),
						"Database Cleared!", Toast.LENGTH_LONG).show();

				SharedPreferences settingsPref = application
						.getSharedPreferences(AquaTestApp.PREF, 0);
				settingsPref.edit()
						.putLong(AquaTestApp.PREF_LAST_UPDATE_TIME, 0).commit();
				break;
		}
		return true;

	}


	/**
	 * Show a dialog with a singleton instance of this activity as the parent.
	 * 
	 * @param id
	 *            the id of the dialog to show
	 */
	public void showDialogSmart(int id)
	{
		if (ACTIVE_INSTANCE != null)
			ACTIVE_INSTANCE.showDialog(id);
	}

	/**
	 * Dismiss a dialog with a singleton instance of this activity as the
	 * parent.
	 * 
	 * @param id
	 *            the id of the dialog to show
	 */
	public void dismissDialogSmart(int id)
	{
		if (ACTIVE_INSTANCE != null)
			ACTIVE_INSTANCE.dismissDialog(id);
	}


	/**
	 * Called on creation of a dialog box.
	 * 
	 * @param id
	 *            the id of the dialog to create.
	 */
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case DIALOG_DEFAULT_MUNICIPALITY:
				Dialog dl = new Dialog(this);
				DefaultMunicipalityDialog dvd = new DefaultMunicipalityDialog(
						this);
				dvd.setMunicipalities(
						application.dbAdapter.getOrderedMunicipalities(),
						application.getDefaultMunicipality(), dl, this);
				dl.setContentView(dvd, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				dl.setTitle("Default Municipality");
				return dl;

			case DIALOG_UPDATE_CHECK:
				DataUpdateCheckDialog ducd = new DataUpdateCheckDialog(this);
				ducd.setTitle("Update Data");
				return ducd;

			case DIALOG_UPDATE:
				DataUpdateDialog dud = new DataUpdateDialog(this,
						application.dbAdapter, application.updateThread/* dBUpdater */);
				dud.setTitle("Updating...");
				application.updateThread.setHandler(dud.messageHandler);
				return dud;

			default:
				return super.onCreateDialog(id);
		}
	}


	/**
	 * Close the specified dialog.
	 * 
	 * @param dialog
	 *            the dialog to close
	 */
	// @Override
	public void closeDialog(Dialog dialog)
	{
		dialog.dismiss();
	}

	/**
	 * Called on destruction of this activity.
	 */
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ACTIVE_INSTANCE = null;
	}

	/**
	 * Callback method for the splash screen. It is used to launch the update
	 * dialogs.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		// if the splash screen has just closed, show the update dialogs
		if (requestCode == ACTIVITY_RESULT_SHOW_DIALOGS)
		{
			showUpdateDialogs();
		}
	}
}