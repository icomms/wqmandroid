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

import java.util.Vector;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;

import com.aquatest.dbinterface.objects.Municipality;

/**
 * DefaultMunicipalityDialog
 * 
 * Dialog to allow user to select default Municipality
 */
public class DefaultMunicipalityDialog extends ScrollView implements OnClickListener, OnCheckedChangeListener{
	
	private Context mContext;
	private Button okButton;
	private Button cancelButton;
	private Vector<Municipality> municipalities;
	private RadioGroup rg;
	private DialogListener dListener;
	private Dialog dialog;
	
	public DefaultMunicipalityDialog(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public DefaultMunicipalityDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	/**
	 * Initialisations
	 */
	private void init() {
		View v = LayoutInflater.from(mContext).inflate(R.layout.default_municipality_dialog, null, true);
		this.addView(v, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		okButton = (Button) v.findViewById(R.id.OKButton);
		cancelButton = (Button) v.findViewById(R.id.CancelButton);
		rg = (RadioGroup) v.findViewById(R.id.dv_radio_group);
		rg.setOnCheckedChangeListener(this);
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);	
	}
	
	
	/**
	 * Set list of municipalities
	 * @param municipalities list of municipalities
	 * @param defaultMunicipality stored default Municipality
	 * @param dialog Dialog object
	 * @param dListener DialogListener object 
	 */
	public void setMunicipalities(Vector<Municipality> municipalities,Municipality defaultMunicipality,Dialog dialog, DialogListener dListener) {
		this.municipalities = municipalities;
		this.dListener = dListener;
		this.dialog = dialog;
		

		for (int i = 0; i < municipalities.size(); i++) {
			RadioButton rb = new RadioButton(mContext);
			Municipality mp = municipalities.elementAt(i);
			rb.setText(mp.name);
			rg.addView(rb);
			if (defaultMunicipality != null) {
				if (mp.id == defaultMunicipality.id) {
					rb.setChecked(true);
				}
			}
		}
		
		this.invalidate();
	}
	
	
	/**
	 * onClick handler of ok, cancel buttons
	 */
	public void onClick(View v) {
		if (v == okButton) {
			// Log.v("button presed","ok");
			int rbid = rg.getCheckedRadioButtonId();
			if (rbid != -1) {
				RadioButton checkButton = (RadioButton) this.findViewById(rbid);
				int index = rg.indexOfChild(checkButton);
				SharedPreferences settingsPref = getContext().getSharedPreferences(AquaTestApp.PREF, 0);
				settingsPref.edit()
					.putInt(AquaTestApp.PREF_DEFAULT_MUNICIPALITY,municipalities.elementAt(index).id)
					.commit();
			}
		} else {
			// Log.v("button presed","cancel");
		}
		dListener.closeDialog(dialog);
	}

	/**
	 * handler of radio buttons changed
	 */
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//RadioButton rb = (RadioButton) this.findViewById(checkedId);
		//rb.setChecked(true);
	}


}
