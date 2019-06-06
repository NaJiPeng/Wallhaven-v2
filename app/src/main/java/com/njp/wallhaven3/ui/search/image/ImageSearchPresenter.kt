package com.njp.wallhaven3.ui.search.image

import android.content.Context
import android.util.Log
import com.njp.wallhaven3.base.BasePresenter
import com.njp.wallhaven3.repositories.Repository
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

class ImageSearchPresenter(view: ImageSearchContract.View) : BasePresenter<ImageSearchContract.View>(view), ImageSearchContract.Presenter {

    override fun getImages(context: Context, path: String) {
        val file = Compressor(context)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .compressToFile(File(path))
        Repository.getInstance().searchByImage(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { if (it.isEmpty()) view?.onGetNoImages() else view?.onGetImages(it) },
                        {
                            view?.onGetImagesFail("网络连接失败 Q_Q")
                            Log.e("wwww", "error", it)
                        }
                )?.let { addDisposable(it) }
    }

}