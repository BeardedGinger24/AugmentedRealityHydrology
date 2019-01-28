package edu.calstatela.jplone.watertrekapp.Data;

/**
 * Created by kz on 2/20/18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.arframework.graphics3d.helper.MeshHelper;

/**
 * Created by ProgrammingKnowledge on 4/3/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;
    //Database name
    public static final String DATABASE_NAME = "HYDROLOGY";

    //Table Names
    private static final String TABLE_DBGS = "DBGS";
    private static final String TABLE_MESHDATA = "MESHDATA";
    private static final String TABLE_WELL = "WELL";
    private static final String TABLE_SOILMOISTURE = "SOILMOISTURE";
    private static final String TABLE_SNOTEL = "SNOTEL";
    private static final String TABLE_RESERVOIR = "RESERVOIR";
    private static final String TABLE_LOG = "LOG";

    //common column names
    private static final String KEY_LAT = "LAT";
    private static final String KEY_LON = "LON";
    private static final String KEY_ALT = "ALT";
    private static final String KEY_MAX = "MAX";
    private static final String KEY_MIN = "MIN";
    private static final String KEY_MAXDATE = "MAXDATE";
    private static final String KEY_MINDATE = "MINDATE";
    private static final String KEY_SITE_NO = "SITE_NO";
    private static final String KEY_STATION_NAME = "STATION_NAME";

    //Mesh coloumn names
    private static final String KEY_DIRECTORY = "DIRECTORY";
    private static final String KEY_FILENAME_TERRAIN = "FILENAMETERRAIN";
    private static final String KEY_FILENAME_VECS = "FILENAMEVECS";


    //Well column names
    private static final String KEY_MASTER_SITE_ID = "MASTER_SITE_ID";
    private static final String KEY_CASGEM_STATION_ID = "CASGEM_STATION_ID";
    private static final String KEY_STATE_WELL_NBR = "STATE_WELL_NBR";
    private static final String KEY_SITE_CODE = "SITE_CODE";
    private static final String KEY_COUNT = "COUNT";
    private static final String KEY_AVERAGE = "AVERAGE";
    private static final String KEY_STDDEV = "STDDEV";

    //DBGS column names
    private static final String KEY_MEASUREMENT = "MEASUREMENT";
    //SoilMoisture column names
    private static final String KEY_WBANNO = "WBANNO";
    //LOG column names
    private static final String KEY_TIME = "TIME";
    private static final String KEY_ID = "ID";

    private static final String CREATE_TABLE_MESHDATA = "CREATE TABLE IF NOT EXISTS "+TABLE_MESHDATA+" ("+KEY_FILENAME_TERRAIN+" VARCHAR(30), "
            + KEY_DIRECTORY+" VARCHAR(30), " +KEY_LAT+ " DOUBLE, "+KEY_LON+" DOUBLE, "+KEY_ALT+" DOUBLE)";
    //private static final String CREATE_TABLE_RESERVOIR = "create table " + TABLE_RESERVOIR + "（" + KEY_SITE_NO + " varchar(300) PRIMARY KEY ASC,"
    //      + KEY_DESCRIPTION + " varchar(300)," +  KEY_LAT + " double," + KEY_MAX + " double, " + KEY_MIN + " DOUBLE)";
    private static final String CREATE_TABLE_WELL = "CREATE TABLE IF NOT EXISTS "+ TABLE_WELL + "(" + KEY_MASTER_SITE_ID + " VARCHAR(30), "
            + KEY_CASGEM_STATION_ID + " VARCHAR(30), " + KEY_STATE_WELL_NBR + " VARCHAR(30), " + KEY_SITE_CODE + " VARCHAR(30), "
            +KEY_LAT+" VARCHAR(30), " + KEY_LON + " VARCHAR(30), " + KEY_MAX + " VARCHAR(30), " + KEY_MIN +
            " VARCHAR(30), " + KEY_COUNT + " VARCHAR(30), " + KEY_AVERAGE + " VARCHAR(30), " + KEY_STDDEV +
            " VARCHAR(30)) ";
    private static final String CREATE_TABLE_DBGS = "CREATE TABLE IF NOT EXISTS " + TABLE_DBGS + "(" + KEY_MASTER_SITE_ID + " VARCHAR(30), "
            + KEY_MEASUREMENT + " VARCHAR(100))";
    private static final String CREATE_TABLE_SOILMOISTURE = "CREATE TABLE IF NOT EXISTS "+ TABLE_SOILMOISTURE + "("+ KEY_WBANNO +
            " VARCHAR(30), " + KEY_LON + " VARCHAR(30), " + KEY_LAT + " VARCHAR(30), "+ KEY_MAX + " VARCHAR(30), " + KEY_MIN + " VARCHAR(30))";
    private static final String CREATE_TABLE_LOG = "CREATE TABLE IF NOT EXISTS LOG ( LAT TEXT, LON TEXT, ID TEXT, TIME TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_MESHDATA);
        db.execSQL(CREATE_TABLE_WELL);
        db.execSQL(CREATE_TABLE_SOILMOISTURE);
        db.execSQL(CREATE_TABLE_LOG);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESHDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WELL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOILMOISTURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
        onCreate(db);
    }

    public void closeDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    //Mesh functions
    public void addMeshData(SQLiteDatabase db,MeshData meshData){
        ContentValues values = new ContentValues();
        float loc[] = meshData.getLatlonAlt();
        values.put(KEY_DIRECTORY,meshData.dir);
        values.put(KEY_FILENAME_TERRAIN,meshData.filenameTerrain);
        //values.put(KEY_FILENAME_VECS,meshData.filenameVecs);
        values.put(KEY_LAT,loc[0]);
        values.put(KEY_LON,loc[1]);
        values.put(KEY_ALT,loc[2]);
        db.insert(TABLE_MESHDATA,null,values);
    }
    public float[] getMeshData(SQLiteDatabase db, String filename){
        String query = "SELECT "+"*"+" FROM " + TABLE_MESHDATA + " WHERE " + KEY_FILENAME_TERRAIN + " = " + "'"+filename+"'";
        Cursor c = db.rawQuery(query, null);
        if( c != null) {
            c.moveToFirst();
        }
        float[] loc = new float[]{c.getFloat(c.getColumnIndex(KEY_LAT)),c.getFloat(c.getColumnIndex(KEY_LON)),c.getFloat(c.getColumnIndex(KEY_ALT))};
        return loc;
    }
    //well functions
    public void addWell (Well well){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MASTER_SITE_ID, well.getMasterSiteId());
        values.put(KEY_CASGEM_STATION_ID, well.getCasgemStationId());
        values.put(KEY_STATE_WELL_NBR, well.getStateWellNbr());
        values.put(KEY_SITE_CODE, well.getSiteCode());
        values.put(KEY_LAT, well.getLat());
        values.put(KEY_LON, well.getLon());
        values.put(KEY_MAX, well.getMax());
        values.put(KEY_COUNT, well.getCount() );
        values.put(KEY_AVERAGE, well.getAvg());
        values.put(KEY_STDDEV, well.getStdDev());
        db.replace(TABLE_WELL, null, values);
    }
    public void deleteWell(String masterSiteID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WELL, KEY_MASTER_SITE_ID + " = ?",
                new String[] {masterSiteID});
        db.delete(TABLE_DBGS, KEY_MASTER_SITE_ID + " = ?",
                new String[] {masterSiteID});
    }
    public Well getWell(String masterSiteID){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_WELL + " WHERE " + KEY_MASTER_SITE_ID + " = " + masterSiteID;
        Log.e(LOG, query);
        Cursor c = db.rawQuery(query, null);
        if( c != null)
            c.moveToFirst();
        Well w = new  Well();
        w.setAvg(c.getString(c.getColumnIndex(KEY_AVERAGE)));
        w.setCasgemStationId(c.getString(c.getColumnIndex(KEY_CASGEM_STATION_ID)));
        w.setCount(c.getString(c.getColumnIndex(KEY_COUNT)));
        w.setLat(c.getString(c.getColumnIndex(KEY_LAT)));
        w.setLon(c.getString(c.getColumnIndex(KEY_LON)));
        w.setMasterSiteId(c.getString(c.getColumnIndex(KEY_MASTER_SITE_ID)));
        w.setMax(c.getString(c.getColumnIndex(KEY_MAX)));
        w.setMin(c.getString(c.getColumnIndex(KEY_MIN)));
        w.setSiteCode(c.getString(c.getColumnIndex(KEY_SITE_CODE)));
        w.setStateWellNbr(c.getString(c.getColumnIndex(KEY_STATE_WELL_NBR)));
        w.setStdDev(c.getString(c.getColumnIndex(KEY_STDDEV)));

        return w;
    }
    public List<Well> getWells(){
        List<Well> Wells = new ArrayList<Well>();
        String query = "SELECT * FROM "+ TABLE_WELL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        Log.e(c.getColumnName(0),"test");

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Well w = new  Well();
                w.setAvg(c.getString(c.getColumnIndex(KEY_AVERAGE)));
                w.setCasgemStationId(c.getString(c.getColumnIndex(KEY_CASGEM_STATION_ID)));
                w.setCount(c.getString(c.getColumnIndex(KEY_COUNT)));
                w.setLat(c.getString(c.getColumnIndex(KEY_LAT)));
                w.setLon(c.getString(c.getColumnIndex(KEY_LON)));
                w.setMasterSiteId(c.getString(c.getColumnIndex(KEY_MASTER_SITE_ID)));
                w.setMax(c.getString(c.getColumnIndex(KEY_MAX)));
                w.setMin(c.getString(c.getColumnIndex(KEY_MIN)));
                w.setSiteCode(c.getString(c.getColumnIndex(KEY_SITE_CODE)));
                w.setStateWellNbr(c.getString(c.getColumnIndex(KEY_STATE_WELL_NBR)));
                w.setStdDev(c.getString(c.getColumnIndex(KEY_STDDEV)));

                Wells.add(w);
                c.moveToNext();
            }
        }


        return Wells;
    }
    //Soil functions
    public void addSoil(SoilMoisture soil){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WBANNO, soil.getWbanno());
        values.put(KEY_LON, soil.getLon());
        values.put(KEY_LAT, soil.getLat());
        values.put(KEY_MAX, soil.getMax());
        values.put(KEY_MIN, soil.getMin());
        db.replace(TABLE_SOILMOISTURE, null, values);

    }
    public List<SoilMoisture> getSoils(){
        List<SoilMoisture> soils = new ArrayList<SoilMoisture>();
        String query = "SELECT * FROM "+ TABLE_SOILMOISTURE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        Log.e(c.getColumnName(0),"test");

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                SoilMoisture s = new SoilMoisture();
                s.setLat(c.getString(c.getColumnIndex(KEY_LAT)));
                s.setLon(c.getString(c.getColumnIndex(KEY_LON)));
                s.setMax(c.getString(c.getColumnIndex(KEY_MAX)));
                s.setMin(c.getString(c.getColumnIndex(KEY_MIN)));
                s.setWbanno(c.getString(c.getColumnIndex(KEY_WBANNO)));
                soils.add(s);
                c.moveToNext();
            }
        }


        return soils;
    }
    public void addLog(String lat, String lon, String ID, String datetime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LAT, lat);
        values.put(KEY_LON, lon);
        values.put(KEY_ID, ID);
        values.put(KEY_TIME, datetime);
        db.replace(TABLE_LOG, null, values);
    }
    public Record getRecord(int ID) {
        Record r = new Record();
        String query = "SELECT * FROM " + TABLE_LOG + " WHERE "+ KEY_ID + "= "+ ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                r.setLat(c.getString(c.getColumnIndex(KEY_LAT)));
                r.setLon(c.getString(c.getColumnIndex(KEY_LON)));
                r.setID(c.getString(c.getColumnIndex(KEY_ID)));
                r.setDate(c.getString(c.getColumnIndex(KEY_TIME)));
                c.moveToNext();
            }
        }
        Log.d(r.getLat()+"","lat:");
        Log.d(r.getLon()+"", "lon:");
        Log.d(r.getType_id()+"", "id:");
        Log.d(r.getDate()+"", "date:");
        return r;
    }
}


