/*
Copyright (c) 2015, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.ds.avare.position;

import com.ds.avare.utils.BitmapHolder;

/**
 * A class that finds tile at a give location and zooms
 * @author zkhan
 *
 */
public class Epsg900913 {

    /**
     * To get tile info.
     */
    private static final double SIZE = BitmapHolder.HEIGHT;
	private static final double ORIGIN_SHIFT = 2 * Math.PI * 6378137.0 / 2.0;
    private static final double INITIAL_RESOLUTION = 20037508.342789244 * 2 / SIZE;

    private int mTx;
    private int mTy;
    private double mLonL;
    private double mLatU;
    private double mLonR;
    private double mLatD;

    private void findBounds(double resolution) {
        //Converts pixel coordinates in given zoom level of pyramid to EPSG:900913
        //Find upper left coordinates, and lower right coordinates of this tile (lower right are upper left of next tile below)
        double minx = (mTx * SIZE * resolution - ORIGIN_SHIFT);
        double miny = (mTy * SIZE * resolution - ORIGIN_SHIFT);
        double maxx = ((mTx + 1) * SIZE * resolution - ORIGIN_SHIFT);
        double maxy = ((mTy + 1) * SIZE * resolution - ORIGIN_SHIFT);
        
        //Returns bounds of the given tile in latutude/longitude using WGS84 datum
        
        mLonL = (minx / ORIGIN_SHIFT) * 180.0;
        mLatD = 180 / Math.PI * (2 * Math.atan(Math.exp((miny / ORIGIN_SHIFT) * Math.PI)) - Math.PI / 2.0);
        mLonR = (maxx / ORIGIN_SHIFT) * 180.0;
        mLatU = 180 / Math.PI * (2 * Math.atan(Math.exp((maxy / ORIGIN_SHIFT) * Math.PI)) - Math.PI / 2.0);
    }
   
    /**
     * Find tile
     * @param lat
     * @param lon
     * @param zoom
     */
    public Epsg900913(double lat, double lon, double zoom) {
        
    	double resolution = INITIAL_RESOLUTION / Math.pow(2, zoom);
        // "Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator EPSG:900913"
    	// to meters
        double mx = lon * ORIGIN_SHIFT / 180.0;
        double my = Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) * 180.0 / Math.PI * ORIGIN_SHIFT / 180.0;
        
        //"Converts EPSG:900913 to pyramid pixel coordinates in given zoom level"
        // to pixels
        double px = (mx + ORIGIN_SHIFT) / resolution;
        double py = (my + ORIGIN_SHIFT) / resolution;

        //tile number
        mTx = (int)(px / SIZE);
        mTy = (int)(py / SIZE);

        findBounds(resolution);
    }

    /**
     * Find tile
     * @param lat
     * @param lon
     * @param zoom
     */
    public Epsg900913(int tx, int ty, double zoom) {
        
    	// Make projection from a given tile number
    	double resolution = INITIAL_RESOLUTION / Math.pow(2, zoom);

        //tile number
        mTx = tx;
        mTy = ty;
        
        findBounds(resolution);
    }

    /*
     * Tile col/rows
     */
    public int getTilex() {
    	return mTx;
    }

    public int getTiley() {
    	return mTy;
    }

    /*
     * Linear interpolation will do
     */
    
    public double getLonUpperLeft() {
    	return mLonL;
    }

    public double getLonLowerLeft() {
    	return mLonL;
    }

    public double getLonLowerRight() {
    	return mLonR;
    }

    public double getLonUpperRight() {
    	return mLonR;
    }

    public double getLonCenter() {
    	return (mLonR + mLonL) / 2.0;
    }

    public double getLatUpperLeft() {
    	return mLatU;
    }

    public double getLatUpperRight() {
    	return mLatU;
    }

    public double getLatLowerRight() {
    	return mLatD;
    }

    public double getLatLowerLeft() {
    	return mLatD;
    }

    public double getLatCenter() {
    	return (mLatU + mLatD) / 2.0;
    }
    
}
