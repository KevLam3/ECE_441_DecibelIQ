package com.example.ece441project

// ---------------------------------------------------------
// Android + BLE
// ---------------------------------------------------------
import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission

// ---------------------------------------------------------
// Jetpack Compose State + ViewModel
// ---------------------------------------------------------
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// ---------------------------------------------------------
// Firebase
// ---------------------------------------------------------
import com.google.firebase.database.FirebaseDatabase

// ---------------------------------------------------------
// UUIDs
// ---------------------------------------------------------
import java.util.UUID

// ---------------------------------------------------------
// BLE ViewModel
// ---------------------------------------------------------
class BleViewModel : ViewModel() {

    // -----------------------------
    // UI State
    // -----------------------------
    val spl = mutableStateOf("0.0")
    val laeq = mutableStateOf("0.0")
    val dose = mutableStateOf("0.0")
    val led = mutableStateOf("GREEN")
    val blink = mutableStateOf(false)
    val time24 = mutableStateOf("0.0")
    val safe = mutableStateOf("0.0")

    // -----------------------------
    // BLE Core
    // -----------------------------
    private var gatt: BluetoothGatt? = null
    private var isScanning = false

    private val serviceUUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    private val rawUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
    private val dataUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
    private val cccdUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // -----------------------------
    // Firebase Reference
    // -----------------------------
    private val firebaseSummaryRef =
        FirebaseDatabase.getInstance().getReference("summary")

    // ---------------------------------------------------------
    // Start BLE Scan
    // ---------------------------------------------------------
    @Suppress("MissingPermission")
    fun startScan(context: Context) {
        if (isScanning) return
        isScanning = true

        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val scanner = manager.adapter.bluetoothLeScanner

        scanner.startScan(object : ScanCallback() {
            override fun onScanResult(type: Int, result: ScanResult) {
                if (result.device?.name == "ESP32-MIC") {
                    Log.d("BLE", "Found ESP32-MIC, connecting...")
                    scanner.stopScan(this)
                    connectToDevice(result.device, context)
                }
            }
        })
    }

    // ---------------------------------------------------------
    // Connect to Device
    // ---------------------------------------------------------
    @Suppress("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice, context: Context) {
        gatt = device.connectGatt(context, false, object : BluetoothGattCallback() {

            // -----------------------------
            // Connection State
            // -----------------------------
            override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BLE", "Connected, requesting MTU")
                    g.requestMtu(512)
                }
            }

            // -----------------------------
            // MTU Updated → Discover Services
            // -----------------------------
            override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
                Handler(Looper.getMainLooper()).postDelayed({
                    gatt.discoverServices()
                }, 200)
            }

            // -----------------------------
            // Services Discovered
            // -----------------------------
            override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
                val service = g.getService(serviceUUID)
                if (service == null) {
                    Log.e("BLE", "Service not found")
                    return
                }

                val rawChar = service.getCharacteristic(rawUUID)
                val dataChar = service.getCharacteristic(dataUUID)

                // Enable RAW first
                enableNotifications(g, rawChar)

                // Enable DATA second (Android requires spacing)
                Handler(Looper.getMainLooper()).postDelayed({
                    enableNotifications(g, dataChar)
                }, 350)
            }

            // -----------------------------
            // Notification Received
            // -----------------------------
            override fun onCharacteristicChanged(
                g: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                if (characteristic.uuid == dataUUID) {
                    val packet = characteristic.getStringValue(0) ?: ""
                    Log.d("BLE", "DATA NOTIF: '$packet'")
                    parseTelemetry(packet)
                    uploadTelemetryToFirebase()
                }
            }
        })
    }

    // ---------------------------------------------------------
    // Enable Notifications (with CCCD write)
    // ---------------------------------------------------------
    @Suppress("MissingPermission")
    private fun enableNotifications(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        gatt.setCharacteristicNotification(characteristic, true)

        val descriptor = characteristic.getDescriptor(cccdUUID)
        if (descriptor == null) {
            Log.e("BLE", "CCCD missing for ${characteristic.uuid}")
            return
        }

        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE

        Handler(Looper.getMainLooper()).postDelayed({
            gatt.writeDescriptor(descriptor)
        }, 150)
    }

    // ---------------------------------------------------------
    // Parse Telemetry Packet
    // ---------------------------------------------------------
    private fun parseTelemetry(packet: String) {
        val parts = packet.split(",")

        for (rawPart in parts) {
            val part = rawPart.trim()
            if (!part.contains("=")) continue

            val split = part.split("=", limit = 2)
            if (split.size != 2) continue

            val key = split[0].trim()
            var rawValue = split[1].trim()

            rawValue = rawValue
                .replace("%", "")
                .replace("h", "")
                .replace(" ", "")

            when (key) {
                "spl" -> spl.value = rawValue
                "laeq" -> laeq.value = rawValue
                "dose" -> dose.value = rawValue
                "led" -> led.value = rawValue
                "blink" -> blink.value = rawValue == "1"
                "time24" -> time24.value = rawValue
                "safe" -> safe.value = rawValue
            }
        }
    }

    // ---------------------------------------------------------
    // Upload to Firebase
    // ---------------------------------------------------------
    private fun uploadTelemetryToFirebase() {
        firebaseSummaryRef.setValue(
            mapOf(
                "spl" to spl.value,
                "laeq" to laeq.value,
                "dose" to dose.value,
                "led" to led.value,
                "blink" to blink.value,
                "time24" to time24.value,
                "safe" to safe.value
            )
        )
    }

    // ---------------------------------------------------------
    // Cleanup
    // ---------------------------------------------------------
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCleared() {
        gatt?.close()
        super.onCleared()
    }
}