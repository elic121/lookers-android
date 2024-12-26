package com.example.lookers.model.entity.prod

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProdEntity(
    val id: Int,
    val prodType: ProdType,
    val drawerUnitId: Int,
    val prodNames: List<ProdName>,
    val imageUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val positionX: Double,
    val positionY: Double,
    val rotate: Double
) : Parcelable