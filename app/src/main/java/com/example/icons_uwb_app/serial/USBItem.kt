package com.example.icons_uwb_app.serial

import android.hardware.usb.UsbDevice
import com.hoho.android.usbserial.driver.*

data class USBItem(
    val device: UsbDevice,
    val port: UsbSerialPort,
    val driver: UsbSerialDriver = CdcAcmSerialDriver(device)
)
