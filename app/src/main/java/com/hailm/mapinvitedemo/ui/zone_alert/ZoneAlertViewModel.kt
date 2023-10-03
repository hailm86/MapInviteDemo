package com.hailm.mapinvitedemo.ui.zone_alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.helper.SingleLiveEvent
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZoneAlertViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileProvider: UserProfileProvider
) : BaseViewModel() {

    private val _zoneAlertList = MutableLiveData<List<ZoneAlertUiModel>>()
    val zoneAlertList: LiveData<List<ZoneAlertUiModel>> get() = _zoneAlertList

    val deleteZoneSuccess = SingleLiveEvent<Int>()

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
                                zoneName = data.zoneName,
                                zoneLat = data.zoneLat,
                                zoneLong = data.zoneLong,
                                zoneRadius = data.zoneRadius,
                                zonePhoneNumber = data.zonePhoneNumber,
                                zoneType = data.zoneType,
                                documentId = document.id,
                                currentZoom = data.currentZoom
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

    fun deleteZone(zoneData: ZoneAlertUiModel) {
        viewModelScope.launch {
            try {
                val result1 = async { deleteZoneMember(zoneData) }
                val result2 = async { deleteZoneAlert(zoneData) }

                awaitAll(result1, result2)
                val currentList = _zoneAlertList.value.orEmpty().toMutableList()

                val position = currentList.indexOf(zoneData)
                deleteZoneSuccess.value = position
            } catch (e: Exception) {
                deleteZoneSuccess.value = -1
                printLog(e)
            }

        }
    }

    private fun deleteZoneMember(zoneData: ZoneAlertUiModel) {
        val zoneMember = firestore.collection(Constants.ZONE_MEMBER)

        zoneMember.whereEqualTo("documentIdZoneAlert", zoneData.documentId)
            .whereEqualTo("zoneMember", zoneData.zonePhoneNumber)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        zoneMember.document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                printLog("Delete zoneMember success")
                            }
                            .addOnFailureListener { e ->
                                printLog("Delete zoneMember failed $e")
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                printLog("Error getting documents: $exception")
            }
    }

    private fun deleteZoneAlert(zoneData: ZoneAlertUiModel) {
        val zoneAlert =
            firestore.collection(Constants.ZONE_ALERT).document(zoneData.documentId.toString())
        zoneAlert
            .delete()
            .addOnSuccessListener {
                printLog("Delete zoneAlert success")
            }
            .addOnFailureListener { e ->
                printLog("Delete zoneAlert failed $e")
            }
    }
}
