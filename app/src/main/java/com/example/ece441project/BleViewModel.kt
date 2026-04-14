package com.example.ece441project

import android.Manifest
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.pm.PackageManager
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class BleViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private var bluetoothGatt: BluetoothGatt? = null
    private var scanner: BluetoothLeScanner? = null

    // ESP32 UUIDs
    private val SERVICE_UUID =
        UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    private val DATA_UUID =
        UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
    private val CCCD_UUID =
        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // UI State
    private val _spl = MutableStateFlow(0f)
    val spl: StateFlow<Float> = _spl

    private val _laeq = MutableStateFlow(0f)
    val laeq: StateFlow<Float> = _laeq

    private val _dose = MutableStateFlow(0f)
    val dose: StateFlow<Float> = _dose

    private val _led = MutableStateFlow("GREEN")
    val led: StateFlow<String> = _led

    private val _blink = MutableStateFlow(false)
    val blink: StateFlow<Boolean> = _blink

    private val _time24 = MutableStateFlow(24f)
    val time24: StateFlow<Float> = _time24

    private val _safe = MutableStateFlow(0f)
    val safe: StateFlow<Float> = _safe

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    // ---------------------------------------------------------
    // SCANNING
    // ---------------------------------------------------------
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner?.startScan(listOf(filter), settings, scanCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        scanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            stopScan()
            connect(result.device)
        }
    }

    // ---------------------------------------------------------
    // CONNECT
    // ---------------------------------------------------------
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    // ---------------------------------------------------------
    // GATT CALLBACK
    // ---------------------------------------------------------
    private val gattCallback = object : BluetoothGattCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _isConnected.value = true
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _isConnected.value = false
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(SERVICE_UUID) ?: return
            val dataChar = service.getCharacteristic(DATA_UUID) ?: return

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED
            ) return

            gatt.setCharacteristicNotification(dataChar, true)

            val descriptor = dataChar.getDescriptor(CCCD_UUID)
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            // Retry if needed
            if (status != BluetoothGatt.GATT_SUCCESS) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid != DATA_UUID) return

            val packet = characteristic.value?.toString(Charsets.UTF_8) ?: return
            parsePacket(packet)
        }
    }

    // ---------------------------------------------------------
    // PARSE TELEMETRY
    // ---------------------------------------------------------
    private fun parsePacket(packet: String) {
        val map = packet.split(",").associate {
            val (k, v) = it.split("=").map { it.trim() }
            k to v
        }

        viewModelScope.launch {
            _spl.value = map["spl"]?.toFloatOrNull() ?: 0f
            _laeq.value = map["laeq"]?.toFloatOrNull() ?: 0f
            _dose.value = map["dose"]?.toFloatOrNull() ?: 0f
            _led.value = map["led"] ?: "GREEN"
            _blink.value = map["blink"] == "1"
            _time24.value = map["time24"]?.toFloatOrNull() ?: 0f
            _safe.value = map["safe"]?.toFloatOrNull() ?: 0f
        }
    }
}