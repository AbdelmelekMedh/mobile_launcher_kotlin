package com.hellodati.launcher.animation

import android.view.animation.Animation
import android.view.animation.Transformation

class MosaicAnimation : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val matrix = t.matrix
        matrix.setScale(interpolatedTime, interpolatedTime)
    }
}