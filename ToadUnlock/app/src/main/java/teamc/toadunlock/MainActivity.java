package teamc.toadunlock;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Calendar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.multidex.MultiDex;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Light;
import com.aware.providers.Accelerometer_Provider;
import com.aware.providers.Battery_Provider;
import com.aware.providers.Locations_Provider;
import com.aware.utils.Aware_Plugin;

import java.util.List;
import java.util.Date;

import weka.core.Attribute;
import java.util.ArrayList;
import weka.core.Instances;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.DenseInstance;

public class MainActivity extends Activity {
//train set number
    final static int size=40;
// app set
List<AppInfo> list;

    //UI design

    public ImageView ImageView1;
    public ImageView ImageView2;
    public ImageView ImageView3;
    public ImageView ImageView4;
    public ImageView ImageView5;
    public ImageView ImageView6;
    public TextView TextView1;
    public TextView Time;
    public TextView Date;

    public AppInfo info1;
    public AppInfo info2;
    public AppInfo info3;
    public AppInfo info4;
    public AppInfo info5;
    public AppInfo info6;
    private String mDateFormat;
    private Calendar mCalendar;
    private String mFormat;

    public boolean isRun=true;
    public int prediction=0;

    //sensor on-the-fly
    //Accelerometer
    public static double acc_x=0;
    public static double acc_y=0;
    public static double acc_z=10;

    //light
    public static double light_level=1000;


    //Battery
    public static double battery_level=100;
    public static double battery_status=3;

    //GPS
    public static double GPSlatitude=0;
    public static double GPSlongitude=0;
    public static double GPSbearing=0;
    public static double GPSspeed=0;
    public static double GPSaltitude=0;

    //time
    public static double hour=12;
    public static double day=1;
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }
    public Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (isRun) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Date.setText(DateFormat.format(mDateFormat, new Date()));
                            mCalendar.setTimeInMillis(System.currentTimeMillis());
                            mFormat = "kk:mm";
                            CharSequence newTime = DateFormat.format(mFormat, mCalendar);
                            Time.setText(newTime);
                        }
                    });

                    //container of training data
                    ArrayList<Attribute> atts = new ArrayList<Attribute>(14);
                    ArrayList<String> classVal = new ArrayList<String>();

                    double[][] instanceValue1 = new double[size][14];



                    //sensor data
                    Log.d("Toad", "Act 50");
                    int rows_of_data_ear = 0;

                    Cursor cursor_app = getContentResolver().query(Provider.Toad_Unlock_Data.CONTENT_URI, null, null, null, Provider.Toad_Unlock_Data.TIMESTAMP + " DESC LIMIT "+size);
                    if (cursor_app != null && cursor_app.moveToFirst()) {
                        int i = 0;
                        do {
                            double DB_acc_x = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.A_VALUES_0));
                            double DB_acc_y = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.A_VALUES_1));
                            double DB_acc_z = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.A_VALUES_2));
                            double DB_battery_level = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.BL_VALUES_0));
                            double DB_battery_status = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.BS_VALUES_0));
                            double DB_light_level = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.LUX));
                            double DB_GPSlatitude = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.LATITUDE));
                            double DB_GPSlongitude = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.LONGITUDE));
                            double DB_GPSbearing = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.BEARING));
                            double DB_GPSspeed = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.SPEED));
                            double DB_GPSaltitude = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.ALTITUDE));
                            double DB_hour = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.HOUR));
                            double DB_day = cursor_app.getDouble(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.DAY));
                            String package_name = cursor_app.getString(cursor_app.getColumnIndex(Provider.Toad_Unlock_Data.LABEL));

                            //add package_name into classes

                            if(!classVal.contains(package_name)) {
                                classVal.add(package_name);
                            }
