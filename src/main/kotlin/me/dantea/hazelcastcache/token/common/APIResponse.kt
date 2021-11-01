package me.dantea.hazelcastcache.token.common

data class APIResponse<T>(
    val success: Boolean,
    val payload: T?,
    val error: APIError?
) {
    companion object {
        fun <T> success(payload: T) = APIResponse(true, payload, null)
        fun <T> failure(code: String, message: String) = failure<T>(APIError(code, message))
        private fun <T> failure(error: APIError) = APIResponse<T>(false, null, error)
    }
}