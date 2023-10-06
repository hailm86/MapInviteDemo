package com.hailm.mapinvitedemo.ui.notification

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.DateFormatUtils
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.local.DataRepo
import com.hailm.mapinvitedemo.ui.zone_alert.ZoneAlertUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val dataRepo: DataRepo,
    private val userProfileProvider: UserProfileProvider
) : ViewModel() {

    private val _notificationList = MutableLiveData<List<NotificationUiModel>>()
    val notificationList: LiveData<List<NotificationUiModel>> get() = _notificationList

    @SuppressLint("SimpleDateFormat")
    fun getListNotification() {
        viewModelScope.launch {
            val list = dataRepo.loadListNoti(userProfileProvider.userPhoneNumber.toString())
            val notificationUiModelList = list.map {
                NotificationUiModel(
                    it.id,
                    it.phoneNumber,
                    it.title,
                    it.body,
                    it.zoneName,
                    it.createTime
                )
            }

            val dateFormat = SimpleDateFormat(DateFormatUtils.DateFormat.DateTime_Hyphen.toString())
            val newList =
                notificationUiModelList.sortedByDescending { dateFormat.parse(it.createTime) }

            _notificationList.value = newList
        }
    }
}
