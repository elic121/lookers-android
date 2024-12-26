package com.example.lookers.model.entity.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.example.lookers.model.entity.prod.ProdType

@Parcelize
data class SearchProdEntity(
    val id: Int,
    val imageUrl: String,
    val names: List<String>,
    val prodType: ProdType,

    val drawerId: Int,
    val drawerName: String,

    val drawerUnitId: Int,
    val drawerUnitName: String,
) : Parcelable
