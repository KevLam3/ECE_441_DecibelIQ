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
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// -----------------------------
// Firebase
// -----------------------------
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

// -----------------------------
// Java Utilities
// -----------------------------
import java.nio.ByteBuffer
import java.util.UUID


// Helper function to check BLE permissions
private fun hasBlePermissions(context: android.content.Context): Boolean {
    val required = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    return required.all { perm ->
        ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }
}


@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val rawValue = remember { mutableStateOf("Waiting...") }

    // Firebase reference
    val firebaseRef = Firebase.database.getReference("mic/raw")

    // Required permissions for Android 12+
    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {

        if (!hasBlePermissions(context)) {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissions,
                1
            )
            return@LaunchedEffect
        }

        // EVERYTHING BELOW THIS LINE MUST BE INSIDE THIS BLOCK
        // AND NOTHING BLE CAN BE OUTSIDE IT

        val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

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

                                descriptor.value =
                                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE

                                gatt.writeDescriptor(descriptor)
                            }

                            override fun onCharacteristicChanged(
                                gatt: BluetoothGatt,
                                characteristic: BluetoothGattCharacteristic
                            ) {
                                val value = ByteBuffer.wrap(characteristic.value).int

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
