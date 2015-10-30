package teamc.toadunlock;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.aware.Accelerometer;
import com.aware.Applications;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Light;

import com.aware.providers.Applications_Provider;
import com.aware.providers.Battery_Provider.Battery_Data;
import com.aware.providers.Locations_Provider;

import java.util.Calendar;


/**
 * Created by Comet on 22/10/15.
 */
public class UnlockService extends Service{
    private Intent mFxLockIntent = null;
    private KeyguardManager mKeyguardManager = null ;
    private KeyguardManager.KeyguardLock mKeyguardLock = null ;

    //sensor data
    public static String foreground_package;
    //Accelerometer
    public static double acc_x=0;
    public static double acc_y=0;
    public static double acc_z=0;
    //screen unlock
    private boolean scr_cache = false;
    //accelerometer data got?
    private boolean acc_cache = false;
    //useful app?
    private boolean app_cache = false;

    //Battery
    public static double battery_level=0;
    public static double battery_status=0;
    private boolean battery_cache = false;

    //light
    public static double light_level=0;
    private boolean light_cache = false;

    //GPS
    public static double GPSlatitude=0;
    public static double GPSlongitude=0;
    public static double GPSbearing=0;
    public static double GPSspeed=0;
    public static double GPSaltitude=0;
    private boolean GPS_cache = false;

    //
    public static double hour=0;
    public static double day=0;
    private boolean time_cache = false;

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();

        mFxLockIntent = new Intent(UnlockService.this, MainActivity.class);
        mFxLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("Toad1", "Service 27");
        //register listener for screen
        IntentFilter mScreenOnOrOffFilter = new IntentFilter();
        mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_ON");
        mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_OFF");

        UnlockService.this.registerReceiver(mScreenOnOrOffReceiver, mScreenOnOrOffFilter);

        Intent aware=new Intent (this, Aware.class);
        startService(aware);

