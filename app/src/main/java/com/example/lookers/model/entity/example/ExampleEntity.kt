package com.example.lookers.model.entity.example

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "example")
data class ExampleEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    @SerializedName("X-Cloud-Trace-Context")
    val xCloudTraceContext: String? = "",

    @SerializedName("traceparent")
    val traceparent: String? = "",

    @SerializedName("User-Agent")
    val userAgent: String? = "",

    @SerializedName("Host")
    val host: String? = "",
): Parcelable
