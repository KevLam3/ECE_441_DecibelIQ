package com.example.ece441project

import android.Manifest
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
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

    private val mainHandler = Handler(Looper.getMainLooper())

    // ---------- SCAN ----------

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.w(TAG, "Bluetooth not available or disabled")
            return
        }

        Log.d(TAG, "Starting BLE scan")

        scanner = bluetoothAdapter.bluetoothLeScanner

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        // No filter: discover all, then match by name or service later if needed
        scanner?.startScan(null, settings, scanCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        Log.d(TAG, "Stopping BLE scan")
        scanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val name = device.name ?: "Unknown"
            Log.d(TAG, "Found device: $name - ${device.address}")

            // Match your ESP32 by name
            if (name == "ESP32-MIC") {
                Log.d(TAG, "Target ESP32-MIC found, stopping scan and connecting")
                stopScan()
                connectToDevice(device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Scan failed with error: $errorCode")
        }
    }

    // ---------- CONNECT / GATT LIFECYCLE ----------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectToDevice(device: BluetoothDevice) {
        Log.d(TAG, "Connecting to device: ${device.address}")

        // Close any previous GATT to avoid ghost connections
        gatt?.close()
        gatt = null

        targetDevice = device

        gatt = device.connectGatt(context, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        Log.d(TAG, "Disconnect requested")
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    private val gattCallback = object : BluetoothGattCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange: status=$status, newState=$newState")

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "GATT error, closing and retrying if possible")
                gatt.close()
                this@BleViewModel.gatt = null
                retryConnection()
                return
            }

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to GATT server, requesting MTU")

                    this@BleViewModel.gatt = gatt

                    mainHandler.postDelayed({
                        try {
                            gatt.requestMtu(MTU_REQUEST)
                        } catch (e: SecurityException) {
                            Log.e(TAG, "Missing BLUETOOTH_CONNECT permission for requestMtu", e)
                            gatt.discoverServices()
                        }
                    }, MTU_DELAY_MS)
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server")
                    gatt.close()
                    this@BleViewModel.gatt = null
                    retryConnection()
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.d(TAG, "MTU changed to $mtu, status=$status")
            gatt.discoverServices()
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG, "Services discovered, status=$status")
            if (status != BluetoothGatt.GATT_SUCCESS) return

            val service = gatt.getService(SERVICE_UUID)
            if (service == null) {
                Log.e(TAG, "Service $SERVICE_UUID not found")
                return
            }

            val characteristic = service.getCharacteristic(CHARACTERISTIC_UUID)
            if (characteristic == null) {
                Log.e(TAG, "Characteristic $CHARACTERISTIC_UUID not found")
                return
            }

            // Enable notifications
            gatt.setCharacteristicNotification(characteristic, true)

            val descriptor = characteristic.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            )
            if (descriptor != null) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            } else {
                Log.w(TAG, "CCCD descriptor not found, notifications may not work")
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == CHARACTERISTIC_UUID) {
                val data = characteristic.getStringValue(0)
                Log.d(TAG, "Received: $data")
                parsePacket(data)
            }
        }
    }

    // ---------- RECONNECT LOGIC ----------

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun retryConnection() {
        val device = targetDevice ?: return
        Log.d(TAG, "Retrying connection in ${RECONNECT_DELAY_MS}ms")

        mainHandler.postDelayed({
            if (bluetoothAdapter?.isEnabled == true) {
                connectToDevice(device)
            } else {
                Log.w(TAG, "Bluetooth disabled, cannot retry connection")
            }
        }, RECONNECT_DELAY_MS)
    }

    // ---------- PARSING ESP32 PACKET ----------

    private fun parsePacket(packet: String) {
        // Expected format:
        // spl=53.47,laeq=83.07,dose=0.85,led=ORANGE,blink=0,time24=23.89,safe=12.38

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
    }

    // ---------- CLEANUP ----------

    override fun onCleared() {
        super.onCleared()
        try {
            stopScan()
        } catch (_: SecurityException) {
        }
        try {
            gatt?.disconnect()
            gatt?.close()
        } catch (_: SecurityException) {
        }
        gatt = null
    }
}