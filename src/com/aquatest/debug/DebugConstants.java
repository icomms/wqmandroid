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
package com.aquatest.debug;

import com.aquatest.webservice.MockAquaTestWebService;

/**
 * Contains constants that relate to the debugging mode of the application
 */
public class DebugConstants {
	
	/** 
	 * If <code>true</code>, then the calendar will default to October 2010, where there is data. </p>
	 * 
	 * The java compiler optimises out any references to this boolean since it is declared
	 * as <code>final</code>.
	 */
	public static final boolean CALENDAR_DEBUG = false;
	
	/** 
	 * If <code>true</code>, then the app will be built to use the {@link MockAquaTestWebService} 
	 * class for all web service calls. </p>
	 * 
	 * The java compiler optimises out any references to this boolean since it is declared
	 * as <code>final</code>.
	 */
	public static final boolean MOCK_WEB_SERVICES = false;

	/** 
	 * If <code>true</code>, then the mock JSON responses returned by {@link MockAquaTestWebService}
	 * will be long, which can be useful for profiling the database query string routines.
	 * If <code>false</code>, then the responses are brief, which speeds up the startup routines. </p>
	 * 
	 * The java compiler optimises out any references to this boolean since it is declared
	 * as <code>final</code>.
	 */
	public static final boolean MOCK_WEB_SERVICE_LONG_RESPONSES = true;

}
