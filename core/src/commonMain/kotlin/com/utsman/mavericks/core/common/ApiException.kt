package com.utsman.mavericks.core.common

class ApiException(private val code: Int, private val errorResponse: Any) : Throwable() {
    override val message: String
        get() {
            return "{\"code\": $code, \"error_response\": $errorResponse, \"message\": ${cause?.message}}"
        }
}