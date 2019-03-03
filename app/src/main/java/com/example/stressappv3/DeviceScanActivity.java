package com.example.stressappv3;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends AppCompatActivity {
    private final static String TAG = DeviceScanActivity.class.getSimpleName();
    //private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private ArrayList<BluetoothDevice> mLeDevices;

    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanFilter scanFilter;
    private ScanSettings settings;
    private ArrayList<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private BluetoothManager mBluetoothManager;

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private BluetoothGattServer mGattServer;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 30000;      // Stops scanning after 30 seconds.

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        context = this;
        //getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Your device is not equipped with BLE, and is incompatible with this app.", Toast.LENGTH_LONG).show();
            Log.d("BLE_NOT_SUPPORTED","Ble not supported.");
            finish();
        }

        //check if ACCESS_COARSE_LOCATION is granted by the user.  Either ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION need to be set in order to perform BLE device scanning.
        //I chose the coarse location as a courtesy to the user.  My application does not use/track a user's location in any way, other than the nature of BLE scanning.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(DeviceScanActivity.this, "Bluetooth scanning requires your coarse location permissions.  Please grant this permission when prompted.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        mBluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth adapter setup failed", Toast.LENGTH_SHORT).show();
            Log.d("BT_ADAPTER_FAILED","mBluetoothAdapter = null");
            finish();
            return;
        }else{
            Toast.makeText(this, "Bluetooth adapter retrieved.", Toast.LENGTH_SHORT).show();
            Log.d("BT_ADAPTER_SUCCESS","mBluetoothAdapter != null");
            //get a reference to BluetoothLeScanner through the BluetoothAdapter.
            //using BluetoothLeScanner since BluetoothAdapter.startLeScan() has been deprecated past API 21
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if(mBluetoothLeScanner == null){
                Toast.makeText(this, "BluetoothLeScanner setup failed", Toast.LENGTH_SHORT).show();
                Log.d("BTLESCANNER_FAILED","mBluetoothLeScanner = null");
                finish();
                return;
            }else{
                Toast.makeText(this, "BluetoothLeScanner setup successful", Toast.LENGTH_SHORT).show();
                Log.d("BTLESCANNER_SUCCESS","mBluetoothLeScanner != null");
                //create ScanFilter to select only select devices
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                scanFilter = new ScanFilter.Builder()
                        .setDeviceAddress(getString(R.string.mac_address))
                        //.setServiceUuid(ParcelUuid.fromString(getString(R.string.service_UUID)))
                        .build();
                filters = new ArrayList<ScanFilter>();
                filters.add(scanFilter);
            }
        }

        mLeDevices = new ArrayList<BluetoothDevice>();
        scanLeDevice(true);
        /*if(mLeDevices.get(0) != null){
            Log.d("DeviceControl, OnResume", "Devices available: " + mLeDevices.get(0));
        }else{
            Log.d("DeviceControl, OnResume", "No devices available.");
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        /*
         * Check for advertising support. Not all devices are enabled to advertise
         * Bluetooth LE data.
         */
        if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "No Advertising Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        //mGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);

        // Initializes list view adapter.
        //mLeDeviceListAdapter = new LeDeviceListAdapter();
        //setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
        /*if(mLeDevices != null){
            Log.d("DeviceControl, OnResume", "Devices available: " + mLeDevices.get(0));
        }else{
            Log.d("DeviceControl, OnResume", "No devices available.");
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        //mLeDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            //mBluetoothAdapter.startLeScan(mLeScanCallback);
            /*UUID[] service_UUIDs = new UUID[]{
                    UUID.fromString(getString(R.string.service_UUID))
            };*/
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
            //mBluetoothAdapter.startLeScan(service_UUIDs,mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
            //mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        //invalidateOptionsMenu();
    }

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            //connect to ESP32 gatt service
            //enable scanning = false after connecting
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
            mLeDevices.add(device);
            Log.d("connectToDevice()", "Device " + device.getAddress().toString() + " found.");
            Toast.makeText(context, "Device found: " + device.getAddress().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
            connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    /*
     * Callback handles all incoming requests from GATT clients.
     * From connections to read/write requests.
     */
    /*private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.i(TAG, "onConnectionStateChange "
                    +DeviceProfile.getStatusDescription(status)+" "
                    +DeviceProfile.getStateDescription(newState));

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                postDeviceChange(device, true);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                postDeviceChange(device, false);
            }
        }*/

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //List<BluetoothGattService> services = gatt.getServices();
            BluetoothGattService service = gatt.getService(UUID.fromString(getString(R.string.service_UUID)));
            if(service != null)
            {
                Log.i("onServicesDiscovered", "Service found: " + service.toString());

            }else{
                Log.i("onServicesDiscovered", "Service not found.");
            }
            if(gatt.readCharacteristic(
                    service.getCharacteristic(
                            UUID.fromString(
                                    getString(
                                            R.string.characteristic_tx_UUID))))){
                //uuid from transmit of micro, stored in strings.xml
                Log.i("readCharacteristic", "Characteristic read.");
            }
            /*Log.i("onServicesDiscovered", services.toString());
            gatt.readCharacteristic(services.get(1).getCharacteristics().get
                    (0));*/
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            Toast.makeText(context, "Characteristic Read: " + characteristic.getValue().toString(), Toast.LENGTH_SHORT).show();
            gatt.disconnect();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic) {
            Log.i("onCharacteristicChanged", characteristic.toString());
            Toast.makeText(context, "Characteristic changed: " + characteristic.toString(), Toast.LENGTH_SHORT).show();
            gatt.disconnect();
        }
    };

    // Device scan callback.
    /*private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mLeDeviceListAdapter.addDevice(device);
                            if(!mLeDevices.contains(device))
                            {
                                mLeDevices.add(device);
                                Log.d("onLeScan()", "Device " + device.getAddress().toString() + "found.");
                                Toast.makeText(context, "Device found: " + device.getAddress().toString(), Toast.LENGTH_SHORT).show();
                            }else{
                                mLeDevices.add(device);
                                Log.d("onLeScan()", "Device " + device.getAddress().toString() + "found.");
                                Toast.makeText(context, "Device found: " + device.getAddress().toString(), Toast.LENGTH_SHORT).show();
                            }
                            //mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };*/
}
