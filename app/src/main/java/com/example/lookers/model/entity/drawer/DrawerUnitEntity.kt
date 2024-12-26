package com.example.lookers.model.entity.drawer

import android.os.Parcelable
import com.example.lookers.model.entity.prod.ProdEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrawerUnitEntity(
    val id: Int,
    val drawerUnitName: String,
    val prods: List<ProdEntity>,
    val updatedAt: String,
) : Parcelable
