package com.utsman.mavericks.core

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform