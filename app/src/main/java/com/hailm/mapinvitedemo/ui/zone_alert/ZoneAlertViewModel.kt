package com.hailm.mapinvitedemo.ui.zone_alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZoneAlertViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileProvider: UserProfileProvider
) : BaseViewModel() {

    private val _zoneAlertList = MutableLiveData<List<ZoneAlertUiModel>>()
    val zoneAlertList: LiveData<List<ZoneAlertUiModel>> get() = _zoneAlertList

    fun getAllZoneAlert() {
        viewModelScope.launch {
            firestore
                .collection(Constants.ZONE_ALERT)
                .whereEqualTo("zonePhoneNumber", userProfileProvider.userPhoneNumber)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val dataList: MutableList<ZoneAlertUiModel> = mutableListOf()
                        for (document in documents) {
                            val data = document.toObject(ZoneAlert::class.java)
                            val zoneAlertUiModel = ZoneAlertUiModel(
                                data.zoneName,
                                data.zoneLat,
                                data.zoneLong,
                                data.zoneRadius,
                                data.zonePhoneNumber,
                                data.zoneType,
                                document.id
                            )
                            dataList.add(zoneAlertUiModel)
                        }

                        _zoneAlertList.value = dataList
                    }
                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }
}
