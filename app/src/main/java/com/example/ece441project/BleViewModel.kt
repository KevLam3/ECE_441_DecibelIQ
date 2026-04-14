package com.example.ece441project

import android.Manifest
import android.app.Application
import android.bluetooth.*
import android.content.pm.PackageManager
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

    // -----------------------------
    // BLE Connection
    // -----------------------------
    fun connect(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()
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

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val packet = characteristic.getStringValue(0)
            parsePacket(packet)
        }
    }

    // -----------------------------
    // Parse ESP32 telemetry packet
    // -----------------------------
    private fun parsePacket(packet: String) {
        val map = packet.split(",").associate {
            val (k, v) = it.split("=")
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