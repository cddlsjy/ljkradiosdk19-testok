package com.yuriy.openradio.shared.dependencies

import com.yuriy.openradio.shared.presenter.MediaPresenter

interface MediaPresenterDependency {
    fun configureWith(mediaPresenter: MediaPresenter)
}