/*
                            Log.d("QUERY", "acc_x=" + DB_acc_x);
                            Log.d("QUERY", "acc_y=" + DB_acc_y);
                            Log.d("QUERY", "acc_z=" + DB_acc_z);
                            Log.d("QUERY", "battery_level=" + DB_battery_level);
                            Log.d("QUERY", "battery_status=" + DB_battery_status);
                            Log.d("QUERY", "light_level=" + DB_light_level);
                            Log.d("QUERY", "GPSlatitude=" + DB_GPSlatitude);
                            Log.d("QUERY", "GPSlongitude=" + DB_GPSlongitude);
                            Log.d("QUERY", "GPSbearing=" + DB_GPSbearing);
                            Log.d("QUERY", "GPSspeed=" + DB_GPSspeed);
                            Log.d("QUERY", "GPSaltitude=" + DB_GPSaltitude);
                            Log.d("QUERY", "hour=" + DB_hour);
                            Log.d("QUERY", "day=" + DB_day);
                            Log.d("QUERY", "package_name=" + package_name);
                            */
                            instanceValue1[i][0]=DB_acc_x;
                            instanceValue1[i][1]=DB_acc_y;
                            instanceValue1[i][2]=DB_acc_z;
                            instanceValue1[i][3]=DB_battery_level;
                            instanceValue1[i][4]=DB_battery_status;
                            instanceValue1[i][5]=DB_light_level;
                            instanceValue1[i][6]=DB_GPSlatitude;
                            instanceValue1[i][7]=DB_GPSlongitude;
                            instanceValue1[i][8]=DB_GPSbearing;
                            instanceValue1[i][9]=DB_GPSspeed;
                            instanceValue1[i][10]=DB_GPSaltitude;
                            instanceValue1[i][11]=DB_hour;
                            instanceValue1[i][12]=DB_day;
                            instanceValue1[i][13]=classVal.indexOf(package_name);
                            i++;
                            rows_of_data_ear++;
                            if (i > size-1) {
                                break;
                            }
                        }
                        while (cursor_app.moveToNext());
                    }
                    if (cursor_app != null && !cursor_app.isClosed()) {
                        Log.d("SENSORS_DATA_ear", "Saved data: " + rows_of_data_ear);
                        cursor_app.close();
                    }
                    boolean go = true;
                    if (rows_of_data_ear < size) //don't go learning, not enough data size rows
                    {
                        go = false;
                        //break;
                    }

                    if (go) {
                        //get current sensor data
                        Cursor cursorBattery = getApplicationContext().getContentResolver().query(Battery_Provider.Battery_Data.CONTENT_URI, null, null, null, Battery_Provider.Battery_Data.TIMESTAMP + " DESC LIMIT 1");
                        if (cursorBattery != null && cursorBattery.moveToFirst()) {
                            battery_level = cursorBattery.getDouble(cursorBattery.getColumnIndex(Battery_Provider.Battery_Data.LEVEL));
                            battery_status = cursorBattery.getDouble(cursorBattery.getColumnIndex(Battery_Provider.Battery_Data.STATUS));
                            //Log.d("SENSORS", "BL_VALUES_0=" + battery_level);
                            //Log.d("SENSORS", "BS_VALUES_0=" + battery_status);
                        }
                        if (cursorBattery != null && !cursorBattery.isClosed())
                            cursorBattery.close();
                        Cursor cursorLocations = getApplicationContext().getContentResolver().query(Locations_Provider.Locations_Data.CONTENT_URI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
                        if (cursorLocations != null && cursorLocations.moveToFirst()) {
                            GPSlatitude = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
                            GPSlongitude = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));
                            GPSbearing = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.BEARING));
                            GPSspeed = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.SPEED));
                            GPSaltitude = cursorLocations.getDouble(cursorLocations.getColumnIndex(Locations_Provider.Locations_Data.ALTITUDE));

                        }
                        if (cursorLocations != null && !cursorLocations.isClosed())
                            cursorLocations.close();
                        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);


                        double weekDay = 1;

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

                        day = weekDay;
