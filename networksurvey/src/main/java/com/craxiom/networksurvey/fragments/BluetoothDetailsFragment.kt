package com.craxiom.networksurvey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.navArgs
import com.craxiom.messaging.BluetoothRecord
import com.craxiom.messaging.BluetoothRecordData
import com.craxiom.networksurvey.listeners.IBluetoothSurveyRecordListener
import com.craxiom.networksurvey.services.NetworkSurveyService
import com.craxiom.networksurvey.ui.UNKNOWN_RSSI
import com.craxiom.networksurvey.ui.bluetooth.BluetoothDetailsScreen
import com.craxiom.networksurvey.ui.bluetooth.BluetoothDetailsViewModel
import com.craxiom.networksurvey.util.NsTheme
import timber.log.Timber

/**
 * The fragment that displays the details of a single Bluetooth device from the scan results.
 */
class BluetoothDetailsFragment : AServiceDataFragment(), IBluetoothSurveyRecordListener {
    private lateinit var bluetoothData: BluetoothRecordData
    private lateinit var viewModel: BluetoothDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args: BluetoothDetailsFragmentArgs by navArgs()
        bluetoothData = args.bluetoothData

        val composeView = ComposeView(requireContext())

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                viewModel = viewModel()
                viewModel.bluetoothData = bluetoothData
                if (bluetoothData.hasSignalStrength()) {
                    viewModel.addInitialRssi(bluetoothData.signalStrength.value)
                } else {
                    viewModel.addInitialRssi(UNKNOWN_RSSI)
                }
                NsTheme {
                    BluetoothDetailsScreen(viewModel = viewModel)
                }
            }
        }

        return composeView
    }

    override fun onResume() {
        super.onResume()

        startAndBindToService()
    }

    override fun onSurveyServiceConnected(service: NetworkSurveyService?) {
        if (service == null) return
        service.registerBluetoothSurveyRecordListener(this)
    }

    override fun onSurveyServiceDisconnecting(service: NetworkSurveyService?) {
        if (service == null) return
        service.unregisterBluetoothSurveyRecordListener(this)
    }

    override fun onBluetoothSurveyRecord(bluetoothRecord: BluetoothRecord?) {
        if (bluetoothRecord == null) return
        if (bluetoothRecord.data.sourceAddress.equals(bluetoothData.sourceAddress)) {
            if (bluetoothRecord.data.hasSignalStrength()) {
                viewModel.addNewRssi(bluetoothRecord.data.signalStrength.value)
            } else {
                Timber.i("No signal strength present for ${bluetoothData.sourceAddress} in the bluetooth record")
                viewModel.addNewRssi(UNKNOWN_RSSI)
            }
        }
    }

    override fun onBluetoothSurveyRecords(bluetoothRecords: MutableList<BluetoothRecord>?) {
        val matchedRecord =
            bluetoothRecords?.find { it.data.sourceAddress.equals(bluetoothData.sourceAddress) }

        if (matchedRecord == null) {
            Timber.i("No bluetooth record found for ${bluetoothData.sourceAddress} in the bluetooth scan results")
            viewModel.addNewRssi(UNKNOWN_RSSI)
            return
        }

        if (matchedRecord.data.hasSignalStrength()) {
            viewModel.addNewRssi(matchedRecord.data.signalStrength.value)
        } else {
            Timber.i("No signal strength present for ${bluetoothData.sourceAddress} in the bluetooth record")
            viewModel.addNewRssi(UNKNOWN_RSSI)

        }
    }
}