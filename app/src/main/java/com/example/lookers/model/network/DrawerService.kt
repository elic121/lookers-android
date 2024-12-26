package com.example.lookers.model.network

import com.example.lookers.model.entity.drawer.DrawerEntity
import com.example.lookers.model.entity.drawer.DrawerImageResponse
import com.example.lookers.model.entity.drawer.DrawerUnitEntity
import com.example.lookers.model.entity.drawer.DrawerUser
import com.example.lookers.model.entity.prod.ProdHistory
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface DrawerService {
    @GET("drawer")
    fun getAllDrawers(): Call<List<DrawerEntity>>

    @GET("drawer/my")
    fun getAllMyDrawers(): Call<List<DrawerEntity>>

    @GET("drawer/{drawerId}")
    fun getDrawerByDrawerId(
        @Path("drawerId") drawerId: Int,
    ): Call<DrawerEntity>

    @POST("drawer/{drawerId}")
    fun registerDrawer(
        @Path("drawerId") drawerId: Int,
    ): Call<DrawerEntity>

    @Multipart
    @POST("drawer/{drawerId}/image")
    fun registerDrawerImage(
        @Path("drawerId") drawerId: Int,
        @Part file: MultipartBody.Part,
    ): Call<DrawerImageResponse>

    @PATCH("drawer/{drawerId}")
    fun updateDrawer(
        @Path("drawerId") drawerId: Int,
        @Body drawerEntity: DrawerEntity,
    ): Call<DrawerEntity>

    @GET("drawer/{drawerId}/users")
    fun getUsersByDrawerId(
        @Path("drawerId") drawerId: Int,
    ): Call<List<DrawerUser>>

    @GET("drawer/{drawerId}/history")
    fun getHistoryByDrawerId(
        @Path("drawerId") drawerId: Int,
    ): Call<List<ProdHistory>>

    @GET("drawer/unit/{unitId}/history")
    fun getHistoryByUnitId(
        @Path("unitId") unitId: Int,
    ): Call<List<ProdHistory>>

    @PATCH("drawer/unit/{unitId}")
    fun updateDrawerUnit(
        @Path("unitId") unitId: Int,
        @Body drawerUnitEntity: DrawerUnitEntity,
    ): Call<DrawerUnitEntity>

    @GET("drawer/unit/{unitId}")
    fun getDrawerUnitByUnitId(
        @Path("unitId") unitId: Int,
    ): Call<DrawerUnitEntity>
}
