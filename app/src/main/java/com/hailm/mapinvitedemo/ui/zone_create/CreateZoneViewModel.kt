package com.hailm.mapinvitedemo.ui.zone_create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.ZONE_ALERT
import com.hailm.mapinvitedemo.ui.invite_list.UserInvite
import com.hailm.mapinvitedemo.ui.invite_list.UserInviteUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateZoneViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileProvider: UserProfileProvider
) : BaseViewModel() {
    private val _addZoneAlert = MutableLiveData<Boolean>()
    val addZoneAlert: LiveData<Boolean> get() = _addZoneAlert

    private val _memberList = MutableLiveData<ArrayList<UserInviteUiModel>>()
    val memberList: LiveData<ArrayList<UserInviteUiModel>> get() = _memberList

    fun addZoneAlertToFirebase(zoneAlert: HashMap<String, Any>) {
        viewModelScope.launch {
            firestore.collection(ZONE_ALERT).add(zoneAlert)
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

    fun addEditZoneAlertToFirebase(zoneAlert: HashMap<String, Any>, documentIdZoneAlert: String) {
        viewModelScope.launch {
            val documentRef =
                firestore.collection(ZONE_ALERT).document(documentIdZoneAlert)

            val updates = hashMapOf(
                "zoneName" to zoneAlert["zoneName"] as Any,
                "zoneLat" to zoneAlert["zoneLat"] as Any,
                "zoneLong" to zoneAlert["zoneLong"] as Any,
                "zoneRadius" to zoneAlert["zoneRadius"] as Any,
                "zonePhoneNumber" to zoneAlert["zonePhoneNumber"] as Any,
                "zoneType" to zoneAlert["zoneType"] as Any,
                "zoneMember" to zoneAlert["zoneMember"] as Any
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

    fun getListMember() {
        viewModelScope.launch {
            firestore
                .collection(Constants.USER_INVITE)
                .whereEqualTo("userOne", userProfileProvider.userPhoneNumber)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val dataList: ArrayList<UserInviteUiModel> = arrayListOf()
                        for (document in documents) {
                            val data = document.toObject(UserInvite::class.java)
                            val userInviteUiModel = UserInviteUiModel(
                                data.userOne,
                                data.userTwo,
                                data.status,
                                document.id
                            )
                            dataList.add(userInviteUiModel)
                        }

                        printLog("====> $dataList")
                        _memberList.value = dataList
                    }
                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }

    fun addMemberToZone(memberPhone: String, documentIdZoneAlert: String) {
        viewModelScope.launch {
            printLog("==> $documentIdZoneAlert")
            val areaRef =
                firestore.collection(ZONE_ALERT).document(documentIdZoneAlert)
            areaRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val existingUserIds =
                            documentSnapshot.get("zoneMember") as? List<String> ?: emptyList()
                        val updatedUserIds = existingUserIds.toMutableList()
                        if (!updatedUserIds.contains(memberPhone)) {
                            updatedUserIds.add(memberPhone)
                        }

                        areaRef.update("zoneMember", updatedUserIds)
                            .addOnSuccessListener {
                                // Người dùng đã được thêm vào khu vực (Area) thành công
                            }
                            .addOnFailureListener { e ->
                                // Xử lý lỗi nếu có
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Xử lý lỗi nếu có
                }
        }
    }
}