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
    rotate(-90f * sf)
    drawLine(0f, 0f, 2 * size, 0f, paint)
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
    drawCorFillSquare(size / 2, scale, paint)
    restore()
}

class CorSqFillLineBouncyView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CSFLBNode(var i : Int, val state : State = State()) {

        private var next : CSFLBNode? = null
        private var prev : CSFLBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = CSFLBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawCSFLBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CSFLBNode {
            var curr : CSFLBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class CorSqFillLine(var i : Int) {

        private val root : CSFLBNode = CSFLBNode(0)
        private var curr : CSFLBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : CorSqFillLineBouncyView) {

        private val animator : Animator = Animator(view)
        private val csflb : CorSqFillLine = CorSqFillLine(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            csflb.draw(canvas, paint)
            animator.animate {
                csflb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            csflb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : CorSqFillLineBouncyView {
            val view : CorSqFillLineBouncyView = CorSqFillLineBouncyView(activity)
            activity.setContentView(view)
            return view
        }
    }
}