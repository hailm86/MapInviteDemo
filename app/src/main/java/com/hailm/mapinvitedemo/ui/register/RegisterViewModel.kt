package com.hailm.mapinvitedemo.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.util.Constants.USERS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileProvider: UserProfileProvider
) : BaseViewModel() {

    private val _hasPhoneNumber = MutableLiveData<Boolean>()
    val hasPhoneNumber: LiveData<Boolean> get() = _hasPhoneNumber

    private val _addNewUser = MutableLiveData<Boolean>()
    val addNewUser: LiveData<Boolean> get() = _addNewUser

    fun checkHasPhoneNumber(phoneNumber: String) {
        firestore
            .collection(USERS)
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    _hasPhoneNumber.postValue(false)
                } else {
                    _hasPhoneNumber.postValue(true)
                }
            }
            .addOnFailureListener { exception ->
                printLog("Error getting documents: $exception")
            }
    }

    fun addNewUserToFirebase(userData: HashMap<String, String>) {
        viewModelScope.launch {
            firestore.collection(USERS).add(userData)
                .addOnSuccessListener { documentReference ->
                    printLog("Document added with ID: ${documentReference.id}")
                    _addNewUser.postValue(true)
                }
                .addOnFailureListener { e ->
                    printLog(e)
                    _addNewUser.postValue(false)
                }
        }
    }
}