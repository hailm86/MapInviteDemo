package com.hailm.mapinvitedemo.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.util.Constants
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
}