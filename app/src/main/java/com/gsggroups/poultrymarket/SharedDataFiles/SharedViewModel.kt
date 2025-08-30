package com.gsggroups.poultrymarket.SharedDataFiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // 1. Batch Ready Switch
    private val _batchReady = MutableLiveData<Boolean>()
    val batchReady: LiveData<Boolean> get() = _batchReady

    // 2. Need Load Switch
    private val _needLoad = MutableLiveData<Boolean>()
    val needLoad: LiveData<Boolean> get() = _needLoad

    // 3. Going For Load Switch
    private val _goingForLoad = MutableLiveData<Boolean>()
    val goingForLoad: LiveData<Boolean> get() = _goingForLoad

    // Setter methods
    fun setBatchReady(state: Boolean) {
        _batchReady.value = state
    }

    fun setNeedLoad(state: Boolean) {
        _needLoad.value = state
    }

    fun setGoingForLoad(state: Boolean) {
        _goingForLoad.value = state
    }
}