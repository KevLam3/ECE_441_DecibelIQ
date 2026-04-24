package com.example.ece441project

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class BleViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "BleVM"

        val SERVICE_UUID: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")

        private const val MTU_REQUEST = 512
        private const val RECONNECT_DELAY_MS = 500L
        private const val MTU_DELAY_MS = 300L
    }

    private val context: Context = application.applicationContext
    private val firebaseRef = FirebaseDatabase.getInstance().getReference("mic")
    private val shiftLogsRef = FirebaseDatabase.getInstance().getReference("shift_logs")

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private var scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private var gatt: BluetoothGatt? = null
    private var targetDevice: BluetoothDevice? = null

    // BLE data exposed to UI
    private val _spl = MutableStateFlow(0f)
    val spl: StateFlow<Float> = _spl

    private val _laeq = MutableStateFlow(0f)
    val laeq: StateFlow<Float> = _laeq

    private val _dose = MutableStateFlow(0f)
    val dose: StateFlow<Float> = _dose

    private val _led = MutableStateFlow("")
    val led: StateFlow<String> = _led

    private val _blink = MutableStateFlow(false)
    val blink: StateFlow<Boolean> = _blink

    private val _time24 = MutableStateFlow(0f)
    val time24: StateFlow<Float> = _time24

    private val _safe = MutableStateFlow(0f)
    val safe: StateFlow<Float> = _safe

    private val _batteryPercent = MutableStateFlow(0)
    val batteryPercent: StateFlow<Int> = _batteryPercent

    // Active shift logging
    private val _activeShiftId = MutableStateFlow<String?>(null)
    val activeShiftId: StateFlow<String?> = _activeShiftId

    private val mainHandler = Handler(Looper.getMainLooper())

    // ---------- Public API for shift logging ----------

    fun startShiftLogging(shiftId: String) {
        _activeShiftId.value = shiftId
    }

    fun stopShiftLogging() {
        _activeShiftId.value = null
    }

    // ---------- SCAN ----------

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.w(TAG, "Bluetooth not available or disabled")
            return
        }

        scanner = bluetoothAdapter.bluetoothLeScanner

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner?.startScan(null, settings, scanCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        scanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val name = device.name ?: "Unknown"

            if (name == "ESP32-MIC") {
                stopScan()
                connectToDevice(device)
            }
        }
    }

    // ---------- CONNECT / GATT LIFECYCLE ----------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectToDevice(device: BluetoothDevice) {
        gatt?.close()
        gatt = null

        targetDevice = device
        gatt = device.connectGatt(context, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    private val gattCallback = object : BluetoothGattCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {

            if (status != BluetoothGatt.GATT_SUCCESS) {
                gatt.close()
                this@BleViewModel.gatt = null
                retryConnection()
                return
            }

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    this@BleViewModel.gatt = gatt

                    mainHandler.postDelayed({
                        try {
                            gatt.requestMtu(MTU_REQUEST)
                        } catch (_: SecurityException) {
                            gatt.discoverServices()
                        }
                    }, MTU_DELAY_MS)
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    gatt.close()
                    this@BleViewModel.gatt = null
                    retryConnection()
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            gatt.discoverServices()
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return

            val service = gatt.getService(SERVICE_UUID) ?: return
            val characteristic = service.getCharacteristic(CHARACTERISTIC_UUID) ?: return

            gatt.setCharacteristicNotification(characteristic, true)

            val descriptor = characteristic.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            )
            descriptor?.let {
                it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(it)
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == CHARACTERISTIC_UUID) {
                val data = characteristic.getStringValue(0)
                parsePacket(data)
            }
        }
    }

    // ---------- RECONNECT LOGIC ----------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun retryConnection() {
        val device = targetDevice ?: return

        mainHandler.postDelayed({
            if (bluetoothAdapter?.isEnabled == true) {
                connectToDevice(device)
            }
        }, RECONNECT_DELAY_MS)
    }

    // ---------- PARSE PACKET ----------

    private fun parsePacket(packet: String) {
        val parts = packet.split(",")
        val map = mutableMapOf<String, String>()

        for (part in parts) {
            val kv = part.split("=")
            if (kv.size == 2) {
                map[kv[0].trim()] = kv[1].trim()
            }
        }

        map["spl"]?.toFloatOrNull()?.let { _spl.value = it }
        map["laeq"]?.toFloatOrNull()?.let { _laeq.value = it }
        map["dose"]?.toFloatOrNull()?.let { _dose.value = it }
        map["led"]?.let { _led.value = it }
        map["blink"]?.toIntOrNull()?.let { _blink.value = it != 0 }
        map["time24"]?.toFloatOrNull()?.let { _time24.value = it }
        map["safe"]?.toFloatOrNull()?.let { _safe.value = it }
        map["batt"]?.toIntOrNull()?.let { _batteryPercent.value = it }

        // Log dose history for active shift (timestamped)
        val shiftId = _activeShiftId.value
        if (shiftId != null) {
            val ts = System.currentTimeMillis().toString()
            shiftLogsRef
                .child(shiftId)
                .child("doseHistory")
                .child(ts)
                .setValue(_dose.value)
        }

        uploadToFirebase()
    }

    // ---------- FIREBASE UPLOAD (live mic snapshot) ----------

    @SuppressLint("DefaultLocale")
    private fun uploadToFirebase() {
        val data = mapOf(
            "spl" to String.format("%.2f", spl.value),
            "laeq" to String.format("%.2f", laeq.value),
            "dose" to String.format("%.2f", dose.value),
            "led" to led.value,
            "blink" to blink.value,
            "time24" to String.format("%.2f", time24.value),
            "safe" to String.format("%.2f", safe.value),
            "batt" to batteryPercent.value.toString()
        )

        firebaseRef.setValue(data)
    }

    // ---------- CLEANUP ----------

    override fun onCleared() {
        super.onCleared()
        try { stopScan() } catch (_: SecurityException) {}
        try {
            gatt?.disconnect()
            gatt?.close()
        } catch (_: SecurityException) {}
        gatt = null
    }
}