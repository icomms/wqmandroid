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

import java.util.Calendar;
import java.util.HashMap;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.aquatest.configuration.ConfigFile;
import com.aquatest.dbinterface.objects.Municipality;
import com.aquatest.dbinterface.objects.Parameter;
import com.aquatest.dbinterface.objects.Town;
import com.aquatest.dbinterface.tools.DatabaseAdaptor;
import com.aquatest.dbinterface.tools.DatabaseUpdater;
import com.aquatest.webservice.AquaTestWebService;

/**
 * Application class
 */
public class AquaTestApp extends Application
{
	public final static String PREF = "aquatest_pref";
	public final static String PREF_DEFAULT_MUNICIPALITY = "aquatest_pref_default_municipality";
	public final static String PREF_DATA_UPDATE_INTERVAL = "aquatest_pref_data_update_interval";
	public final static String PREF_LAST_UPDATE_TIME = "aquatest_pref_data_update_interval";


	public DatabaseAdaptor dbAdapter;
	private Municipality currentMunicipality;
	private Town currentTown;
	private Parameter currentParameter;
	private Calendar dayDate;
	public boolean loadSplash = true;
	public DatabaseUpdater updateThread;

	/**
	 * Global settings as defined in the configuration file
	 * /assets/config/config.txt.
	 */
	public ConfigFile config;


	/**
	 * Called on application created
	 */
	public void onCreate()
	{
		// initialise configuration settings
		this.config = new ConfigFile(this);

		// add config settings to the database adapter
		DatabaseAdaptor.config = this.config;
		AquaTestWebService.config = this.config;
		
		dbAdapter = new DatabaseAdaptor(this);
		updateThread = new DatabaseUpdater(getLastUpdate(), dbAdapter);
	}


	/**
	 * Called on application terminated
	 */
	public void onTerminate()
	{
	}

	/**
	 * Get the current municipality that is being viewed in the application.
	 */
	public Municipality getCurrentMunicipality()
	{
		return currentMunicipality;
	}

	/**
	 * Set the current municipality that is being viewed in the application.
	 */
	public void setCurrentMunicipality(Municipality municipality)
	{
		this.currentMunicipality = municipality;
	}

	/**
	 * Set the current town that is being viewed in the application.
	 */
	public void setCurrentTown(Town town)
	{
		this.currentTown = town;
	}

	/**
	 * Get the current town that is being viewed in the application.
	 */
	public Town getCurrentTown()
	{
		return currentTown;
	}


	/**
	 * Get the default municipality that is saved in the application's
	 * preferences.
	 */
	public Municipality getDefaultMunicipality()
	{
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		int municipalityId = settings.getInt(PREF_DEFAULT_MUNICIPALITY, -1);
		if (municipalityId == -1) { return null; }
		HashMap<Integer, Municipality> municipalities = dbAdapter.municipalities;
		return municipalities.get(municipalityId);
	}


	/**
	 * Get the preferred data update interval that is saved in the application's
	 * preferences.
	 */
	public int getSavedInterval()
	{
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		int interval = settings.getInt(PREF_DATA_UPDATE_INTERVAL, 0);
		return interval;
	}

	/**
	 * Get the time of the last data update.
	 * 
	 * @return time of last update in milliseconds
	 */
	public long getLastUpdate()
	{
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		// settings.edit().clear().commit();

		long lastUpdate = settings.getLong(PREF_LAST_UPDATE_TIME,
				DatabaseUpdater.DEFAULT_LAST_UPDATE);
		return lastUpdate;
	}

	/**
	 * Get the current parameter that is being viewed in the application.
	 */
	public void setCurrentParameter(Parameter parameter)
	{
		this.currentParameter = parameter;
	}

	/**
	 * Get the current parameter that is being viewed in the application.
	 */
	public Parameter getCurrentParameter()
	{
		return currentParameter;
	}

	/**
	 * Set the current date that is being viewed in the application.
	 */
	public void setdayDate(Calendar date)
	{
		this.dayDate = date;
	}

	/**
	 * Get the current date that is being viewed in the application.
	 */
	public Calendar getDate()
	{
		return dayDate;
	}
}
