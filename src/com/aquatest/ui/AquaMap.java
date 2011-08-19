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
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aquatest.dbinterface.objects.Municipality;
import com.aquatest.dbinterface.objects.SamplingPoint;
import com.aquatest.dbinterface.objects.Town;
import com.aquatest.dbinterface.tools.DataCache;
import com.aquatest.dbinterface.tools.DataIndicator;
import com.aquatest.dbinterface.tools.DataUtils;
import com.aquatest.dbinterface.tools.DataUtils.TestIndicators;
import com.aquatest.dbinterface.tools.DatabaseAdaptor;
import com.aquatest.map.MapUtils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Activity to manage the display of the map.
 */
public class AquaMap extends MapActivity implements MapListener
{
	/**
	 * This is the number of days back to look for sample data when plotting
	 * the colour of the pointers on the map.
	 */
	// TODO this should be a global constant
	public static final int OVERVIEW_PERIOD_DAYS = 30;
	
	/** default zoom factor when a municipality is selected */
	private static final int ZOOM_DEFAULT_MUNICIPALITY = 9;
	/** default zoom factor when a town is selected */
	private static final int ZOOM_DEFAULT_TOWN = 15;

	// provide the default boundaries for the map window, in degrees, if no
	// coordinates are provided otherwise
	private static final double DEFAULT_MAP_WINDOW_TOP = -15.0;
	private static final double DEFAULT_MAP_WINDOW_LEFT = 11.0;
	private static final double DEFAULT_MAP_WINDOW_BOTTOM = -35.0;
	private static final double DEFAULT_MAP_WINDOW_RIGHT = 39.0;

	AquaTestApp application;

	LinearLayout linearLayout;
	private MapView mapView;
	private List<Overlay> mapOverlays;

	private Drawable drawableSamplePointFailed;
	private Drawable drawableSamplePointWarning;
	private Drawable drawableSamplePointPass;
	private Drawable drawableSamplePointNone;

	private Drawable drawableTownFailed;
	private Drawable drawableTownWarning;
	private Drawable drawableTownPass;
	private Drawable drawableTownNone;

	private AreaItemizedOverlay tItemizedOverlayFailed;
	private AreaItemizedOverlay tItemizedOverlayWarning;
	private AreaItemizedOverlay tItemizedOverlayPassed;
	private AreaItemizedOverlay tItemizedOverlayNone;

	private AreaItemizedOverlay sItemizedOverlayFailed;
	private AreaItemizedOverlay sItemizedOverlayWarning;
	private AreaItemizedOverlay sItemizedOverlayPassed;
	private AreaItemizedOverlay sItemizedOverlayNone;

	private int zoomLevel;

	private MapController controller;

	private Municipality municipality;
	private Town town;

	/** contains all the sampling points on the map */
	private DataCache dataForWholeMap = null;
	/** contains just the sampling points for the selected town */
	private DataCache dataForTown = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		application = (AquaTestApp) this.getApplication();


		/* INITIALISE THE MAP SCREEN */

		setContentView(R.layout.map);

		// create the map view
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		// view satellite data
		mapView.setSatellite(true);

		controller = mapView.getController();

		mapOverlays = mapView.getOverlays();

		Context c = this.getApplicationContext();

		// get the images that will be used by the overlaid images of towns &
		// samples
		drawableSamplePointFailed = c.getResources().getDrawable(
			R.drawable.map_failed);
		drawableSamplePointWarning = c.getResources().getDrawable(
			R.drawable.map_warning);
		drawableSamplePointPass = c.getResources().getDrawable(
			R.drawable.map_pass);
		drawableSamplePointNone = c.getResources().getDrawable(
			R.drawable.map_none);

		drawableTownFailed = c.getResources().getDrawable(
			R.drawable.town_failed);
		drawableTownWarning = c.getResources().getDrawable(
			R.drawable.town_warning);
		drawableTownPass = c.getResources().getDrawable(R.drawable.town_pass);
		drawableTownNone = c.getResources().getDrawable(R.drawable.town_none);

