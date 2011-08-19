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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

/**
 * Display splash screen.
 */
public class Splash extends Activity {

     private final int SPLASH_DISPLAY_LENGTH = 5000;

     @Override
     public void onCreate(Bundle icicle) {
          super.onCreate(icicle);
          requestWindowFeature(Window.FEATURE_NO_TITLE);
          setContentView(R.layout.splash);
          
          new Handler().postDelayed(new Runnable(){
               public void run() {
                    Splash.this.finish();
               }
          }, SPLASH_DISPLAY_LENGTH);
     }
}
