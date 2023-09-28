package com.hailm.mapinvitedemo.ui.home

import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.helper.SingleLiveEvent
import com.hailm.mapinvitedemo.base.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileProvider: UserProfileProvider
) : BaseViewModel() {
    val hasPhoneNumber = SingleLiveEvent<Boolean>()

    fun checkHasPhoneNumber(phoneNumber: String) {
        firestore
            .collection(Constants.USERS)
            .whereEqualTo("phoneNumber", phoneNumber) // dummy phone number
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    hasPhoneNumber.postValue(false)
                } else {
                    userProfileProvider.saveUserPhoneNumber(phoneNumber)
                    hasPhoneNumber.postValue(true)
                }
            }
            .addOnFailureListener { exception ->
                printLog("Error getting documents: $exception")
            }
    }

    fun onNavigationHandled() {
        hasPhoneNumber.value = false
    }
}
