package dev.jskrzypczak.photovault.core.network

/** Provides the current backend base URL for every outgoing Ktor request. */
interface BaseUrlProvider {
    fun current(): String
}
