package com.hailm.mapinvitedemo.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.model.User
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.ui.invite_list.UserInvite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileProvider: UserProfileProvider
) : BaseViewModel() {


    private val _hasPhoneNumber = MutableLiveData(Constants.DEFAULT)
    val hasPhoneNumber: LiveData<Int> get() = _hasPhoneNumber

    private val _inviteSuccess = MutableLiveData(Constants.DEFAULT)
    val inviteSuccess: LiveData<Int> get() = _inviteSuccess

    private val _hasInvited = MutableLiveData<Pair<Boolean, String?>>()
    val hasInvited: LiveData<Pair<Boolean, String?>> get() = _hasInvited


    private val _listLatLong = MutableLiveData<List<LatLng>>()
    val listLatLong: LiveData<List<LatLng>> get() = _listLatLong

    fun checkHasPhoneNumber(phoneNumber: String) {
        firestore
            .collection(Constants.USERS)
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    _hasPhoneNumber.postValue(Constants.FAILURE)
                } else {
                    _hasPhoneNumber.postValue(Constants.SUCCESS)
                }
            }
            .addOnFailureListener { exception ->
                printLog("Error getting documents: $exception")
            }
    }

    fun checkInvitedUser(phoneNumber: String) {
        viewModelScope.launch {
            val userPhoneNumber = userProfileProvider.userPhoneNumber
            firestore
                .collection(Constants.USER_INVITE)
                .whereEqualTo("userOne", userPhoneNumber)
                .whereEqualTo("userTwo", phoneNumber)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        _hasInvited.postValue(Pair(false, userPhoneNumber))
                    } else {
                        _hasInvited.postValue(Pair(true, userPhoneNumber))
                    }
                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }

    fun inviteUserToFirebase(userInvite: HashMap<String, String>) {
        viewModelScope.launch {
            firestore.collection(Constants.USER_INVITE).add(userInvite)
                .addOnSuccessListener { documentReference ->
                    printLog("Document added with ID: ${documentReference.id}")
                    _inviteSuccess.postValue(Constants.SUCCESS)
                }
                .addOnFailureListener { e ->
                    printLog(e)
                    _inviteSuccess.postValue(Constants.FAILURE)
                }
        }
    }

    fun getAllUserInvite() {
        viewModelScope.launch {
            val userPhoneNumber = userProfileProvider.userPhoneNumber
            firestore
                .collection(Constants.USER_INVITE)
                .whereEqualTo("userOne", userPhoneNumber)
                .whereEqualTo("status", Constants.ACCEPT)
                .get()
                .addOnSuccessListener { documents ->
                    val phoneList: MutableList<String> = mutableListOf()
                    for (document in documents) {
                        val data = document.toObject(UserInvite::class.java)
                        phoneList.add(data.userTwo.toString())
                    }

                    getListLatLongByUser(phoneList)
                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }

        }
    }

    private fun getListLatLongByUser(phoneList: List<String>) {
        if (phoneList.isEmpty()) return

        firestore
            .collection(Constants.USERS)
            .whereIn("phoneNumber", phoneList)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                if (querySnapshot != null) {
                    val list: MutableList<LatLng> = mutableListOf()
                    for (document in querySnapshot.documents) {
                        val data = document.toObject(User::class.java)
                        list.add(
                            LatLng(
                                data?.lat?.toDouble() ?: 0.0,
                                data?.long?.toDouble() ?: 0.0
                            )
                        )
                    }
                    _listLatLong.value = list
                }
            }
            .addOnFailureListener { exception ->
                // Xử lý lỗi nếu có
            }
    }
}