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
package com.aquatest.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * Represents a configuration file that can be added to the project to set
 * certain preferences. </p>
 * 
 * A default configuration file is stored in the project's /assets/config/
 * folder. This can be updated after the app is built by using the Android SDK
 * aapt tool. By doing so, different builds of the app can be set up with
 * different preferences e.g. municipalities that can be viewed. </p>
 * 
 * Initial design just allows for the file to contain one integer value. This
 * value will represent the only municipality that can be displayed by the app.
 * Later implementations can build on this to add more complex functionality.
 */
public class ConfigFile
{
	public static final String CONFIG_FILE_FULL_NAME = "config/config.txt";

	/**
	 * key in properties hashtable for an allowed municipality id
	 */
	private static final String KEY_ALLOWED_MUNICIPALITY = "KEY_ALLOWED_MUNICIPALITY";

	/**
	 * application that created this instance
	 */
	private Application application;

	/** configuration preferences */
	private Hashtable<String, String> properties;

	/**
	 * set to <code>true</code> at end of {@link #init()} method
	 */
	private static boolean initialised = false;

	/**
	 * Private constructor to hide default constructor - use
	 */
	private ConfigFile()
	{
		super();
	}

	/**
	 * Creates a new {@link ConfigFile} instance linked to a given application
	 * context.
	 * 
	 * @param application
	 *            {@link Application} that will be used as the context to
	 *            retrieve an {@link AssetManager} which can open the actual
	 *            file.
	 */
	public ConfigFile(Application application)
	{
		this();
		this.application = application;
	}

	/**
	 * Reads the configuration file into memory. Discards any previous
	 * configuration properties that may have been set.
	 * 
	 * @return
	 */
	public String init()
	{
		// retrieve all assets into the AssetManager
		AssetManager assetManager = application.getAssets();

		// open the text file
		InputStream inputStream = null;
		try
		{
			inputStream = assetManager.open(CONFIG_FILE_FULL_NAME);
		}
		catch (IOException e)
		{
			// Log.e("DEBUG", e.getMessage());
		}

		String s = readTextFile(inputStream);
		properties = parsePropertiesText(s);

		// set the flag
		initialised = true;

		return s;
	}

	/**
	 * This method reads simple text input stream / file.
	 * 
	 * @param inputStream
	 * @return data from file, or <code>null</code> if inputStream is
	 *         <code>null</code>
	 */
	private String readTextFile(InputStream inputStream)
	{
		// escape condition
		if (inputStream == null)
			return null;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try
		{
			while ((len = inputStream.read(buf)) != -1)
			{
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		}
		catch (IOException e)
		{
		}
		return outputStream.toString();
	}

	/**
	 * Parses some text to extract the properties that have been set.
	 * 
	 * @param text
	 *            Text to parse. In this implementation the text is expected to
	 *            contain only one integer, which is parsed and stored as the
	 *            one municipality that the user will be allowed to view.
	 * @return A table containing all the properties that have been found in the
	 *         text.
	 */
	private Hashtable<String, String> parsePropertiesText(String text)
	{
		// currently there is only 1 allowed property
		Hashtable<String, String> props = new Hashtable<String, String>(1);

		// try to parse the text as an integer
		Integer allowedMunicipalityId;
		try
		{
			// parse the text
			allowedMunicipalityId = Integer.valueOf(text);

		}
		catch (Throwable t)
		{
			// set value of property to -1
			allowedMunicipalityId = -1;
		}

		// store the id
		props.put(KEY_ALLOWED_MUNICIPALITY, allowedMunicipalityId.toString());

		return props;
	}

	/**
	 * Determines if this config file limits the user to one municipality.
	 * 
	 * @return <code>true</code> if user is limited to one municipality,
	 *         <code>false</code> otherwise.
	 */
	public boolean isLimitedToOneMunicipality()
	{
		return (!"-1".toString().equals(
				properties.get(KEY_ALLOWED_MUNICIPALITY)));
	}

	/**
	 * Returns the municipality ID that the application has been configured for.
	 * 
	 * @return If the user is limited to one municipality, this method returns
	 *         an integer ID in the wqmauthority table in the database, of the
	 *         allowed municipality. Otherwise this method returns -1.
	 */
	public int getAllowedMunicipalityId()
	{
		int allowedMunicipalityId;
		String idString = properties.get(KEY_ALLOWED_MUNICIPALITY);

		if (idString == null)
		{
			allowedMunicipalityId = -1;
		}
		else
		{
			// try to parse the text as an integer
			try
			{
				// parse the text
				allowedMunicipalityId = Integer.valueOf(idString).intValue();
			}
			catch (Throwable t)
			{
				// set value of property to -1
				allowedMunicipalityId = -1;
			}
		}

		return allowedMunicipalityId;
	}

	public boolean isInitialised()
	{
		return initialised;
	}
}
