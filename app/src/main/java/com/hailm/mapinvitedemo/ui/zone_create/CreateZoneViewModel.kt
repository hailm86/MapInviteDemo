package com.hailm.mapinvitedemo.ui.zone_create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.extension.printLog
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
}