/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.ds.avare.shapes;

import com.ds.avare.position.Epsg900913;
import com.ds.avare.storage.Preferences;
import com.ds.avare.utils.BitmapHolder;
import com.ds.avare.utils.NetworkHelper;


/**
 * @author zkhan
 * The class that holds all info about a tile
 */
public class Tile {
    /**
     * 
     * Center tile is most important aspect of this database.
     * Everything is relative to this tile, so we store center tiles aspects like
     */
    private double mLonUL;
    private double mLonLL;
    private double mLonUR;
    private double mLonLR;
    private double mLatUL;
    private double mLatLL;
    private double mLatUR;
    private double mLatLR;
    private double mLonC;
    private double mLatC;
    private double mWidth;
    private double mHeight;
    private int mRow;
    private int mCol;
    private double mZoom;
    private Epsg900913 mProj;
    private Preferences mPref;

    /**
     * Common function for all tile constructors.
     */
    private void setup() {
        mLonUL = mProj.getLonUpperLeft();
        mLatUL = mProj.getLatUpperLeft();
        mLonLR = mProj.getLonLowerRight();
        mLatLR = mProj.getLatLowerRight();
        mLonLL = mProj.getLonLowerLeft();
        mLatLL = mProj.getLatLowerLeft();
        mLonUR = mProj.getLonUpperRight();
        mLatUR = mProj.getLatUpperRight();
        mLonC = mProj.getLonCenter();
        mLatC = mProj.getLatCenter();
        mRow = mProj.getTiley();
        mCol = mProj.getTilex();
        mWidth = BitmapHolder.WIDTH;
        mHeight = BitmapHolder.HEIGHT;    	
    }
    
    /**
     * 
     * @param t
     * @param row
     * @param col
     */
    public Tile(Tile t, int col, int row) {
    	// Make a new tile from a give center tile, at an offset of row/col
    	Epsg900913 proj = t.getProjection();
    	int tx = proj.getTilex() + col;
    	int ty = proj.getTiley() - row; // row increase up
        mZoom = t.getZoom();
    	mProj = new Epsg900913(tx, ty, mZoom);
    	setup();
        mPref = t.mPref;
    }

    /**
     * Get a tile for a particular position for elevation
     * @param pref
     * @param lon
     * @param lat
     * @param chart
     */
    public Tile(Preferences pref, double lon, double lat) {
        mZoom = 10;
    	mProj = new Epsg900913(lat, lon, mZoom);
    	setup();
        mPref = pref;
    }

    /**
     * Get a tile for a particular position
     * @param pref
     * @param lon
     * @param lat
     * @param chart
     */
    public Tile(Preferences pref, double lon, double lat, double zoom) {
        mZoom = 10 - zoom;
    	mProj = new Epsg900913(lat, lon, mZoom);
    	setup();
        mPref = pref;
    }

    /**
     * Find if give location is within this tile
     * @param lon
     * @param lat
     * @return
     */
    public boolean within(double lon, double lat) {
        return (
                (mLonUL <= lon) && (mLonLL <= lon) && (mLonUR >= lon) && (mLonLR >= lon) &&
                (mLatUL >= lat) && (mLatUR >= lat) && (mLatLL <= lat) && (mLatLR <= lat)
               );          
    }

    /**
     * @return
     */
    public double getPx() {
        return(-((mLonUL - mLonUR)  + (mLonLL - mLonLR)) / (mWidth * 2));
    }
    
    /**
     * @return
     */
    public double getPy() {
        return(-((mLatUL - mLatLL)  + (mLatUR - mLatLR)) / (mHeight * 2));
    }

    /**
     * Find offsetTopX from top of tile
     * @param lon
     * @return
     */
    public double getOffsetTopX(double lon) {
        double px = getPx();
        
        if(px != 0) {
            return(lon - mLonUL) / px;
        }
        else {
            return(0);
        }        
    }

    /**
     * Find offsetTopY from top of tile
     * @param lon
     * @return
     */
    public double getOffsetTopY(double lat) {
        double py = getPy();
        
        if(py != 0) {
            return(lat - mLatUL) / py;
        }
        else {
            return(0);
        }        
    }

    /**
     * Find offsetX from center of tile
     * @param lon
     * @return
     */
    public double getOffsetX(double lon) {

        double px = getPx();
        
        if(px != 0) {
            return(lon - mLonC) / px - (BitmapHolder.WIDTH / 2 - mWidth / 2);
        }
        else {
            return(0);
        }
    }
    
    /**
     * Find offsetY from center of tile
     * @param lon
     * @param lat
     * @return
     */
    public double getOffsetY(double lat) {

        double py = getPy();
        
        if(py != 0) {
            return (lat - mLatC) / py - (BitmapHolder.HEIGHT / 2 - mHeight / 2);
        }
        else {
            return(0);
        }
    }

    /**
     * @param rowm
     * @return Neighboring tile based on its row
     */
    private int getNeighborRow(int rowm) {
    	return mRow + rowm;
    }
    
    /**
     * @param colm
     * @return Neighboring tile based on its col
     */
    private int getNeighborCol(int colm) {
        return  mCol + colm;
    }
    
    public double getLatitude() {
        return mLatC;
    }
    
    public double getLongitude() {
        return mLonC;
    }
    
    private Epsg900913 getProjection() {
    	return mProj;
    }
    
    private double getZoom() {
    	return mZoom;
    }

    /**
     * @return Name of the tile relative to this tile (col, row)
     */
    public String getTileNeighbor(int col, int row) {
    	int coll = getNeighborCol(col);
    	int rowl = getNeighborRow(row);
    	// form /tiles/cycle/type/all/zoom/col/row.png
    	String name = "tiles/" + NetworkHelper.getVersion(mPref.getCycleAdjust()) + "/" + mPref.getChartType() 
    			+ "/all/" + (int)mZoom +  "/" + coll + "/" + rowl + Preferences.IMAGE_EXTENSION; 
        return(name);
    }

    /**
     * @return Name of the tile
     */
    public String getName() {
    	return getTileNeighbor(0, 0);
    }

}
