package com.example.ece441project.ui.theme

// -----------------------------
// Jetpack Compose
// -----------------------------
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// -----------------------------
// Android + Bluetooth
// -----------------------------
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// -----------------------------
// Firebase
// -----------------------------
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.DatabaseReference

// -----------------------------
// Java Utilities
// -----------------------------
import java.nio.ByteBuffer
import java.util.UUID


// ---------------------------------------------------------
// Permission Helper (API‑aware)
// ---------------------------------------------------------
private fun hasBlePermissions(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}


// ---------------------------------------------------------
// BLE Logic (Deprecated APIs suppressed where required)
// ---------------------------------------------------------
@SuppressLint("MissingPermission", "DeprecatedBluetoothLeScanner")
private fun startBleScan(
    context: Context,
    firebaseRef: DatabaseReference,
    rawValue: MutableState<String>
) {
    val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val scanner = bluetoothManager.adapter.bluetoothLeScanner

    val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            if (result.device?.name == "ESP32-MIC") {

                scanner.stopScan(this)

                result.device.connectGatt(
                    context,
                    false,
                    object : BluetoothGattCallback() {

                        override fun onConnectionStateChange(
                            gatt: BluetoothGatt,
                            status: Int,
                            newState: Int
                        ) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                gatt.discoverServices()
                            }
                        }

                        override fun onServicesDiscovered(
                            gatt: BluetoothGatt,
                            status: Int
                        ) {
                            val service = gatt.getService(
                                UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
                            )

                            val characteristic = service.getCharacteristic(
                                UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
                            )

                            gatt.setCharacteristicNotification(characteristic, true)

                            val descriptor = characteristic.getDescriptor(
                                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                            )

                            descriptor.setValue(
                                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            )

                            gatt.writeDescriptor(descriptor)
                        }

                        @Suppress("DEPRECATION")
                        override fun onCharacteristicChanged(
                            gatt: BluetoothGatt,
                            characteristic: BluetoothGattCharacteristic
                        ) {
                            val value = ByteBuffer.wrap(characteristic.getValue()).int
                            rawValue.value = value.toString()
                            firebaseRef.setValue(value)
                        }
                    }
                )
            }
        }
    }

    scanner.startScan(scanCallback)
}


// ---------------------------------------------------------
// HomeScreen UI + Permission Handling
// ---------------------------------------------------------
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val rawValue = remember { mutableStateOf("Waiting...") }

    val firebaseRef = Firebase.database.getReference("mic/raw")

    // API‑aware permission list
    val permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

    LaunchedEffect(Unit) {

        // Request permissions if missing
        if (!hasBlePermissions(context)) {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissions,
                1
            )
            return@LaunchedEffect
        }

        // Permissions granted → safe to start BLE
        startBleScan(context, firebaseRef, rawValue)
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Mic Raw Value:", fontSize = 22.sp)
        Text(rawValue.value, fontSize = 60.sp)
    }
}
