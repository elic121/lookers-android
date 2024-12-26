package com.example.lookers.repository

import com.example.lookers.model.entity.drawer.DrawerEntity
import com.example.lookers.model.entity.drawer.DrawerImageResponse
import com.example.lookers.model.entity.drawer.DrawerUnitEntity
import com.example.lookers.model.entity.drawer.DrawerUser
import com.example.lookers.model.entity.prod.ProdHistory
import com.example.lookers.model.network.DrawerService
import com.example.lookers.util.makeRequest
import okhttp3.MultipartBody
import javax.inject.Inject

class DrawerRepository
    @Inject
    constructor(
        private val drawerService: DrawerService,
    ) {
        suspend fun getAllDrawers(): Result<List<DrawerEntity>> = makeRequest { drawerService.getAllDrawers().execute() }

        suspend fun getAllMyDrawers(): Result<List<DrawerEntity>> = makeRequest { drawerService.getAllMyDrawers().execute() }

        suspend fun getDrawerByDrawerId(drawerId: Int): Result<DrawerEntity> =
            makeRequest { drawerService.getDrawerByDrawerId(drawerId).execute() }

        suspend fun getUsersByDrawerId(drawerId: Int): Result<List<DrawerUser>> =
            makeRequest {
                drawerService.getUsersByDrawerId(drawerId).execute()
            }

        suspend fun getHistoryByDrawerId(drawerId: Int): Result<List<ProdHistory>> =
            makeRequest {
                drawerService.getHistoryByDrawerId(drawerId).execute()
            }

        suspend fun getHistoryByUnitId(unitId: Int): Result<List<ProdHistory>> =
            makeRequest { drawerService.getHistoryByUnitId(unitId).execute() }

        suspend fun registerDrawer(drawerId: Int): Result<DrawerEntity> = makeRequest { drawerService.registerDrawer(drawerId).execute() }

        suspend fun updateDrawer(drawerEntity: DrawerEntity): Result<DrawerEntity> =
            makeRequest {
                drawerService.updateDrawer(drawerEntity.drawerId, drawerEntity).execute()
            }

        suspend fun updateDrawerUnit(
            unitId: Int,
            drawerUnitEntity: DrawerUnitEntity,
        ): Result<DrawerUnitEntity> =
            makeRequest {
                drawerService.updateDrawerUnit(unitId, drawerUnitEntity).execute()
            }

        suspend fun getDrawerUnitByUnitId(unitId: Int): Result<DrawerUnitEntity> =
            makeRequest {
                drawerService.getDrawerUnitByUnitId(unitId).execute()
            }

        suspend fun registerDrawerImage(
            drawerId: Int,
            file: MultipartBody.Part,
        ): Result<DrawerImageResponse> =
            makeRequest {
                drawerService.registerDrawerImage(drawerId, file).execute()
            }
    }
