package com.example.lookers.model.entity

enum class FcmCall(private val type: String) {
    REGISTER("register");

    override fun toString(): String {
        return type
    }

    companion object {
        fun fromType(type: String): FcmCall? {
            return entries.find { it.type == type }
        }
    }
}
