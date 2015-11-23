package it.vvzen.dontcryforbeacon;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by valerioviperino on 21/11/15.
 *
 */
public class Utility {

    /** Method to activate bluetooth on app start */
    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        }
        else if(!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }
}
