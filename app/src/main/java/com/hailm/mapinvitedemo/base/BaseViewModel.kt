package com.hailm.mapinvitedemo.base

import androidx.lifecycle.ViewModel
import com.hailm.mapinvitedemo.base.extension.toErrorDialog
import com.hailm.mapinvitedemo.base.model.AppErrorDialog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseViewModel : ViewModel() {
    private val loadingSharedFlow by lazy { MutableSharedFlow<Boolean>() }
    private val errorSharedFlow by lazy { MutableSharedFlow<AppErrorDialog>() }

    private val loadingCount by lazy { MutableStateFlow(0) }

    fun bindLoadingSharedFlow() = loadingSharedFlow.asSharedFlow()
    fun bindErrorSharedFlow() = errorSharedFlow.asSharedFlow()

    protected suspend fun showLoading() {
        loadingCount.emit(++loadingCount.value)
        loadingSharedFlow.emit(loadingCount.value > 0)
    }

    protected suspend fun hideLoading() {
        loadingCount.emit(--loadingCount.value)
        loadingSharedFlow.emit(loadingCount.value > 0)
    }

    protected suspend fun showError(throwable: Throwable) {
        errorSharedFlow.emit(throwable.toErrorDialog())
    }

    override fun onCleared() {
        super.onCleared()
    }
}
