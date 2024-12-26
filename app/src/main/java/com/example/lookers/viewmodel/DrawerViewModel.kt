package com.example.lookers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookers.model.entity.ResultState
import com.example.lookers.model.entity.drawer.DrawerEntity
import com.example.lookers.model.entity.drawer.DrawerImageResponse
import com.example.lookers.model.entity.drawer.DrawerUnitEntity
import com.example.lookers.model.entity.drawer.DrawerUser
import com.example.lookers.model.entity.prod.ProdHistory
import com.example.lookers.repository.DrawerRepository
import com.example.lookers.util.handleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel
    @Inject
    constructor(
        private val drawerRepository: DrawerRepository,
    ) : ViewModel() {
        private val _myDrawerList = MutableLiveData<ResultState<List<DrawerEntity>>>()
        val myDrawerList: LiveData<ResultState<List<DrawerEntity>>> = _myDrawerList

        private val _drawerList = MutableLiveData<ResultState<List<DrawerEntity>>>()
        val drawerList: LiveData<ResultState<List<DrawerEntity>>> = _drawerList

        private val _drawerDetail = MutableLiveData<ResultState<DrawerEntity>>()
        val drawerDetail: LiveData<ResultState<DrawerEntity>> = _drawerDetail

        private val _drawerUpdate = MutableLiveData<ResultState<DrawerEntity>>()
        val drawerUpdate: LiveData<ResultState<DrawerEntity>> = _drawerUpdate

        private val _drawerUsers = MutableLiveData<ResultState<List<DrawerUser>>>()
        val drawerUsers: LiveData<ResultState<List<DrawerUser>>> = _drawerUsers

        private val _prodHistoryList = MutableLiveData<ResultState<List<ProdHistory>>>()
        val prodHistoryList: LiveData<ResultState<List<ProdHistory>>> = _prodHistoryList

        private val _drawerRegister = MutableLiveData<ResultState<DrawerEntity>>()
        val drawerRegister: LiveData<ResultState<DrawerEntity>> = _drawerRegister

        private val _drawerUnit = MutableLiveData<ResultState<DrawerUnitEntity>>()
        val drawerUnit: LiveData<ResultState<DrawerUnitEntity>> = _drawerUnit

        private val _drawerUnitUpdate = MutableLiveData<ResultState<DrawerUnitEntity>>()
        val drawerUnitUpdate: LiveData<ResultState<DrawerUnitEntity>> = _drawerUnitUpdate

        private val _drawerImage = MutableLiveData<ResultState<DrawerImageResponse>>()
        val drawerImage: LiveData<ResultState<DrawerImageResponse>> = _drawerImage

        fun getAllDrawers() {
            viewModelScope.launch {
                _drawerList.value = ResultState.Loading
                val result = drawerRepository.getAllDrawers()
                handleResult(_drawerList, result)
            }
        }

        fun getAllMyDrawers() {
            viewModelScope.launch {
                _myDrawerList.value = ResultState.Loading
                val result = drawerRepository.getAllMyDrawers()
                handleResult(_myDrawerList, result)
            }
        }

        fun getDrawerByDrawerId(drawerId: Int) {
            viewModelScope.launch {
                _drawerDetail.value = ResultState.Loading
                val result = drawerRepository.getDrawerByDrawerId(drawerId)
                handleResult(_drawerDetail, result)
            }
        }

        fun getUsersByDrawerId(drawerId: Int) {
            viewModelScope.launch {
                _drawerUsers.value = ResultState.Loading
                val result = drawerRepository.getUsersByDrawerId(drawerId)
                handleResult(_drawerUsers, result)
            }
        }

        fun getHistoryByUnitId(unitId: Int) {
            viewModelScope.launch {
                _prodHistoryList.value = ResultState.Loading
                val result = drawerRepository.getHistoryByUnitId(unitId)
                handleResult(_prodHistoryList, result)
            }
        }

        fun getHistoryByDrawerId(drawerId: Int) {
            viewModelScope.launch {
                _prodHistoryList.value = ResultState.Loading
                val result = drawerRepository.getHistoryByDrawerId(drawerId)
                handleResult(_prodHistoryList, result)
            }
        }

        fun registerDrawer(drawerId: Int) {
            viewModelScope.launch {
                _drawerRegister.value = ResultState.Loading
                val result = drawerRepository.registerDrawer(drawerId)
                handleResult(_drawerRegister, result)
            }
        }

        fun updateDrawer(drawerEntity: DrawerEntity) {
            viewModelScope.launch {
                _drawerUpdate.value = ResultState.Loading
                val result = drawerRepository.updateDrawer(drawerEntity)
                handleResult(_drawerUpdate, result)
            }
        }

        fun updateDrawerUnit(
            unitId: Int,
            drawerUnitEntity: DrawerUnitEntity,
        ) {
            viewModelScope.launch {
                _drawerUnitUpdate.value = ResultState.Loading
                val result = drawerRepository.updateDrawerUnit(unitId, drawerUnitEntity)
                handleResult(_drawerUnitUpdate, result)
            }
        }

        fun getDrawerUnitByUnitId(unitId: Int) {
            viewModelScope.launch {
                _drawerUnit.value = ResultState.Loading
                val result = drawerRepository.getDrawerUnitByUnitId(unitId)
                handleResult(_drawerUnit, result)
            }
        }

        fun registerDrawerImage(
            drawerId: Int,
            file: MultipartBody.Part,
        ) {
            viewModelScope.launch {
                _drawerImage.value = ResultState.Loading
                val result = drawerRepository.registerDrawerImage(drawerId, file)
                handleResult(_drawerImage, result)
            }
        }
    }
