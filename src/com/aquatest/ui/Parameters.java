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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.aquatest.dbinterface.objects.Parameter;
import com.aquatest.dbinterface.tools.DatabaseAdaptor;
import com.aquatest.dbinterface.tools.DataUtils.TableViews;


/**
 * Show list of parameters of data
 */
public class Parameters extends Activity implements OnItemClickListener{
	AquaTestApp application;
	
	Vector<Parameter> parameters;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameters);
        
        application = (AquaTestApp) this.getApplication();
        
        if (!DatabaseAdaptor.HIDE_EMPTY_DATA)
        	parameters = application.dbAdapter.getOrderedParameters();
        else parameters = application.dbAdapter.getOrderedActiveParametersByTown(application.getCurrentTown().id);
        
        String [] colFrom = {PARAMETER_NAME};
		int [] colTo = {R.id.ParameterItemText};
		SimpleAdapter sa = new SimpleAdapter(this, getParameters(), R.layout.parameter_list_item, colFrom, colTo);
		
        ListView lv = (ListView) findViewById(R.id.ParamterList);
        lv.setAdapter(sa);
		lv.setOnItemClickListener(this);
    }
    
	public static final String PARAMETER_NAME = "PARAMETER_NAME";

	/**
	 * get list of parameters
	 * @return parameters
	 */
	private List<Map<String, Object>> getParameters()
	{
		List<Map<String, Object>> oRet = new ArrayList<Map<String, Object>>(parameters.size());
		Map<String, Object> m;
		for (int i = 0; i < parameters.size(); i++) {
			Parameter parameter = parameters.elementAt(i);
			m = new HashMap<String, Object>();
			m.put(PARAMETER_NAME, parameter.testName);
			oRet.add(m);
		}
		
		return oRet;
	}

	/**
	 * When parameters selected, show single parameter view
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Uri uri = TableViews.buildUri(DatabaseAdaptor.TOWN, application.getCurrentTown().id, TableViews.SINGLE_PARAMETER_MEASURED_VALUE, parameters.elementAt(arg2).id);
		application.setCurrentParameter(parameters.elementAt(arg2));
		Intent intent = new Intent(this,DataList.class);
		intent.setData(uri);
		startActivity(intent);
	}
}
