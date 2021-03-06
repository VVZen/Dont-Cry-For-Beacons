package it.vvzen.dontcryforbeacon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.Collection;

public class ListeningActivity extends AppCompatActivity implements BeaconConsumer {


    /** DEBUG */
    private static final String TAG = ListeningActivity.class.getSimpleName();

    private Context context;

    /** Beacons */
    private BeaconManager mBeaconManager;
    private RegionBootstrap regionBootstrap;
    private Identifier myEddystoneNamespaceId;
    private final static double STATE1A_TRIGGER_DISTANCE = 4.0d;

    /** A Beacon Region is a way to set a matching pattern of what beacons you are interested in.
     * By creating an "all-beacons-region" with null values for all identifiers,
     * this tells the library that we want to know about any beacon we see. */
    private final Region mRegion = new Region("all-beacons-region", null, null, null);

    private boolean userHasEnteredRange = false;
    private boolean userHasExitedRange = false;

    /** UI */
    private TextView tv_titleDescription, tv_distanceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener);

        context = ListeningActivity.this;

        if(MyApp.isInDebug){
            Log.e(TAG, "App started");
        }

        // Assigning ui vars to xml
        tv_titleDescription = (TextView) this.findViewById(R.id.tv_greeter);
        TextView tv_listenerMessage = (TextView) this.findViewById(R.id.tv_listener_message);
        tv_distanceInfo = (TextView) this.findViewById(R.id.tv_distance_info);

        // Setting typeface
        tv_titleDescription.setTypeface(Fonts.getEBGaramondSC08Regular(context));
        tv_listenerMessage.setText(Html.fromHtml(getString(R.string.listener_message)));
        tv_listenerMessage.setTypeface(Fonts.getEBGaramond12Regular(context));
        tv_distanceInfo.setTypeface(Fonts.getEBGaramond12Regular(context));

        Utility.setBluetooth(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBeaconManager.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBeaconManager = BeaconManager.getInstanceForApplication(context);
        BeaconManager.setDebug(true);
        mBeaconManager.getBeaconParsers().clear();


        /**
         * Four prefixes are allowed in the setBeaconLayout string:

         m - matching byte sequence for this beacon type to parse (exactly one required)
         s - ServiceUuid for this beacon type to parse (optional, only for Gatt-based becons)
         i - identifier (at least one required, multiple allowed)
         p - power calibration field (exactly one required)
         d - data field (optional, multiple allowed)
         *
         *  Example: m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25
         * */


        // The setBeaconLayout method will tell the Android Beacon Library how to decode an Eddystone UID frame.
        // You don't need to understand the layout string --
        // just know that his defines the format of the frame to the beacon parser

        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));

        // Detect the main Eddystone-UID frame
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        // Detect the Telemetry (TLM) frame
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        // Detect the URL frame
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        // The last line of the method binds the app to the library's beacon scanning service,
        // so it can start looking for beacons.
        mBeaconManager.bind(this);
        //to try: mBeaconManager.bind((BeaconConsumer) context);

    }

    @Override
    public void onBeaconServiceConnect() {


        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                if(MyApp.isInDebug){
                    Log.e(TAG, "I just saw an beacon for the first time!");
                }
            }

            @Override
            public void didExitRegion(Region region) {
                if(MyApp.isInDebug){
                    Log.e(TAG, "I no longer see a beacon");
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        // Update the tv_distanceInfo with the current distance
                        tv_distanceInfo.setText("ND");
                        //Toast.makeText(context, "SEI VICINO AL BEACON OUTDOOR!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                if(MyApp.isInDebug){
                    Log.e(TAG, "I have just switched from seeing/not seeing beacons: "+state);
                }
            }
        });

        try {
            mBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            if(MyApp.isInDebug){
                Log.e(TAG, "Catched RemoteException: "+ e.getLocalizedMessage());
            }
        }

        // The startRangingBeaconsInRegion method tells the library
        // to start looking for beacons that match this region definition
        try {
            if(MyApp.isInDebug){
                Log.e(TAG, "Started To Range beacons in region");
            }
            mBeaconManager.startRangingBeaconsInRegion(mRegion);
        } catch (RemoteException e) {

            if(MyApp.isInDebug){
                Log.e(TAG, "beaconManager Remote Exception: " + e.getLocalizedMessage());
            }
            e.printStackTrace();
        }
        // The last line sets the rangeNotifier to this class,
        // so our the same RangingActivity class will get callbacks each time a beacon is seen.
        // The callback is defined in @didRangeBeaconsInRegion
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
                // if you want the Activity to launch every single time beacons come into view, remove this call.
                //regionBootstrap.disable();

                for (final Beacon beacon : beacons) {

                    if (MyApp.isInDebug) {
                        /**
                        Log.e(TAG, "BEACONS INFO" +
                                "\n\tBeacon name " + beacon.getBluetoothName() +
                                "\n\tService UUID: " + beacon.getServiceUuid() +
                                "\n\tBluetooth Address : " + beacon.getBluetoothAddress() +
                                "\n\tDistance from smartphone: " + beacon.getDistance());
                         */
                    }

                    // Check if this is a Eddystone-UID frame
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                        Identifier namespaceId = beacon.getId1();
                        Identifier instanceId = beacon.getId2();

                        if (beacon.getBluetoothAddress().equals(Constants.Beacons.OUTDOOR_BEACON_ADDRESS)) {

                            if (MyApp.isInDebug) {
                                Log.e(TAG, "BEACON OUTDOOR" +
                                        "\n\tDistance: " + beacon.getDistance());
                            }

                            // Notify user of distance to beacon
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // Update the tv_distanceInfo with the current distance
                                    tv_distanceInfo.setText(Double.toString(beacon.getDistance()).substring(0, 5) + " m");
                                    //Toast.makeText(context, "SEI VICINO AL BEACON OUTDOOR!", Toast.LENGTH_LONG).show();
                                }
                            });

                            // Launch state1 only if we're quite close to the beacon
                            if(beacon.getDistance() < STATE1A_TRIGGER_DISTANCE){
                                Intent intent = new Intent(context, State1AActivity.class);
                                // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance"
                                // or you will get two instancescreated when a user launches the activity manually and it gets launched from here.
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }

                        if (beacon.getBluetoothAddress().equals(Constants.Beacons.INDOOR_BEACON_ADDRESS)) {

                            if (MyApp.isInDebug) {
                                Log.e(TAG, "BEACON INDOOR");
                            }
                        }
                    }
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                        // This is a Eddystone-URL frame
                        String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                        /**
                        if (MyApp.isInDebug) {
                            Log.e(TAG, "I see a beacon transmitting a url: " + url +
                                    " approximately " + beacon.getDistance() + " meters away.");
                        }
                         */
                    }
                }
            }
        });
    }
}