        //accelerometer
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
        //app
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_APPLICATIONS, true);
        //locus
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_GPS,true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_NETWORK,true);

        //
        IntentFilter filter = new IntentFilter();
        filter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER);
        filter.addAction(Applications.ACTION_AWARE_APPLICATIONS_FOREGROUND);
        filter.addAction(Light.ACTION_AWARE_LIGHT);

        registerReceiver(contextBR, filter);
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }


    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        startService(new Intent(UnlockService.this, UnlockService.class));
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_APPLICATIONS, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_GPS,false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_NETWORK,false);

        if(contextBR != null)
            unregisterReceiver(contextBR);
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }

    private BroadcastReceiver mScreenOnOrOffReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            if (intent.getAction().equals("android.intent.action.SCREEN_ON")
                    || intent.getAction().equals("android.intent.action.SCREEN_OFF"))
            {
                Log.d("Toad1", "Service 55");
                mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                mKeyguardLock = mKeyguardManager.newKeyguardLock("FxLock");
                //
                mKeyguardLock.disableKeyguard();
                //start unlock screen
                startActivity(mFxLockIntent);
            }


        }
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub

        return null;
    }

    private ContextReceiver contextBR = new ContextReceiver();
    public class ContextReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Sensors Data

            //Get the Foreground app
            if (intent.getAction().equals(Applications.ACTION_AWARE_APPLICATIONS_FOREGROUND)) {
                //How can I get app info?
                boolean uselessApp=false;

                Cursor cursorApp = context.getContentResolver().query(Applications_Provider.Applications_Foreground.CONTENT_URI, null, null, null, Applications_Provider.Applications_Foreground.TIMESTAMP + " DESC LIMIT 1");
                if (cursorApp != null && cursorApp.moveToFirst()) {
                    foreground_package = cursorApp.getString(cursorApp.getColumnIndex(Applications_Provider.Applications_Foreground.PACKAGE_NAME));
                    PackageManager cjj = getPackageManager();
                    Intent LaunchIntent = cjj.getLaunchIntentForPackage(foreground_package);
                    //unlock screen first
                    if(foreground_package.equals("teamc.toadunlock"))
                    {
                        uselessApp=true;
                        scr_cache=true; //log the next app
                    }
                    else if(LaunchIntent==null)//foreground_package.equals("teamc.toadunlock"))
                    {
                        uselessApp=true;
                    }

                    else{ // for test, logged app

                        Log.d("SENSORS", foreground_package);
                    }
                }
                if (cursorApp != null && !cursorApp.isClosed())
                    cursorApp.close();
                //app_cache is useful App
                app_cache=!uselessApp;
            }

            if(!app_cache)
            {
                //won't log useless Apps
                return;
            }
            //useful but not after toad
            if(!scr_cache)
            {
                return;
            }

            //log this

            //Accelerometer data

            if (!acc_cache&&intent.getAction().equals(Accelerometer.ACTION_AWARE_ACCELEROMETER)) {
                ContentValues acc_data = (ContentValues) intent.getParcelableExtra(Accelerometer.EXTRA_DATA);
                if (acc_data != null) {
                    acc_x = acc_data.getAsDouble("double_values_0");
                    acc_y = acc_data.getAsDouble("double_values_1");
                    acc_z = acc_data.getAsDouble("double_values_2");
                }else {
                    acc_x = 0;
                    acc_y = 0;
                    acc_z = 10;
                }
                acc_cache = true;
                //Log.d("SENSORS", "A_VALUES_0=" + acc_x);
            }


            //Battery
            if(!battery_cache)
            {
                Cursor cursorBattery = context.getContentResolver().query(Battery_Data.CONTENT_URI, null, null, null, Battery_Data.TIMESTAMP + " DESC LIMIT 1");
                if (cursorBattery != null && cursorBattery.moveToFirst()) {
                    battery_level = cursorBattery.getDouble(cursorBattery.getColumnIndex(Battery_Data.LEVEL));
                    battery_status = cursorBattery.getDouble(cursorBattery.getColumnIndex(Battery_Data.STATUS));
                    battery_cache = true;
                    //Log.d("SENSORS", "BL_VALUES_0=" + battery_level);
                    //Log.d("SENSORS", "BS_VALUES_0=" + battery_status);
                }
                if (cursorBattery != null && !cursorBattery.isClosed())
                    cursorBattery.close();
            }
            //Light


            if (!light_cache&&intent.getAction().equals(Light.ACTION_AWARE_LIGHT)) {

                ContentValues light_data = (ContentValues) intent.getParcelableExtra(Light.EXTRA_DATA);

                //Log.d("SENSORS", "LI_VALUES_0=" + light_level);
                if (light_data != null) {
                    Log.d("SENSORS", "Light sensor AVAILABLE");
                    light_level = light_data.getAsDouble("double_light_lux");
                } else {
                    Log.d("SENSORS", "Light sensor UNAVAILABLE");
                    light_level = 0;
                }
                light_cache = true;
                //Log.d("SENSORS", "LI_VALUES_0=" + light_level);
            }


            if(!GPS_cache)
            {
                Cursor cursorLocations = context.getContentResolver().query(Locations_Provider.Locations_Data.CONTENT_URI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
                if (cursorLocations != null && cursorLocations.moveToFirst()) {
                    GPSlatitude = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
                    GPSlongitude = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));
                    GPSbearing = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.BEARING));
                    GPSspeed = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.SPEED));
                    GPSaltitude = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.ALTITUDE));
                    GPS_cache = true;
                    //Log.d("SENSORS", "GPSlatitude=" + GPSlatitude);
                    //Log.d("SENSORS", "GPSlongitude=" + GPSlongitude);
                    ////Log.d("SENSORS", "GPSbearing=" + GPSbearing);
                    //Log.d("SENSORS", "GPSspeed=" + GPSspeed);
                   //// Log.d("SENSORS", "GPSaltitude=" + GPSaltitude);
                }
                else
                {
                    //Log.d("SENSORS", "GPSNAIVE!");
                    GPS_cache = true;
                }
                if (cursorLocations != null && !cursorLocations.isClosed())
                    cursorLocations.close();
            }


            //add time here
            if(!time_cache) {
                hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                //Log.d("SENSORS", "Hour=" + hour);

                double weekDay = -1;

                Calendar c = Calendar.getInstance();
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                if (Calendar.MONDAY == dayOfWeek) {
                    weekDay = 1;
                } else if (Calendar.TUESDAY == dayOfWeek) {
                    weekDay = 2;
                } else if (Calendar.WEDNESDAY == dayOfWeek) {
                    weekDay = 3;
                } else if (Calendar.THURSDAY == dayOfWeek) {
                    weekDay = 4;
                } else if (Calendar.FRIDAY == dayOfWeek) {
                    weekDay = 5;
                } else if (Calendar.SATURDAY == dayOfWeek) {
                    weekDay = 6;
                } else if (Calendar.SUNDAY == dayOfWeek) {
                    weekDay = 7;
                }

                //Log.d("SENSORS", "weekDay=" + weekDay);
                day = weekDay;
                time_cache=true;
            }


          /*
          app_cache=false;
          scr_cache=false;
          acc_cache = false;
          //bar_cache = false;
          battery_cache=false;*/
            // If I have a new sample in all sensors do a cache => synchronize to the slowest sensor
            if(app_cache&&scr_cache&&acc_cache&&battery_cache&&light_cache&&GPS_cache&&time_cache)
            {
                Log.d("SENSORS", "data your mom");
                app_cache=false;
                scr_cache=false;
                acc_cache = false;
                //bar_cache = false;
                battery_cache=false;
                light_cache=false;
                GPS_cache=false;
                time_cache=false;
                if(foreground_package.equals(""))
                {
                    return;
                }

              ContentValues data = new ContentValues();
              data.put(Provider.Toad_Unlock_Data.TIMESTAMP, System.currentTimeMillis());
              data.put(Provider.Toad_Unlock_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
              data.put(Provider.Toad_Unlock_Data.A_VALUES_0, acc_x);
              data.put(Provider.Toad_Unlock_Data.A_VALUES_1, acc_y);
              data.put(Provider.Toad_Unlock_Data.A_VALUES_2, acc_z);
              //data.put(Provider.Toad_Unlock_Data.B_VALUES_0, bar);
              data.put(Provider.Toad_Unlock_Data.BL_VALUES_0, battery_level);
              data.put(Provider.Toad_Unlock_Data.BS_VALUES_0, battery_status);


                data.put(Provider.Toad_Unlock_Data.LUX, light_level);
                data.put(Provider.Toad_Unlock_Data.LATITUDE, GPSlatitude);
                data.put(Provider.Toad_Unlock_Data.LONGITUDE, GPSlongitude);
                data.put(Provider.Toad_Unlock_Data.BEARING, GPSbearing);
                data.put(Provider.Toad_Unlock_Data.SPEED, GPSspeed);
                data.put(Provider.Toad_Unlock_Data.ALTITUDE, GPSaltitude);
                data.put(Provider.Toad_Unlock_Data.HOUR, hour);
                data.put(Provider.Toad_Unlock_Data.DAY, day);

              data.put(Provider.Toad_Unlock_Data.LABEL, foreground_package);
              getContentResolver().insert(Provider.Toad_Unlock_Data.CONTENT_URI, data);

          }
        }
    }

}