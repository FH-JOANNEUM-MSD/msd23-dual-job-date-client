package fh.msd.jobdating.core.network

fun Throwable.isNetworkError(): Boolean {
    val name = this::class.simpleName ?: ""
    val msg = message ?: ""
    return name.contains("UnknownHost") ||
        name.contains("ConnectTimeout") ||
        name.contains("SocketTimeout") ||
        name.contains("NoRouteToHost") ||
        name.contains("ConnectionRefused") ||
        name.contains("NetworkException") ||
        name.contains("IOException") ||
        msg.contains("Unable to resolve host", ignoreCase = true) ||
        msg.contains("Failed to connect", ignoreCase = true) ||
        msg.contains("No address associated", ignoreCase = true) ||
        msg.contains("Network is unreachable", ignoreCase = true) ||
        cause?.isNetworkError() == true
}
