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
package com.aquatest.dbinterface.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Class to create a new database if database doesn't exist and to copy correct schema to database; otherwise open existing database.
 */
public class DatabaseProvider extends SQLiteOpenHelper {

	// to copy this file from a rooted device, use command:
	//	adb pull /data/data/com.aquatest.ui/databases/aquatest.db
	/** Path on device to aquatest.db database. For test purposes, it can help to use /sdcard/... instead. */
	private static final String DB_PATH = "/data/data/com.aquatest.ui/databases/";
	private static final String DB_NAME = "aquatest.db";

	// this is done for memory optimisation
	private static final String DB_FULL_PATH_AND_NAME = DB_PATH + DB_NAME;

	private SQLiteDatabase database;
	private final Context context;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public DatabaseProvider(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	
	/**
	 * Create an empty database.
	 * 
	 * @throws IOException
	 */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created
			// into the default system path
			// of your application so we are able to overwrite that
			// database with our database.
			this.getWritableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH_AND_NAME, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transferring byte stream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = context.getAssets().open(DB_NAME);

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(DB_FULL_PATH_AND_NAME);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}
	
	
	/**
	 * Open existing database.
	 * 
	 * @return opened database
	 * @throws SQLException
	 */
	public SQLiteDatabase openDataBase() throws SQLException {

		// Open the database
		database = SQLiteDatabase.openDatabase(DB_FULL_PATH_AND_NAME, null,
				SQLiteDatabase.OPEN_READWRITE);
		return database;
		
	}

	@Override
	public synchronized void close() {

		if (database != null)
			database.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}