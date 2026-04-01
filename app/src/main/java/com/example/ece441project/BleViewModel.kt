package com.example.ece441project

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import java.nio.ByteBuffer
import java.util.UUID

data class Sample(val time: Long, val value: Int)

class BleViewModel : ViewModel() {

    val rawValue = mutableStateOf("Waiting…")
    val waveform = mutableStateListOf<Sample>()

    private var gatt: BluetoothGatt? = null
    private var isScanning = false

    private val serviceUUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    private val charUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
    private val cccdUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    @Suppress("MissingPermission")
    fun startScan(context: Context, firebaseRef: DatabaseReference, maxPoints: Int) {
        if (isScanning) return
        isScanning = true

        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val scanner = bluetoothManager.adapter.bluetoothLeScanner

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                if (result.device?.name == "ESP32-MIC") {
                    scanner.stopScan(this)
                    connectToDevice(result.device, context, firebaseRef, maxPoints)
                }
            }
        }

        scanner.startScan(scanCallback)
    }

    @Suppress("MissingPermission")
    private fun connectToDevice(
        device: BluetoothDevice,
        context: Context,
        firebaseRef: DatabaseReference,
        maxPoints: Int
    ) {
        gatt = device.connectGatt(context, false, object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val service = gatt.getService(serviceUUID)
                val characteristic = service.getCharacteristic(charUUID)

                gatt.setCharacteristicNotification(characteristic, true)

                val descriptor = characteristic.getDescriptor(cccdUUID)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                val value = ByteBuffer
                    .wrap(characteristic.value)
                    .order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    .int

                rawValue.value = value.toString()
                firebaseRef.setValue(value)

                val now = System.currentTimeMillis()
                waveform.add(Sample(now, value))
                if (waveform.size > maxPoints) waveform.removeAt(0)
            }
        })
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCleared() {
        super.onCleared()
        gatt?.close()
    }
}
