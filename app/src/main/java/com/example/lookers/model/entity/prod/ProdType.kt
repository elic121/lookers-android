package com.example.lookers.model.entity.prod

enum class ProdType(
    private val korName: String,
) {
    FOOD("식품"),
    ELECTRONICS("전자기기"),
    BOOKS("서적"),
    STATIONERY("문구"),
    CLOTHING("의류"),
    COSMETICS("화장품"),
    KITCHENWARE("주방용품"),
    TOYS("장난감"),
    UNKNOWN("기타"),
    ;

    override fun toString(): String = korName
}
