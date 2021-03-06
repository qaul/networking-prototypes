package net.qaul.qaulbluemesh;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;


public class DeviceListActivity extends Activity implements View.OnClickListener, ServiceConnection{
    private static final String TAG = "DeviceListActivity";

    private MeshService mBoundService;
    private BluetoothAdapter mBtAdapter;

    private LinearLayout deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        setResult(Activity.RESULT_CANCELED);

        Intent bindIntent = new Intent(this, MeshService.class);

        if (!bindService(bindIntent, this, Context.BIND_AUTO_CREATE)) {
            Toast.makeText(this, "Failed to connect to service", Toast.LENGTH_LONG).show();
        }

        deviceList=(LinearLayout) findViewById(R.id.device_list);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    addDevice(0,device);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(DeviceListActivity.this,"Finished discovery",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void addDevice(int x,BluetoothDevice device){
        TextView tv = new TextView(DeviceListActivity.this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if(x==1)
            tv.setTextColor(Color.BLUE);
        else
            tv.setTextColor(Color.BLACK);
        tv.setText(device.getName());
        tv.setTag(device.getAddress());
        tv.setBackgroundResource(R.drawable.device_back);
        tv.setOnClickListener(DeviceListActivity.this);
        deviceList.addView(tv);

        mBoundService.addDevice(device);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }
    public void doDiscovery(View v) {

        Log.d(TAG, "doDiscovery()");

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
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
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {

            mBoundService.pairWith((String)v.getTag());

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBoundService = ((MeshService.LocalBinder)service).getService();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        for(BluetoothDevice device : pairedDevices){
            addDevice(1,device);
            Log.d("PairedDevice",device.getName());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBoundService = null;
    }
}
