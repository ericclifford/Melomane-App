package main.app.logging

data class LogData(
    val priority: String,
    val tag: String?,
    val message: String,
    val throwable: String,
    val time: String
)