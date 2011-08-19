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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.aquatest.dbinterface.objects.Municipality;

/**
 * Shows list of municipalities. </p>
 * 
 * Internally this class is very similar to {@link Towns}.
 * 
 * @see Towns
 */
public class Municipalities extends Activity implements OnItemClickListener{
	AquaTestApp application;
	
	Vector<Municipality> municipalities;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           
        // load the xml layout file
        setContentView(R.layout.municipalities);
        
        // create heading with municipality name
        application = (AquaTestApp) this.getApplication();

        // this line might not be needed anymore...
        municipalities = application.dbAdapter.getOrderedMunicipalities();
        
        // get the data to put into the list
        String [] colFrom = {MUNICIPALITY_NAME};
		int [] colTo = {R.id.MunicipalityItemText};
		SimpleAdapter sa = new SimpleAdapter(this, getMunicipalities(), R.layout.municipality_list_item, colFrom, colTo);
		
        ListView lv = (ListView) findViewById(R.id.MunicipalityList);
        lv.setAdapter(sa);
		lv.setOnItemClickListener(this);
    }
    
	private static final String MUNICIPALITY_NAME = "MUNICIPALITY_NAME";

	/**
	 * gets list of municipalities
	 * @return list of municipalities
	 */
	private List<Map<String, Object>> getMunicipalities()
	{
		List<Map<String, Object>> oRet = new ArrayList<Map<String, Object>>(municipalities.size());
		Map<String, Object> m;
		for (int i = 0; i < municipalities.size(); i++) {
			Municipality municipality = municipalities.elementAt(i);
			m = new HashMap<String, Object>();
			m.put(MUNICIPALITY_NAME, municipality.name);
			oRet.add(m);
		}
		
		return oRet;
	}

	/**
	 * on municipality clicked, goto municipality options
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		application.setCurrentMunicipality(municipalities.elementAt(arg2));
		Intent intent = new Intent(this, MunicipalityOptions.class);
		startActivity(intent);
	}
}
