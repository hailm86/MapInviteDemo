package com.hailm.mapinvitedemo.ui.zone_create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateZoneViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : BaseViewModel() {
    private val _addZoneAlert = MutableLiveData<Boolean>()
    val addZoneAlert: LiveData<Boolean> get() = _addZoneAlert

    private var documentIdZoneAlert = ""

    fun addZoneAlertToFirebase(zoneAlert: HashMap<String, String>) {
        viewModelScope.launch {
            firestore.collection(Constants.ZONE_ALERT).add(zoneAlert)
                .addOnSuccessListener { documentReference ->
                    printLog("Document added with ID: ${documentReference.id}")
                    _addZoneAlert.postValue(true)
                }
                .addOnFailureListener { e ->
                    printLog(e)
                    _addZoneAlert.postValue(false)
                }
        }
    }

    fun getDocumentIdZoneAlert(zoneAlertOld: ZoneAlert) {
        viewModelScope.launch {
            firestore
                .collection(Constants.ZONE_ALERT)
                .whereEqualTo("zoneName", zoneAlertOld.zoneName)
                .whereEqualTo("zoneLat", zoneAlertOld.zoneLat)
                .whereEqualTo("zoneLong", zoneAlertOld.zoneLong)
                .whereEqualTo("zoneRadius", zoneAlertOld.zoneRadius)
                .whereEqualTo("zonePhoneNumber", zoneAlertOld.zonePhoneNumber)
                .whereEqualTo("zoneType", zoneAlertOld.zoneType)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        documentIdZoneAlert = document.id
                    }

                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }

    fun addEditZoneAlertToFirebase(zoneAlert: HashMap<String, String>) {
        viewModelScope.launch {
            val documentRef =
                firestore.collection(Constants.ZONE_ALERT).document(documentIdZoneAlert)

            val updates = hashMapOf(
                "zoneName" to zoneAlert["zoneName"] as Any,
                "zoneLat" to zoneAlert["zoneLat"] as Any,
                "zoneLong" to zoneAlert["zoneLong"] as Any,
                "zoneRadius" to zoneAlert["zoneRadius"] as Any,
                "zonePhoneNumber" to zoneAlert["zonePhoneNumber"] as Any,
                "zoneType" to zoneAlert["zoneType"] as Any,
            )

            documentRef.update(updates)
                .addOnSuccessListener {
                    printLog("==> Add accept to fireStore success")
                    _addZoneAlert.postValue(true)
                }
                .addOnFailureListener { e ->
                    printLog(e)
                    _addZoneAlert.postValue(false)
                }
        }
    }
}