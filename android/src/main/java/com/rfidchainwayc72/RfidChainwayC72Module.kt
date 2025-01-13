package com.rfidchainwayc72

import com.facebook.react.module.annotations.ReactModule
import android.util.Log
import androidx.annotation.Nullable
import com.facebook.react.bridge.*
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.IUHF
import com.facebook.react.bridge.LifecycleEventListener

@ReactModule(name = RfidChainwayC72Module.NAME)
class RfidChainwayC72Module(reactContext: ReactApplicationContext) :
  NativeRfidChainwayC72Spec(reactContext), LifecycleEventListener {

  companion object {
    const val NAME = "RfidChainwayC72"
    private const val UHF_READER_POWER_ON_ERROR = "UHF_READER_POWER_ON_ERROR"
    private const val UHF_READER_INIT_ERROR = "UHF_READER_INIT_ERROR"
    private const val UHF_READER_READ_ERROR = "UHF_READER_READ_ERROR"
    private const val UHF_READER_RELEASE_ERROR = "UHF_READER_RELEASE_ERROR"
    private const val UHF_READER_WRITE_ERROR = "UHF_READER_WRITE_ERROR"
    private const val UHF_READER_OTHER_ERROR = "UHF_READER_OTHER_ERROR"
  }

  private var mReader: RFIDWithUHFUART? = null
  private var mReaderStatus: Boolean = false
  private val scannedTags = ArrayList<String>()
  private var uhfInventoryStatus = false
  private var deviceName = ""


  init {
    reactContext.addLifecycleEventListener(this)
}
  
  override fun getName(): String {
    return NAME
  }

  override fun onHostDestroy() {
    UhfReaderPower(false).start();
  }

  override fun onHostResume() {
  }

  override fun  onHostPause() {
  }

  override fun multiply(a: Double, b: Double): Double {
    Log.d("MULTIPLY", "MULTIPLY2")
    return a * b
  }

  private fun sendEvent(eventName: String, array: WritableArray?) {
    getReactApplicationContext()
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        .emit(eventName, array)
  }

  private fun sendEvent(eventName: String, status: String?) {
    Log.d("UHF Reader", "Initializing Reader")

      getReactApplicationContext()
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
          .emit(eventName, status)
  }

  override fun initializeReader(promise: Promise): Unit {
    Log.d("UHF Reader", "Initializing Reader")
    UhfReaderPower().start()
    promise.resolve("Initializing Reader")
  }

  override fun deInitializeReader(promise: Promise): Unit {
    Log.d("UHF Reader", "DeInitializing Reader")
    UhfReaderPower(false).start()
    promise.resolve("DeInitializing Reader")
  }


  fun convertArrayToWritableArray(tags: Array<String>): WritableArray {
    val array = WritableNativeArray()
    tags.forEach { tagId ->
        array.pushString(tagId)
    }
    return array
  }


  override fun readSingleTag(promise: Promise): Unit {
    try {
        val tag = mReader?.inventorySingleTag()

        if (tag != null && !tag.epc.isNullOrEmpty()) {
            val tagData = arrayOf(tag.epc, tag.rssi.toString())
            promise.resolve(convertArrayToWritableArray(tagData))
        } else {
            promise.reject(UHF_READER_READ_ERROR, "TAG NOT FOUND OR EMPTY")
        }

    } catch (ex: Exception) {
        promise.reject(UHF_READER_READ_ERROR, ex)
    }
  }

  override fun startReadingTags(callback: Callback): Unit {
    try {
        uhfInventoryStatus = mReader?.startInventoryTag() ?: false
        TagThread().start()

        if (uhfInventoryStatus) {
            callback.invoke(uhfInventoryStatus);
        }
    } catch (ex: Exception) {
        callback.invoke(ex.printStackTrace())
    }
  }


  override fun stopReadingTags(callback: Callback): Unit {
    try {
        uhfInventoryStatus = !(mReader?.stopInventory() ?: false)
        sendEvent("UHF_POWER", "uhfInventoryStatus: " + uhfInventoryStatus)
        if (!uhfInventoryStatus) {
            callback.invoke((scannedTags.size));
        }
    } catch (ex: Exception) {
        callback.invoke(ex.printStackTrace())
    }
  }
  
  override fun readPower(promise: Promise): Unit {
      try {
          val uhfPower = mReader?.getPower()
          if (uhfPower != null && uhfPower >= 0) {
              promise.resolve(uhfPower)
          } else {
              promise.reject(UHF_READER_OTHER_ERROR, "INVALID POWER VALUE")
          }
          Log.d("UHF_SCANNER", uhfPower.toString())

      } catch (ex: Exception) {
          Log.d("UHF_SCANNER", ex.localizedMessage)
          promise.reject(UHF_READER_OTHER_ERROR, ex.localizedMessage)
      }
  }

  override fun changePower(powerValue: Double, promise: Promise): Unit {
      try {
          val uhfPowerState = mReader?.setPower(powerValue.toInt())
          if (uhfPowerState != null && uhfPowerState) {
              promise.resolve(uhfPowerState)
          } else {
              promise.reject(UHF_READER_OTHER_ERROR, "Can't Change Power")
          }
      } catch (ex: Exception) {
          Log.d("UHF_SCANNER", ex.localizedMessage)
          promise.reject(UHF_READER_OTHER_ERROR, ex.localizedMessage)
      }
  }


  override fun writeDataIntoEpc(epc: String, promise: Promise): Unit {
      var newEpc = epc
        sendEvent("UHF_POWER", "EPC: " + newEpc)

      if (newEpc.length == (6 * 4)) {
            newEpc += "00000000"
          
          val uhfWriteState = mReader?.writeData("00000000", IUHF.Bank_EPC, 2, 6, newEpc)
          sendEvent("UHF_POWER", "writeDataIntoEpc: " + uhfWriteState)
          sendEvent("UHF_POWER", "EPC: " + newEpc)

          if (uhfWriteState != null) {
               
              promise.resolve(uhfWriteState)
          } else {
              promise.reject(UHF_READER_WRITE_ERROR, "Can't Write Data")
          }
      } else {
          promise.reject(UHF_READER_WRITE_ERROR, "Invalid Data")
      }
  }


//   override fun clearAllTags() {
//       scannedTags.clear()
//   }

  override fun findTag(findEpc: String, callback: Callback) : Unit {
    try {
        uhfInventoryStatus = mReader?.startInventoryTag() ?: false
        if (uhfInventoryStatus) {
            TagThread(findEpc).start();
            callback.invoke(uhfInventoryStatus);
        }
    } catch (ex: Exception) {
        callback.invoke(ex.printStackTrace())
    }
  }


  inner class UhfReaderPower(private var powerOn: Boolean = true) : Thread() {
    override fun run() {
        sendEvent("RUN", "RUNNING")
        if (powerOn) {
            powerOn()
        } else {
            powerOff()
        }
    }

    private fun powerOn() {
        if (mReader == null || !mReaderStatus) {
            try {
                mReader = RFIDWithUHFUART.getInstance()
                try {
                    mReaderStatus = mReader?.init() ?: false
                    mReader?.setEPCAndTIDMode()
                    sendEvent("UHF_POWER", "success: power on")
                } catch (e: Exception) {
                    sendEvent("UHF_POWER", "failed: init error")
                }
            } catch (e: Exception) {
                sendEvent("UHF_POWER", "failed: power on error")
            }
        }
    }

    private fun powerOff() {
        if (mReader != null) {
            try {
                mReader?.free()
                mReader = null
                sendEvent("UHF_POWER", "success: power off")
            } catch (e: Exception) {
                sendEvent("UHF_POWER", "failed: " + e.message)
            }
        }
    }
  }


  inner class TagThread(private var findEpc: String = "") : Thread() {
    override fun run() {
        var res: UHFTAGInfo? = null
        sendEvent("UHF_POWER", "uhfInventoryStatus: " + uhfInventoryStatus)

        while (uhfInventoryStatus) {
            res = mReader?.readTagFromBuffer()
            if (res != null) {
                if (findEpc.isNullOrBlank()) {
                    addIfNotExists(res)
                } else {
                    lostTagOnly(res)
                }
            }
        }
    }

    private fun lostTagOnly(tag: UHFTAGInfo) {
        val epc = tag.epc
        if (epc == findEpc) {
            val tagData = arrayOf(epc, tag.rssi.toString())
            sendEvent("UHF_TAG", convertArrayToWritableArray(tagData))
        }
    }

    private fun addIfNotExists(tag: UHFTAGInfo) {
        if (!scannedTags.contains(tag.epc)) {
            scannedTags.add(tag.epc)
            val tagData = arrayOf(tag.epc, tag.rssi.toString())
            sendEvent("UHF_TAG", convertArrayToWritableArray(tagData))
        }
    }
  }
}
