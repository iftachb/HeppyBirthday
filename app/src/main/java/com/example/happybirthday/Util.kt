package com.example.happybirthday

import android.content.res.Resources

fun getPositionByAngelX(radius: Int, angle: Int): Int {
    val radians = Math.toRadians(angle.toDouble())
    val x: Double = radius * Math.cos(radians) + 0
    return x.toInt()
}
fun getPositionByAngelY(radius: Int, angle: Int): Int {
    val radians = Math.toRadians(angle.toDouble())
    val y: Double = radius * Math.sin(radians) + 0
    return y.toInt()
}

fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()