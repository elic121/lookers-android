package com.example.lookers.model.entity.prod

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProdInfo(
    val id: Int,
    val prodNames: List<ProdName>,
    val imageUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val prodType: ProdType
) : Parcelable {
    companion object {
        fun fromEntity(entity: ProdEntity): ProdInfo {
            return ProdInfo(
                id = entity.id,
                prodNames = entity.prodNames,
                imageUrl = entity.imageUrl,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                prodType = entity.prodType
            )
        }

        fun fromHistory(entity: ProdHistory): ProdInfo {
            return ProdInfo(
                id = entity.prodId,
                prodNames = entity.prodNames,
                imageUrl = entity.imageUrl,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                prodType = entity.prodType
            )
        }
    }
}