		// create the town layer & set the listener
		tItemizedOverlayFailed = new AreaItemizedOverlay(c, drawableTownFailed,
			true);
		tItemizedOverlayWarning = new AreaItemizedOverlay(c,
			drawableTownWarning, true);
		tItemizedOverlayPassed = new AreaItemizedOverlay(c, drawableTownPass,
			true);
		tItemizedOverlayNone = new AreaItemizedOverlay(c, drawableTownNone,
			true);

		tItemizedOverlayNone.setMapListener(this);
		tItemizedOverlayFailed.setMapListener(this);
		tItemizedOverlayWarning.setMapListener(this);
		tItemizedOverlayPassed.setMapListener(this);

		// create the sample overlay & set the listener
		sItemizedOverlayFailed = new AreaItemizedOverlay(c,
			drawableSamplePointFailed, false);
		sItemizedOverlayWarning = new AreaItemizedOverlay(c,
			drawableSamplePointWarning, false);
		sItemizedOverlayPassed = new AreaItemizedOverlay(c,
			drawableSamplePointPass, false);
		sItemizedOverlayNone = new AreaItemizedOverlay(c,
			drawableSamplePointNone, false);

		sItemizedOverlayNone.setMapListener(this);
		sItemizedOverlayFailed.setMapListener(this);
		sItemizedOverlayWarning.setMapListener(this);
		sItemizedOverlayPassed.setMapListener(this);


		/* INITIALISE THE DATA */

		Calendar endDate = Calendar.getInstance();
		Calendar startDate = DataUtils.calendarSubtractDays(endDate,
			OVERVIEW_PERIOD_DAYS);
		
		// municipality & town are set before this screen is shown
		municipality = application.getCurrentMunicipality();
		town = application.getCurrentTown();

		// fetch the data before calculating map centre points
		dataForWholeMap = new DataCache(application.dbAdapter, municipality.id,
			DatabaseAdaptor.MUNICIPALITY, startDate, endDate);

		if (town != null)
		{
			dataForTown = new DataCache(application.dbAdapter, town.id,
				DatabaseAdaptor.TOWN, startDate, endDate);
		}

		// add sampling points to the map
		// TODO data has not been loaded into samples yet, so points are not
		// coloured correctly
		addSamplingPointsOverlay(dataForWholeMap);

		// calculate map centre points
		GeoPoint centrePointOfDataForWholeMap = calculateCentreOfMap(dataForWholeMap);
		GeoPoint centrePointOfDataForTown = null;
		if (dataForTown != null)
		{
			centrePointOfDataForTown = calculateCentreOfMap(dataForTown);
		}

		// TODO this centre point is never used - calculation can be removed
		GeoPoint centrePointOfAllTowns = addTownsOverlayAndCentreMap(startDate,
			endDate);


		/* UPDATE THE LAYERS WITH THE DATA */

		/*
		 * Data has been added to the layers in terms of overlays. The actual
		 * layers now need to be populated in order to turn those data
		 * representations into visible pictures.
		 */
		// add the town & sample overlaid images to the layers
		tItemizedOverlayFailed.Populate();
		tItemizedOverlayWarning.Populate();
		tItemizedOverlayPassed.Populate();
		tItemizedOverlayNone.Populate();
		sItemizedOverlayFailed.Populate();
		sItemizedOverlayWarning.Populate();
		sItemizedOverlayPassed.Populate();
		sItemizedOverlayNone.Populate();

		/* CHECK FOR PREVIOUS STATE */

