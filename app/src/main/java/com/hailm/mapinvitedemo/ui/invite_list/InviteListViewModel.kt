package com.hailm.mapinvitedemo.ui.invite_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseViewModel
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.DEFAULT
import com.hailm.mapinvitedemo.base.util.Constants.NOT_ACCEPT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.HashMap
import javax.inject.Inject

@HiltViewModel
class InviteListViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileProvider: UserProfileProvider
) : BaseViewModel() {

    private val _userInviteList = MutableLiveData<List<UserInvite>>()
    val userInviteList: LiveData<List<UserInvite>> get() = _userInviteList

    private val _accepted = MutableLiveData<Pair<Int, Int>>()
    val accepted: LiveData<Pair<Int, Int>> get() = _accepted

    fun getListInvite() {
        viewModelScope.launch {
            firestore
                .collection(Constants.USER_INVITE)
                .whereEqualTo("userTwo", userProfileProvider.userPhoneNumber)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val dataList: MutableList<UserInvite> = mutableListOf()
                        for (document in documents) {
                            val data = document.toObject(UserInvite::class.java)
                            dataList.add(data)
                        }

                        printLog("====> $dataList")
                        _userInviteList.value = dataList
                    }
                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }

    fun updateStatus(model: UserInvite, pos: Int) {
        viewModelScope.launch {
            firestore
                .collection(Constants.USER_INVITE)
                .whereEqualTo("userOne", model.userOne)
                .whereEqualTo("userTwo", model.userTwo)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val documentRef =
                            firestore.collection(Constants.USER_INVITE).document(document.id)

                        val updates = hashMapOf<String, Any>(
                            "userOne" to model.userOne.toString(),
                            "userTwo" to model.userTwo.toString(),
                            "status" to Constants.ACCEPT
                        )

                        documentRef.update(updates)
                            .addOnSuccessListener {
                                printLog("==> Add accept to fireStore success")
                                _accepted.value = Pair(Constants.SUCCESS, pos)
                            }
                            .addOnFailureListener { e ->
                                printLog(e)
                                _accepted.value = Pair(Constants.FAILURE, pos)
                            }
                    }

                }
                .addOnFailureListener { exception ->
                    printLog("Error getting documents: $exception")
                }
        }
    }
}