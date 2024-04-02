package com.willkopec.whalert.model.polygon

data class PolygonResponse(
    val adjusted: Boolean,
    val count: Int,
    val queryCount: Int,
    val request_id: String,
    val results: List<Result>,
    val resultsCount: Int,
    val status: String,
    val ticker: String
)