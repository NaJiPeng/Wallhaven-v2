package com.njp.wallhaven.utils

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import com.tencent.tauth.Tencent
import android.os.Bundle
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD
import com.tencent.connect.share.QzoneShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError


class TencentUtil private constructor() {

    companion object {
        private lateinit var context: Application
        private var instance: TencentUtil? = null

        fun init(context: Application) {
            this.context = context
        }

        fun getInstance(): TencentUtil {
            if (instance == null) {
                instance = TencentUtil()
            }
            return instance!!
        }
    }

    private val tencent = Tencent.createInstance("101498847", context)
    private val api = WXAPIFactory.createWXAPI(context, "wx95718f1afcc04b24", true)

    init {
        api.registerApp("wx95718f1afcc04b24")
    }


    private val listener = object : IUiListener {
        override fun onComplete(p0: Any?) {
        }

        override fun onCancel() {
        }

        override fun onError(p0: UiError?) {
        }

    }

    fun shareImageToQQ(activity: Activity, path: String) {
        Bundle().apply {
            putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
            putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path)
            putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE)
        }.let {
            tencent.shareToQQ(activity, it, listener)
        }
    }

    fun shareImageToQzone(activity: Activity, path: String) {
        Bundle().apply {
            putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD)
            putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, ArrayList<String>(listOf(path)))
        }.let {
            tencent.publishToQzone(activity, it, listener)
        }
    }

    fun shareImageToWechat(bitmap: Bitmap) {
        SendMessageToWX.Req().apply {
            message = WXMediaMessage().apply {
                mediaObject = WXImageObject(bitmap)
            }
            scene = SendMessageToWX.Req.WXSceneSession
        }.let {
            api.sendReq(it)
        }
    }

    fun shareImageToCircleOfFriend(bitmap: Bitmap) {
        SendMessageToWX.Req().apply {
            message = WXMediaMessage().apply {
                mediaObject = WXImageObject(bitmap)
            }
            scene = SendMessageToWX.Req.WXSceneTimeline
        }.let {
            api.sendReq(it)
        }
    }


}