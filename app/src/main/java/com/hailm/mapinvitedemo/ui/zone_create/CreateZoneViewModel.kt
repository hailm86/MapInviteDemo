package com.hailm.mapinvitedemo.ui.zone_create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.isInsideGeofence
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.helper.SingleLiveEvent
import com.hailm.mapinvitedemo.base.model.User
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.ZONE_ALERT
import com.hailm.mapinvitedemo.base.util.Constants.ZONE_MEMBER
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

    val hasZoneMember = SingleLiveEvent<Pair<Boolean, String>>()

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

            documentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val zoneMembers = documentSnapshot.get("zoneMember")
                        val updates = hashMapOf(
                            "zoneName" to zoneAlert["zoneName"],
                            "zoneLat" to zoneAlert["zoneLat"],
                            "zoneLong" to zoneAlert["zoneLong"],
                            "zoneRadius" to zoneAlert["zoneRadius"],
                            "zonePhoneNumber" to zoneAlert["zonePhoneNumber"],
                            "zoneType" to zoneAlert["zoneType"],
                            "zoneMember" to zoneMembers,
                            "currentZoom" to zoneAlert["currentZoom"],
                            "zoneDeviceToken" to zoneAlert["zoneDeviceToken"],
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
                .addOnFailureListener { e ->
                    // Xử lý lỗi nếu có
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

    fun addMemberToZone(memberPhone: String, documentIdZoneAlert: String, memberName: String) {
        viewModelScope.launch {
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
                            getLatLongUser(
                                memberPhone,
                                MemberData(documentIdZoneAlert, memberPhone, memberName)
                            )
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

    private fun createZoneMemberToFirebase(
        latLongMember: LatLng,
        memberData: MemberData
    ) {
        viewModelScope.launch {
//            val isInsideGeofence = if (isInsideGeofence(
//                    latLongMember,
//                    CreateZoneFragment.GEOFENCE_RADIUS
//                )
//            ) {
//                Constants.INSIDE
//            } else {
//                Constants.OUTSIDE
//            }

            val isInsideGeofence = Constants.INSIDE
            val zoneMemberData = hashMapOf(
                "documentIdZoneAlert" to memberData.documentIdZoneAlert,
                "zoneMember" to memberData.zoneMember,
                "memberName" to memberData.memberName,
                "isInsideGeofence" to isInsideGeofence
            )

            firestore.collection(ZONE_MEMBER).add(zoneMemberData)
                .addOnSuccessListener { documentReference ->
                    printLog("Document createZoneMemberToFirebase added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    printLog(e)
                }
        }
    }

    private fun getLatLongUser(phoneNumber: String, memberData: MemberData) {
        viewModelScope.launch {
            firestore
                .collection(Constants.USERS)
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val data = document.toObject(User::class.java)
                        createZoneMemberToFirebase(
                            LatLng(
                                data.lat.toString().toDouble(),
                                data.long.toString().toDouble()
                            ), memberData
                        )

                    }
                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }

    fun checkHasPhoneNumber(documentIdZoneAlert: String, zoneMember: String) {
        firestore
            .collection(ZONE_MEMBER)
            .whereEqualTo("documentIdZoneAlert", documentIdZoneAlert)
            .whereEqualTo("zoneMember", zoneMember)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    hasZoneMember.postValue(Pair(false, zoneMember))
                } else {
                    hasZoneMember.postValue(Pair(true, zoneMember))
                }
            }
            .addOnFailureListener { exception ->
                printLog("Error getting documents: $exception")
            }
    }
}

data class MemberData(
    val documentIdZoneAlert: String,
    val zoneMember: String,
    val memberName: String
)