		// restore previous map state if any
		if (savedInstanceState != null)
		{
			// Log.v("Steve", "****RESTORING STATE*****");

			// restore centre point & zoom
			if (savedInstanceState.containsKey("centre"))
			{
				controller.setZoom(savedInstanceState.getInt("zoom"));
			}

			// restore latitude & longitude
			if ((savedInstanceState.containsKey("latitude"))
				&& (savedInstanceState.containsKey("longitude")))
			{
				controller
					.setCenter(new GeoPoint(savedInstanceState
						.getInt("latitude"), savedInstanceState
						.getInt("longitude")));
			}

			// restore title
			if (savedInstanceState.containsKey("title"))
			{
				this.setTitle(savedInstanceState.getString("title"));
			}

			// different items get displayed depending on the zoom level
			if (mapView.getZoomLevel() > 9)
			{
				// only show samples at this zoom level
				mapOverlays.clear();

				mapOverlays.add(sItemizedOverlayFailed);
				mapOverlays.add(sItemizedOverlayWarning);
				mapOverlays.add(sItemizedOverlayPassed);
				mapOverlays.add(sItemizedOverlayNone);

				// show sample labels if zoomed in close
				if (mapView.getZoomLevel() > 14)
				{
					sItemizedOverlayFailed.setShowLabels(true);
					sItemizedOverlayWarning.setShowLabels(true);
					sItemizedOverlayPassed.setShowLabels(true);
					sItemizedOverlayNone.setShowLabels(true);
				}
			}
			else
			{
				// only show towns at this zoom level
				mapOverlays.clear();

				mapOverlays.add(tItemizedOverlayFailed);
				mapOverlays.add(tItemizedOverlayWarning);
				mapOverlays.add(tItemizedOverlayPassed);
				mapOverlays.add(tItemizedOverlayNone);
			}

		}
		// otherwise there is no state to restore
		else
		{
			if (town == null)
			{
				if (municipality.name != null)
				{
					this.setTitle(municipality.name);
				}

				mapOverlays.add(tItemizedOverlayFailed);
				mapOverlays.add(tItemizedOverlayWarning);
				mapOverlays.add(tItemizedOverlayPassed);
				mapOverlays.add(tItemizedOverlayNone);

				// set default zoom level
				controller.setZoom(ZOOM_DEFAULT_MUNICIPALITY);

				// centre map on town
				controller.setCenter(centrePointOfDataForWholeMap);

			}
			else
			{
				if (town.name != null)
				{
					this.setTitle(town.name);
				}

				mapOverlays.add(sItemizedOverlayFailed);
				mapOverlays.add(sItemizedOverlayWarning);
				mapOverlays.add(sItemizedOverlayPassed);
				mapOverlays.add(sItemizedOverlayNone);

				// set default zoom level
				controller.setZoom(ZOOM_DEFAULT_TOWN);

				// centre map on town centre point
				controller.setCenter(centrePointOfDataForTown);
			}
		}

		// FIXME HACK to make sure some points are shown on the map
		/*
		 * The towns currently don't have coordinates. So that means the towns
		 * won't get displayed on the map. This block makes sure that some
		 * points are shown on the map, and there is a similar work-around in
		 * the onUserInteraction() method.
		 * 
		 * Remove this block once the towns have coordinates, and are therefore
		 * able to be displayed on the map.
		 */
		mapOverlays.add(sItemizedOverlayFailed);
		mapOverlays.add(sItemizedOverlayWarning);
		mapOverlays.add(sItemizedOverlayPassed);
		mapOverlays.add(sItemizedOverlayNone);
		// HACK END

