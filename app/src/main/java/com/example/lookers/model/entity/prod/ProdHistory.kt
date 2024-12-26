package com.example.lookers.model.entity.prod

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProdHistory(
    val id: Int,
    val prodId: Int,
    val prodType: ProdType,
    val drawerUnitId: Int,
    val prodNames: List<ProdName>,
    val prodAccessType: String,
    val imageUrl: String,
    val createdAt: String,
    val updatedAt: String
) : Parcelable
