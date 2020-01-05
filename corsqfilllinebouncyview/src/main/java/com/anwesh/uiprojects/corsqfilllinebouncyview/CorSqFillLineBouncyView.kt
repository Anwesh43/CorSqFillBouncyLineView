package com.anwesh.uiprojects.corsqfilllinebouncyview

/**
 * Created by anweshmishra on 05/01/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Color
import android.content.Context
import android.app.Activity

val nodes : Int = 5
val lines : Int = 4
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#673AB7")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
fun Float.cosify() : Float = 1f - Math.sin(Math.PI / 2 + (Math.PI / 2) * this).toFloat()

fun Canvas.drawCorSqFillLine(i : Int, size : Float, scale : Float, paint : Paint) {
    val sf : Float = scale.sinify().divideScale(i, lines)
    save()
    rotate(90f * i)
    save()
    translate(-size, -size)
    rotate(90f * sf)
    drawLine(0f, 0f, size, 0f, paint)
    restore()
    restore()
}

fun Canvas.drawCorFillSquare(size : Float, scale : Float, paint : Paint) {
    val sc : Float = scale.divideScale(0, 2).cosify()
    for (j in 0..(lines - 1)) {
        drawCorSqFillLine(j, size, scale, paint)
    }
    val sr : Float = size * sc
    drawRect(RectF(-sr, -sr, sr, sr), paint)
}

fun Canvas.drawCSFLBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawCorFillSquare(size, scale, paint)
    restore()
}
