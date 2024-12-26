package com.example.lookers.model.entity.drawer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawerEntity(
    val drawerId: Int,
    val imageUrl: String,
    val drawerName: String,
    val drawerUnits: List<DrawerUnitEntity>,
    val backgroundColor: String,
) : Parcelable