/*
                        Log.d("SENSORS", "A_VALUES_0=" + acc_x);
                        Log.d("SENSORS", "LI_VALUES_0=" + light_level);
                        Log.d("SENSORS", "BL_VALUES_0=" + battery_level);
                        Log.d("SENSORS", "BS_VALUES_0=" + battery_status);
                        Log.d("SENSORS", "GPSlatitude=" + GPSlatitude);
                        Log.d("SENSORS", "GPSlongitude=" + GPSlongitude);
                        Log.d("SENSORS", "GPSbearing=" + GPSbearing);
                        Log.d("SENSORS", "GPSspeed=" + GPSspeed);
                        Log.d("SENSORS", "GPSaltitude=" + GPSaltitude);
                        Log.d("SENSORS", "Hour=" + hour);
                        Log.d("SENSORS", "weekDay=" + day);
*/
                        //get package name into numbers


                        //train model

                        atts.add(new Attribute("1"));
                        atts.add(new Attribute("2"));
                        atts.add(new Attribute("3"));
                        atts.add(new Attribute("4"));
                        atts.add(new Attribute("5"));
                        atts.add(new Attribute("6"));
                        atts.add(new Attribute("7"));
                        atts.add(new Attribute("8"));
                        atts.add(new Attribute("9"));
                        atts.add(new Attribute("10"));
                        atts.add(new Attribute("11"));
                        atts.add(new Attribute("12"));
                        atts.add(new Attribute("13"));
                        atts.add(new Attribute("class", classVal));
                        Instances dataRaw = new Instances("TestInstances", atts, 0);

                        for(int i=0;i<size;i++) {
                            dataRaw.add(new DenseInstance(1.0, instanceValue1[i]));
                        }
                        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);

                        NaiveBayes model=new NaiveBayes();

                        model.buildClassifier(dataRaw);   // build classifier

                        Instances dataRaw2 = new Instances("EvalInstances", atts, 0);

                        double[] instanceValue3 = new double[dataRaw2.numAttributes()];

                        instanceValue3[0]=acc_x;
                        instanceValue3[1]=acc_y;
                        instanceValue3[2]=acc_z;
                        instanceValue3[3]=battery_level;
                        instanceValue3[4]=battery_status;
                        instanceValue3[5]=light_level;
                        instanceValue3[6]=GPSlatitude;
                        instanceValue3[7]=GPSlongitude;
                        instanceValue3[8]=GPSbearing;
                        instanceValue3[9]=GPSspeed;
                        instanceValue3[10]=GPSaltitude;
                        instanceValue3[11]=hour;
                        instanceValue3[12]=day;

                        dataRaw2.add(new DenseInstance(1.0, instanceValue3));
                        dataRaw2.setClassIndex(dataRaw2.numAttributes() - 1);

                        //then predict

                        double[] x2 = model.distributionForInstance(dataRaw2.instance(0));

                        //then paint them in views
                        double first=-1;
                        int firstIndex=0;
                        for(int i=0;i<classVal.size();i++) {
                            if(x2[i]>first)
                            {
                                first=x2[i];
                                firstIndex=i;
                            }
                        }
                        x2[firstIndex]=-1;
                        Log.d("NAIVE", "1="+classVal.get(firstIndex));
                        double second=-1;
                        int secondIndex=firstIndex;
                        for(int i=0;i<classVal.size();i++) {
                            if(x2[i]>second)
                            {
                                second=x2[i];
                                secondIndex=i;
                            }
                        }
                        x2[secondIndex]=-1;
                        Log.d("NAIVE", "2="+classVal.get(secondIndex));
                        double third=-1;
                        int thirdIndex=firstIndex;
                        for(int i=0;i<classVal.size();i++) {
                            if(x2[i]>third)
                            {
                                third=x2[i];
                                thirdIndex=i;
                            }
                        }
                        x2[thirdIndex]=-1;
                        Log.d("NAIVE", "3="+classVal.get(thirdIndex));
                        double fourth=-1;
                        int fourthIndex=firstIndex;
                        for(int i=0;i<classVal.size();i++) {
                            if(x2[i]>fourth)
                            {
                                fourth=x2[i];
                                fourthIndex=i;
                            }
                        }
                        x2[fourthIndex]=-1;
                        Log.d("NAIVE", "4="+classVal.get(fourthIndex));
                        double fifth=-1;
                        int fifthIndex=firstIndex;
                        for(int i=0;i<classVal.size();i++) {
                            if(x2[i]>fifth)
                            {
                                fifth=x2[i];
                                fifthIndex=i;
                            }
                        }
                        x2[fifthIndex]=-1;
                        Log.d("NAIVE", "5="+classVal.get(fifthIndex));
                        double six=-1;
                        int sixIndex=firstIndex;
                        for(int i=0;i<classVal.size();i++) {
                            if(x2[i]>six)
                            {
                                six=x2[i];
                                sixIndex=i;
                            }
                        }
                        x2[sixIndex]=-1;
                        Log.d("NAIVE", "6="+classVal.get(sixIndex));

                        for(int i=0;i<list.size();i++) {
                            if(list.get(i).getPackageName().equals(classVal.get(firstIndex)))
                            {
                                info1 = list.get(i);
                                ImageView1.setImageDrawable(info1.getIcon());
                            }
                            if(list.get(i).getPackageName().equals(classVal.get(secondIndex)))
                            {
                                info2 = list.get(i);
                                ImageView2.setImageDrawable(info2.getIcon());
                            }
                            if(list.get(i).getPackageName().equals(classVal.get(thirdIndex)))
                            {
                                info3 = list.get(i);
                                ImageView3.setImageDrawable(info3.getIcon());
                            }
                            if(list.get(i).getPackageName().equals(classVal.get(fourthIndex)))
                            {
                                info4 = list.get(i);
                                ImageView4.setImageDrawable(info4.getIcon());
                            }
                            if(list.get(i).getPackageName().equals(classVal.get(fifthIndex)))
                            {
                                info5 = list.get(i);
                                ImageView5.setImageDrawable(info5.getIcon());
                            }
                            if(list.get(i).getPackageName().equals(classVal.get(sixIndex)))
                            {
                                info6 = list.get(i);
                                ImageView6.setImageDrawable(info6.getIcon());
                            }
                        }




                        prediction = 4000;
                        //log accuracy
                    }

                    try {
                        Thread.currentThread().sleep(1000 + prediction);
                        Thread.currentThread().yield();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch(Exception e) {

            }



        }
    });
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        final int TextView1X1 = TextView1.getLeft();
        final int TextView1Y1 = TextView1.getTop();
        TextView1.setOnTouchListener(new View.OnTouchListener() {
            float downx = -1;
            float downy = -1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {  //& MotionEvent.ACTION_MASK
                    case MotionEvent.ACTION_MOVE:

                        TextView1.setX(event.getX());
                        TextView1.setY(event.getY());
                        break;

                    case MotionEvent.ACTION_DOWN:
                        Log.d("Toad", "Act 55");
                        downx = event.getX();
                        downy = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Toad", "Act 59");
                        float distance = (event.getX() - downx) * (event.getX() - downx) + (event.getY() - downy) * (event.getY() - downy);
                        TextView1.setX(TextView1X1);
                        TextView1.setY(TextView1Y1);
                        if (distance > 4900) {
                            if(prediction>0) {
                                //wrong+1
                                ContentValues data = new ContentValues();
                                data.put(Provider.Toad_Unlock_Data2.TIMESTAMP, System.currentTimeMillis());
                                data.put(Provider.Toad_Unlock_Data2.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                                data.put(Provider.Toad_Unlock_Data2.WRONG, 1);
                                data.put(Provider.Toad_Unlock_Data2.RIGHT, 0);
                                getContentResolver().insert(Provider.Toad_Unlock_Data2.CONTENT_URI, data);
                            }
                            //naive
                            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                            keyguardLock.disableKeyguard();
                            finish();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("Toad", "Act 62");
                        break;
                }
                return true;
            }
        });
        final int ImageView1Left = ImageView1.getLeft();
        final int ImageView1Top = ImageView1.getTop();
        ImageView1.setOnTouchListener(new View.OnTouchListener() {
            float downx = -1;
            float downy = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {  //& MotionEvent.ACTION_MASK
                    case MotionEvent.ACTION_MOVE:
                        ImageView1.setX(event.getX());
                        ImageView1.setY(event.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Toad", "Act 55");
                        downx = event.getX();
                        downy = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Toad", "Act 59");
                        float distance = (event.getX() - downx) * (event.getX() - downx) + (event.getY() - downy) * (event.getY() - downy);
                        ImageView1.setX(ImageView1Left);
                        ImageView1.setY(ImageView1Top);
                        if (distance > 4900) {
                            if(prediction>0) {//right+1
                                ContentValues data = new ContentValues();
                                data.put(Provider.Toad_Unlock_Data2.TIMESTAMP, System.currentTimeMillis());
                                data.put(Provider.Toad_Unlock_Data2.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                                data.put(Provider.Toad_Unlock_Data2.WRONG, 0);
                                data.put(Provider.Toad_Unlock_Data2.RIGHT, 1);
                                getContentResolver().insert(Provider.Toad_Unlock_Data2.CONTENT_URI, data);
                            }
                            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                            keyguardLock.disableKeyguard();
                            PackageManager cjj = getPackageManager();
                            Intent LaunchIntent = cjj.getLaunchIntentForPackage(info1.getPackageName());
                            if (LaunchIntent == null) {
                                Log.d("Toad", "Act 114");
                                break;
                            }
                            startActivity(LaunchIntent);

                            finish();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("Toad", "Act 62");
                        break;
                }
                return true;
            }
        });
        final int ImageView2Left = ImageView2.getLeft();
        final int ImageView2Top = ImageView2.getTop();
        ImageView2.setOnTouchListener(new View.OnTouchListener() {
            float downx = -1;
            float downy = -1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {  //& MotionEvent.ACTION_MASK
                    case MotionEvent.ACTION_MOVE:
                        ImageView2.setX(event.getX());
                        ImageView2.setY(event.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Toad", "Act 55");
                        downx=event.getX();
                        downy=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Toad", "Act 59");
                        float distance = (event.getX()-downx)*(event.getX()-downx)+(event.getY()-downy)*(event.getY()-downy);
                        ImageView2.setX(ImageView2Left);
                        ImageView2.setY(ImageView2Top);
                        if(distance>4900)
                        {
                            if(prediction>0) {//right+1
                                ContentValues data = new ContentValues();
                                data.put(Provider.Toad_Unlock_Data2.TIMESTAMP, System.currentTimeMillis());
                                data.put(Provider.Toad_Unlock_Data2.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                                data.put(Provider.Toad_Unlock_Data2.WRONG, 0);
                                data.put(Provider.Toad_Unlock_Data2.RIGHT, 1);
                                getContentResolver().insert(Provider.Toad_Unlock_Data2.CONTENT_URI, data);
                            }
                            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                            keyguardLock.disableKeyguard();
                            PackageManager cjj = getPackageManager();
                            Intent LaunchIntent = cjj.getLaunchIntentForPackage(info2.getPackageName());
                            if(LaunchIntent==null)
                            {
                                Log.d("Toad", "Act 159");
                                break;
                            }
                            startActivity(LaunchIntent);
                            finish();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("Toad", "Act 62");
                        break;
                }
                return true;
            }
        });
        final int ImageView3Left = ImageView3.getLeft();
        final int ImageView3Top = ImageView3.getTop();
        ImageView3.setOnTouchListener(new View.OnTouchListener() {
            float downx = -1;
            float downy = -1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {  //& MotionEvent.ACTION_MASK
                    case MotionEvent.ACTION_MOVE:
                        ImageView3.setX(event.getX());
                        ImageView3.setY(event.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Toad", "Act 55");
                        downx=event.getX();
                        downy=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Toad", "Act 59");
                        float distance = (event.getX()-downx)*(event.getX()-downx)+(event.getY()-downy)*(event.getY()-downy);
                        ImageView3.setX(ImageView3Left);
                        ImageView3.setY(ImageView3Top);
                        if(distance>4900)
                        {
                            if(prediction>0) {//right+1
                                ContentValues data = new ContentValues();
                                data.put(Provider.Toad_Unlock_Data2.TIMESTAMP, System.currentTimeMillis());
                                data.put(Provider.Toad_Unlock_Data2.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                                data.put(Provider.Toad_Unlock_Data2.WRONG, 0);
                                data.put(Provider.Toad_Unlock_Data2.RIGHT, 1);
                                getContentResolver().insert(Provider.Toad_Unlock_Data2.CONTENT_URI, data);
                            }
                            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                            keyguardLock.disableKeyguard();
                            PackageManager cjj = getPackageManager();
                            Intent LaunchIntent = cjj.getLaunchIntentForPackage(info3.getPackageName());
                            if(LaunchIntent==null)
                            {
                                Log.d("Toad", "Act 204");
                                break;
                            }
                            startActivity(LaunchIntent);
                            finish();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("Toad", "Act 62");
                        break;
                }
                return true;
            }
        });
        final int ImageView4Left = ImageView4.getLeft();
        final int ImageView4Top = ImageView4.getTop();
        ImageView4.setOnTouchListener(new View.OnTouchListener() {
            float downx = -1;
            float downy = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {  //& MotionEvent.ACTION_MASK
                    case MotionEvent.ACTION_MOVE:
                        ImageView4.setX(event.getX());
                        ImageView4.setY(event.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Toad", "Act 55");
                        downx = event.getX();
                        downy = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Toad", "Act 59");
                        float distance = (event.getX() - downx) * (event.getX() - downx) + (event.getY() - downy) * (event.getY() - downy);
                        ImageView4.setX(ImageView4Left);
                        ImageView4.setY(ImageView4Top);
                        if (distance > 4900) {
                            if(prediction>0) {//right+1
                                ContentValues data = new ContentValues();
                                data.put(Provider.Toad_Unlock_Data2.TIMESTAMP, System.currentTimeMillis());
                                data.put(Provider.Toad_Unlock_Data2.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                                data.put(Provider.Toad_Unlock_Data2.WRONG, 0);
                                data.put(Provider.Toad_Unlock_Data2.RIGHT, 1);
                                getContentResolver().insert(Provider.Toad_Unlock_Data2.CONTENT_URI, data);
                            }
                            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                            keyguardLock.disableKeyguard();
                            PackageManager cjj = getPackageManager();
                            Intent LaunchIntent = cjj.getLaunchIntentForPackage(info4.getPackageName());
                            if (LaunchIntent == null) {
                                Log.d("Toad", "Act 249");
                                break;
                            }
                            startActivity(LaunchIntent);
                            finish();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("Toad", "Act 62");
                        break;
                }
                return true;
            }
        });
        final int ImageView5Left = ImageView5.getLeft();
        final int ImageView5Top = ImageView5.getTop();
        ImageView5.setOnTouchListener(new View.OnTouchListener() {
            float downx = -1;
            float downy = -1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {  //& MotionEvent.ACTION_MASK
                    case MotionEvent.ACTION_MOVE:
                        ImageView5.setX(event.getX());
                        ImageView5.setY(event.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Toad", "Act 55");
                        downx=event.getX();
                        downy=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Toad", "Act 59");
                        float distance = (event.getX()-downx)*(event.getX()-downx)+(event.getY()-downy)*(event.getY()-downy);
                        ImageView5.setX(ImageView5Left);
                        ImageView5.setY(ImageView5Top);
                        if(distance>4900)
                        {
                            if(prediction>0) {//right+1
                                ContentValues data = new ContentValues();
                                data.put(Provider.Toad_Unlock_Data2.TIMESTAMP, System.currentTimeMillis());
                                data.put(Provider.Toad_Unlock_Data2.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                                data.put(Provider.Toad_Unlock_Data2.WRONG, 0);
                                data.put(Provider.Toad_Unlock_Data2.RIGHT, 1);
                                getContentResolver().insert(Provider.Toad_Unlock_Data2.CONTENT_URI, data);
                            }
                            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                            keyguardLock.disableKeyguard();
                            PackageManager cjj = getPackageManager();
                            Intent LaunchIntent = cjj.getLaunchIntentForPackage(info5.getPackageName());
                            if(LaunchIntent==null)
                            {
                                Log.d("Toad", "Act 294");
                                break;
                            }
                            startActivity(LaunchIntent);
                            finish();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("Toad", "Act 62");
                        break;
                }
                return true;
            }
        });
        final int ImageView6Left = ImageView6.getLeft();
        final int ImageView6Top = ImageView6.getTop();
        ImageView6.setOnTouchListener(new View.OnTouchListener() {
            float downx = -1;
            float downy = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {  //& MotionEvent.ACTION_MASK
                    case MotionEvent.ACTION_MOVE:
                        ImageView6.setX(event.getX());
                        ImageView6.setY(event.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Toad", "Act 55");
                        downx = event.getX();
                        downy = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Toad", "Act 59");
                        float distance = (event.getX() - downx) * (event.getX() - downx) + (event.getY() - downy) * (event.getY() - downy);
                        ImageView6.setX(ImageView6Left);
                        ImageView6.setY(ImageView6Top);
                        if (distance > 4900) {
                            if(prediction>0) {//right+1
                                ContentValues data = new ContentValues();
                                data.put(Provider.Toad_Unlock_Data2.TIMESTAMP, System.currentTimeMillis());
                                data.put(Provider.Toad_Unlock_Data2.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                                data.put(Provider.Toad_Unlock_Data2.WRONG, 0);
                                data.put(Provider.Toad_Unlock_Data2.RIGHT, 1);
                                getContentResolver().insert(Provider.Toad_Unlock_Data2.CONTENT_URI, data);
                            }
                            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                            keyguardLock.disableKeyguard();
                            PackageManager cjj = getPackageManager();
                            Intent LaunchIntent = cjj.getLaunchIntentForPackage(info6.getPackageName());
                            if (LaunchIntent == null) {
                                Log.d("Toad", "Act 339");
                                break;
                            }
                            startActivity(LaunchIntent);
                            finish();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("Toad", "Act 62");
                        break;
                }
                return true;
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Toad", "Act 15");
        startService(new Intent(MainActivity.this, UnlockService.class));
        Log.d("Toad", "Act 18");
        mCalendar = Calendar.getInstance();
        Time = (TextView) findViewById(R.id.time);
        Date = (TextView) findViewById(R.id.date);
        mDateFormat= getString(R.string.date_style);
        AppInfoProvider provider = new AppInfoProvider(MainActivity.this);

        list = provider.getAllApps();

        TextView1 = (TextView) findViewById(R.id.unlock);

        info1 = list.get(0);

        ImageView1 = (ImageView) findViewById(R.id.imageView);
        ImageView1.setImageDrawable(info1.getIcon());

        info2 = list.get(1);

        ImageView2 = (ImageView) findViewById(R.id.imageView2);
        ImageView2.setImageDrawable(info2.getIcon());

        info3 = list.get(2);

        ImageView3 = (ImageView) findViewById(R.id.imageView3);
        ImageView3.setImageDrawable(info3.getIcon());

        info4 = list.get(3);

        ImageView4 = (ImageView) findViewById(R.id.imageView4);
        ImageView4.setImageDrawable(info4.getIcon());

        info5 = list.get(4);

        ImageView5 = (ImageView) findViewById(R.id.imageView5);
        ImageView5.setImageDrawable(info5.getIcon());

        info6 = list.get(5);

        ImageView6 = (ImageView) findViewById(R.id.imageView6);
        ImageView6.setImageDrawable(info6.getIcon());


        isRun = true;
        thread.start();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        isRun=true;

        Log.d("Toad", "Act 46");


        //get aware data
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, true);
        //locus
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_GPS,true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_NETWORK,true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER);
        filter.addAction(Light.ACTION_AWARE_LIGHT);
        registerReceiver(contextBR, filter);
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }


    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        isRun=true;

        Log.d("Toad", "Act onPause");


        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_GPS,false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_NETWORK,false);
        if(contextBR != null)
            unregisterReceiver(contextBR);
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }



    private ContextReceiver contextBR = new ContextReceiver();
    public class ContextReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            //Sensors Data

            //Get the raw data
            //Accelerometer data
            if (intent.getAction().equals(Accelerometer.ACTION_AWARE_ACCELEROMETER)) {
                //Log.d("SENSORS", "Received accelerometer data");
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
            }

            //light
            else if (intent.getAction().equals(Light.ACTION_AWARE_LIGHT)) {
                ContentValues light_data = (ContentValues) intent.getParcelableExtra(Light.EXTRA_DATA);

                //Log.d("SENSORS", "LI_VALUES_0=" + light_level);
                if (light_data != null) {
                    Log.d("SENSORS", "Light sensor AVAILABLE");
                    light_level = light_data.getAsDouble("double_light_lux");
                } else {
                    Log.d("SENSORS", "Light sensor UNAVAILABLE");
                    light_level = 0;
                }

            }



        }
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        isRun=true;


        Log.d("Toad", "Act 426");
    }
    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
        isRun=false;
        Log.d("Toad", "Act 426");
        finish();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("Toad", "Act 539");
        if(event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.d("Toad", "Act 501");
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
