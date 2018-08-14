package com.njp.wallhaven.repositories.network

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

/**
 * 带进度的网络响应
 */
class ProgressResponseBody(private val url: String, private val responseBody: ResponseBody) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null
    private val progressListener: ((Int) -> Unit)? = ProgressInterceptor.getListener(url)


    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(ProgressSource(responseBody.source()))
        }
        return bufferedSource!!
    }

    private inner class ProgressSource(delegate: Source) : ForwardingSource(delegate) {

        private var totalBytesRead = 0L

        private var currentProgress = 0

        override fun read(sink: Buffer?, byteCount: Long): Long {
            val bytesRead = super.read(sink, byteCount)
            totalBytesRead = if (bytesRead == -1L) contentLength() else totalBytesRead + bytesRead
            val progress = (100f * totalBytesRead / contentLength()).toInt()
            if (currentProgress != progress) {
                currentProgress = progress
                progressListener?.invoke(progress)
            }
            if (totalBytesRead == contentLength()) {
                ProgressInterceptor.removeListener(url)
            }
            return bytesRead

        }

    }
}