		// easy way to update map labels
		zoomLevel = mapView.getZoomLevel();
		this.onUserInteraction();
	}

	/**
	 * Called on starting the map display; calls a check to see if all
	 * coordinates are set for the data that is going to be displayed.
	 */
	@Override
	protected void onStart()
	{
		super.onStart();

		if ((dataForWholeMap == null)
			|| (!dataForWholeMap.checkForCoordinates()))
		{
			Toast
				.makeText(
					this.getApplicationContext(),
					"Warning: Some points do not have coordinates stored and thus may not display correctly.",
					Toast.LENGTH_LONG).show();
		}
	}


	/**
	 * If viewing towns, then this method is called to overlay towns on the map
	 * and calculates where the map should be centred based on the data
	 * overlaid.
	 * 
	 * @param startDate
	 *        start date of range of data to be displayed
	 * @param endDate
	 *        end date of range of data to be displayed
	 * @return point map needs to be centred on, or <code>null</code> if it
	 *         cannot be determined
	 */
	// TODO this doesn't work since towns don't have coordinates
	private GeoPoint addTownsOverlayAndCentreMap(Calendar startDate,
		Calendar endDate)
	{
		/*
		 * These are adjusted as each point is added to the map. They are used
		 * to ultimately define the visible boundaries of the map.
		 */
		double eastBoundDegrees = DEFAULT_MAP_WINDOW_RIGHT;
		double westBoundDegrees = DEFAULT_MAP_WINDOW_LEFT;
		double northBoundDegrees = DEFAULT_MAP_WINDOW_TOP;
		double southBoundDegrees = DEFAULT_MAP_WINDOW_BOTTOM;

		// used to assign the first sample point found as the centre point
		boolean anySampleCoordsFound = false;

		// use the data to refine the map centre point calculations
		if ((municipality != null) && (municipality.towns != null))
		{
			// loop over all the towns
			Town town = null;
			Iterator<Integer> i = municipality.towns.keySet().iterator();

			while (i.hasNext())
			{
				town = municipality.towns.get(i.next());

				// ignore the point if latitude & longitude are not defined
				if ((town.longitude != 0) || (town.latitude != 0))
				{
					// if this is the first sample, use it as centre point
					if (!anySampleCoordsFound)
					{
						westBoundDegrees = town.longitude;
						eastBoundDegrees = town.longitude;
						northBoundDegrees = town.latitude;
						southBoundDegrees = town.latitude;

						anySampleCoordsFound = true;
					}
					else
					{
						// adjust the boundaries if point outside
						if (town.longitude != 0)
						{
							if (town.longitude > eastBoundDegrees)
							{
								eastBoundDegrees = town.longitude;
							}
							if (town.longitude < westBoundDegrees)
							{
								westBoundDegrees = town.longitude;
							}
						}

						if (town.latitude != 0)
						{
							if (town.latitude < southBoundDegrees)
							{
								southBoundDegrees = town.latitude;
							}
							if (town.latitude > northBoundDegrees)
							{
								northBoundDegrees = town.latitude;
							}
						}
					}

					// find the water quality for this town
					DataIndicator overallQualityIndicator = application.dbAdapter
						.getAreaOverallQuality(town.id, DatabaseAdaptor.TOWN,
							startDate, endDate);

					// use the colour to determine if point has any data
					String colour = overallQualityIndicator.colour;
					boolean hasData = !TestIndicators.NONE.equals(colour);

					// convert coordinates into a GeoPoint to plot on map
					GeoPoint townGeoPoint = new GeoPoint(
						(int) (town.latitude * 1E6),
						(int) (town.longitude * 1E6));

					// create on-screen item for this town
					AquaOverlayItem overlayitem = new AquaOverlayItem(
						townGeoPoint, town.name, town.name + " "
							+ overallQualityIndicator.name, town.id,
						DatabaseAdaptor.TOWN, hasData);

					// add point to the correct overlay
					// TODO add a default colour check here
					if ((TestIndicators.FAIL.equals(colour))
						&& (tItemizedOverlayFailed != null))
					{
						tItemizedOverlayFailed.addOverlay(overlayitem);
					}
					else if ((TestIndicators.WARNING.equals(colour))
						&& (tItemizedOverlayWarning != null))
					{
						tItemizedOverlayWarning.addOverlay(overlayitem);
					}
					else if ((TestIndicators.PASS.equals(colour))
						&& (tItemizedOverlayPassed != null))
					{
						tItemizedOverlayPassed.addOverlay(overlayitem);
					}
					else if ((TestIndicators.NONE.equals(colour))
						&& (tItemizedOverlayNone != null))
					{
						tItemizedOverlayNone.addOverlay(overlayitem);
					}

				} // if
			} // while
		} // if != null

		// return the calculated centre point
		return MapUtils.calculateCentrePointFromDegrees(westBoundDegrees,
			eastBoundDegrees, northBoundDegrees, southBoundDegrees);
	}


	/**
	 * If viewing sampling points, then this method is called to overlay
	 * sampling points on the map.
	 */
	// TODO points are not coloured as data not loaded into sample by now
	private void addSamplingPointsOverlay(DataCache samplingPointData)
	{
		// add sampling points to the map overlay
		if ((samplingPointData != null)
			&& (samplingPointData.samplingPoints != null))
		{
			SamplingPoint samplingPoint = null;
			Iterator<SamplingPoint> i = samplingPointData.samplingPoints
				.values().iterator();

			while (i.hasNext())
			{
				samplingPoint = (SamplingPoint) i.next();

				// ignore the point if latitude & longitude are not defined
				if ((samplingPoint.longitude != 0)
					|| (samplingPoint.latitude != 0))
				{
					// use the colour to determine if point has any data
					String colour = samplingPoint.getColour();
					boolean hasData = !TestIndicators.NONE.equals(colour);

					// convert coordinates into a GeoPoint to plot on map
					int tmpLong = (int) (samplingPoint.longitude * 1E6);
					int tmpLat = (int) (samplingPoint.latitude * 1E6);
					GeoPoint sampleGeoPoint = new GeoPoint(tmpLat, tmpLong);

					// create on-screen item for this data-point
					AquaOverlayItem overlayitem = new AquaOverlayItem(
						sampleGeoPoint, samplingPoint.pointName,
						samplingPoint.pointName, samplingPoint.id,
						DatabaseAdaptor.SAMPLINGPOINT, hasData);

					// add point to the correct overlay
					// TODO add a default colour check here
					if ((TestIndicators.FAIL.equals(colour))
						&& (sItemizedOverlayFailed != null))
					{
						sItemizedOverlayFailed.addOverlay(overlayitem);
					}
					else if ((TestIndicators.WARNING.equals(colour))
						&& (sItemizedOverlayWarning != null))
					{
						sItemizedOverlayWarning.addOverlay(overlayitem);
					}
					else if ((TestIndicators.PASS.equals(colour))
						&& (sItemizedOverlayPassed != null))
					{
						sItemizedOverlayPassed.addOverlay(overlayitem);
					}
					else if ((TestIndicators.NONE.equals(colour))
						&& (sItemizedOverlayNone != null))
					{
						sItemizedOverlayNone.addOverlay(overlayitem);
					}

				} // if
			} // while
		} // if != null
	}

	/**
	 * If viewing sampling points, then this method calculates where the map
	 * should be centred based on the data to be overlaid. If no data is
	 * provided, the map should centre in the middle of South Africa.
	 * 
	 * @return centre point for the map window
	 */
	private GeoPoint calculateCentreOfMap(DataCache samplingPointData)
	{
		/*
		 * These are adjusted as each point is added to the map. They are used
		 * to ultimately define the visible boundaries of the map.
		 */
		double eastBoundDegrees = DEFAULT_MAP_WINDOW_RIGHT;
		double westBoundDegrees = DEFAULT_MAP_WINDOW_LEFT;
		double northBoundDegrees = DEFAULT_MAP_WINDOW_TOP;
		double southBoundDegrees = DEFAULT_MAP_WINDOW_BOTTOM;

		// used to assign the first sample point found as the centre point
		boolean anySampleCoordsFound = false;

		// use the data to refine the map centre point calculations
		if ((samplingPointData != null)
			&& (samplingPointData.samplingPoints != null))
		{
			SamplingPoint samplingPoint = null;
			Iterator<SamplingPoint> i = samplingPointData.samplingPoints
				.values().iterator();

			while (i.hasNext())
			{
				samplingPoint = (SamplingPoint) i.next();

				// ignore the point if latitude & longitude are not defined
				if ((samplingPoint.longitude != 0)
					|| (samplingPoint.latitude != 0))
				{
					// if this is the first sample, use it as centre point
					if (!anySampleCoordsFound)
					{
						westBoundDegrees = samplingPoint.longitude;
						eastBoundDegrees = samplingPoint.longitude;
						northBoundDegrees = samplingPoint.latitude;
						southBoundDegrees = samplingPoint.latitude;

						anySampleCoordsFound = true;
					}
					else
					{
						// adjust the boundaries if point outside
						if (samplingPoint.longitude != 0)
						{
							if (samplingPoint.longitude > eastBoundDegrees)
							{
								eastBoundDegrees = samplingPoint.longitude;
							}
							if (samplingPoint.longitude < westBoundDegrees)
							{
								westBoundDegrees = samplingPoint.longitude;
							}
						}

						if (samplingPoint.latitude != 0)
						{
							if (samplingPoint.latitude < southBoundDegrees)
							{
								southBoundDegrees = samplingPoint.latitude;
							}
							if (samplingPoint.latitude > northBoundDegrees)
							{
								northBoundDegrees = samplingPoint.latitude;
							}
						}
					}
				} // if
			} // while
		} // if != null

		// return the calculated centre point
		return MapUtils.calculateCentrePointFromDegrees(westBoundDegrees,
			eastBoundDegrees, northBoundDegrees, southBoundDegrees);
	}


	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Called when user interacts with map. Used to check if the zoom level has
	 * gone beyond a certain point and its necessary to switch between viewing
	 * towns or sampling points.
	 */
	@Override
	public void onUserInteraction()
	{
		super.onUserInteraction();

		// if ((mapView.getZoomLevel() > 9) && (zoomLevel <= 9)) {

		double longitude = mapView.getMapCenter().getLongitudeE6() / 1E6;
		double latitude = mapView.getMapCenter().getLatitudeE6() / 1E6;

		/*
		 * Town t = municipality.getTownNearestToLocation(longitude, latitude);
		 * 
		 * if (t != null) { String townName = t.name;
		 * Toast.makeText(this.getApplicationContext(),
		 * "Switching to view Sampling Points; nearest town: " + townName
		 * ,Toast.LENGTH_SHORT).show(); } else {
		 * Toast.makeText(this.getApplicationContext(),
		 * "Switching to view Sampling Points", Toast.LENGTH_SHORT).show(); }
		 *
		 * this.setTitle("Sampling Points");
		 */

		// FIXME HACK to make sure some points are shown on the map
		/*
		 * The towns currently don't have coordinates. So that means the towns
		 * won't get displayed on the map. This block makes sure that some
		 * points are shown on the map, and there is a similar work-around in
		 * the onCreate() method.
		 * 
		 * Remove this block once the towns have coordinates, and are therefore
		 * able to be displayed on the map.
		 */
		mapOverlays.clear();

		mapOverlays.add(sItemizedOverlayFailed);
		mapOverlays.add(sItemizedOverlayWarning);
		mapOverlays.add(sItemizedOverlayPassed);
		mapOverlays.add(sItemizedOverlayNone);
		// HACK END

		/*
		 * if ((mapView.getZoomLevel() <= 9) && (zoomLevel > 9)) {
		 * 
		 * Toast.makeText(this.getApplicationContext(),
		 * "Switching to view Towns in Municipality: " + municipality.name
		 * ,Toast.LENGTH_LONG).show();
		 * 
		 * this.setTitle(municipality.name);
		 * 
		 * mapOverlays.clear();
		 * 
		 * mapOverlays.add(tItemizedOverlayFailed);
		 * mapOverlays.add(tItemizedOverlayWarning);
		 * mapOverlays.add(tItemizedOverlayPassed);
		 * mapOverlays.add(tItemizedOverlayNone); }
		 */

		if (mapView.getZoomLevel() <= 14)
		{
			sItemizedOverlayFailed.setShowLabels(false);
			sItemizedOverlayWarning.setShowLabels(false);
			sItemizedOverlayPassed.setShowLabels(false);
			sItemizedOverlayNone.setShowLabels(false);
		}
		if (mapView.getZoomLevel() > 14)
		{
			sItemizedOverlayFailed.setShowLabels(true);
			sItemizedOverlayWarning.setShowLabels(true);
			sItemizedOverlayPassed.setShowLabels(true);
			sItemizedOverlayNone.setShowLabels(true);
		}


		zoomLevel = mapView.getZoomLevel();
		// // Log.v("Steve", String.valueOf(zoomLevel));
	}


	/**
	 * Called when sampling point or town on the map is clicked. View switches
	 * to
	 * 
	 * @param itemId
	 *        id of the item the point represents
	 * @param itemType
	 *        whether it is a sampling point or town
	 * @param hasData
	 *        whether the point has any measured values or not and thus
	 *        shouldn't invoke a screen change when clicked
	 */
	public void itemClicked(int itemId, int itemType, boolean hasData)
	{
		/*
		 * before calling CalendarView, we must set the current municipality,
		 * and current town must be set. CalendarView works off those global
		 * variables.
		 */

		// set current town
		Town selectedTown;
		if (DatabaseAdaptor.TOWN == itemType)
		{
			// get selected town from id
			selectedTown = application.dbAdapter.getTown(itemId);
		}
		else
		{
			// get town from currently selected sampling point
			SamplingPoint selectedSamplingPoint = dataForWholeMap.samplingPoints
				.get(itemId);
			selectedTown = application.dbAdapter
				.getTown(selectedSamplingPoint.townId);
		}
		application.setCurrentTown(selectedTown);
		
		// set the current municipality
		application.setCurrentMunicipality(municipality);

		Intent intent = new Intent(this, AquaCalendar.class);
		startActivity(intent);


		/*
		 * commented out instead of deleted in case it needs to be reintroduced
		 */
		// if (hasData)
		// {
		//
		// Uri uri;
		// if (itemType == DatabaseAdaptor.SAMPLINGPOINT)
		// {
		// uri = TableViews
		// .buildUri(DatabaseAdaptor.MUNICIPALITY, municipality.id,
		// TableViews.POINT_RECENT_PARAMETERS, itemId);
		// }
		// else
		// {
		// uri = TableViews.buildUri(DatabaseAdaptor.TOWN, itemId,
		// TableViews.RECENT_PARAMETERS_GROUPED_BY_DATE, itemId);
		// }
		//
		// Intent intent = new Intent(this, DataList.class);
		// intent.setData(uri);
		// startActivity(intent);
		// }
	}


	/**
	 * Save the map state so that on coming back to the screen it hasn't reset.
	 */
	@Override
	public void onSaveInstanceState(Bundle bundle)
	{

		super.onSaveInstanceState(bundle);
		bundle.putInt("zoom", mapView.getZoomLevel());
		bundle.putInt("latitude", mapView.getMapCenter().getLatitudeE6());
		bundle.putInt("longitude", mapView.getMapCenter().getLongitudeE6());
		bundle.putString("title", this.getTitle().toString());

	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// Log.v("Steve", "****CLEARING MEMORY*****");
		drawableSamplePointFailed.setCallback(null);
		drawableSamplePointWarning.setCallback(null);
		drawableSamplePointPass.setCallback(null);
		drawableSamplePointNone.setCallback(null);

		drawableTownFailed.setCallback(null);
		drawableTownWarning.setCallback(null);
		drawableTownPass.setCallback(null);
		drawableTownNone.setCallback(null);
	}

}