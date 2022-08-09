package com.example.exercise_thread

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private val random = Random()
    private var number = 0
    lateinit var textNumber: TextView
    private lateinit var handler: Handler
    private var y1: Float = 0f
    private var y2: Float = 0f
    private var tg = 0f
    private var kc: Int = 0
    private var n: Int = 0
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private var isPlus = true
    private var isUpdate = false
    private var isZero = false
    private var isRun = false
    private var isSwipe = false

    private var isUp = false
    private var isDown = false


    private var isTouch = false

    private val swipeThread = Thread {
        while (true) {
            if (isSwipe) {
                handler.sendEmptyMessage(MSG_UPDATE_NUMBER)
                Thread.sleep(80)
            }
        }
    }

    private val updateNumberThread = Thread {
        while (true) {
            if (isRun) {
                if (number > 0) {
                    isPlus = false
                } else if (number < 0) {
                    isPlus = true
                }
            }
        }
    }

    private val mThread = Thread {
        while (true) {
            if (isUpdate) {
                if (!isTouch) {
                    countDownTimerLongClick.cancel()
                    handler.sendEmptyMessage(MSG_UPDATE_NUMBER)
                }

            }
            Thread.sleep(60)
        }
    }

    private var countDownTimerSwipe = object : CountDownTimer(1, 1) {
        override fun onTick(p0: Long) {
            val tag = "Main"
            Log.i(tag, "Current tick: $p0")
        }

        override fun onFinish() {
            isSwipe = false
        }
    }

    private var countDownTimer = object : CountDownTimer(2000, 500) {
        override fun onTick(p0: Long) {
            val tag = "Main"
            Log.i(tag, "Current tick: $p0")
            isUpdate = false
        }

        override fun onFinish() {
            isPlus = !isPlus
            isUpdate = true
            isZero = true
            isTouch = false
            isRun = true
        }
    }


    private var countDownTimerLongClick = object : CountDownTimer(1000, 500) {
        override fun onTick(p0: Long) {
            isUpdate = false
        }

        override fun onFinish() {
            isUpdate = true
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listenerHandler()

        handleView()
        mThread.start()
        updateNumberThread.start()
        swipeThread.start()


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleView() {
        textNumber = findViewById(R.id.textview_showing_number)
        textNumber.setOnTouchListener { view, event ->
            textNumber.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    y1 = event.y
                    tg = y1
                    Toast.makeText(this@MainActivity, "$y1 $tg", Toast.LENGTH_SHORT).show()
                    isUpdate = false
                    isRun = false
                    isZero = false
                    isTouch = true
                }

                MotionEvent.ACTION_UP -> {
                    isUpdate = false
                    isTouch = false
                    isSwipe = false
                    countDownTimer.cancel()
                    countDownTimer.start()
                }

                MotionEvent.ACTION_MOVE -> {
                    y2 = event.y
                    Log.e("I", "$tg $y2")
                    isUpdate = false
                    if (y2 > tg) {
                        tg = y2
                        isPlus = false
                        isSwipe = true

                        countDownTimerSwipe.start()

                    } else if (y2 < tg) {
                        tg = y2
                        isPlus = true
                        isSwipe = true
                        countDownTimerSwipe.start()
                    }

                    Log.e("II", "$tg $y2")
//                    if (tg == y2) {
//                        isSwipe = false
//                    }

                }
            }

            true
        }


        btnPlus = findViewById(R.id.btnPlus)
        btnPlus.setOnClickListener {
            isRun = false
            isUpdate = false
            isTouch = false
            isPlus = true
            isZero = false
            updateNumber()
            countDownTimerLongClick.cancel()
            countDownTimer.cancel()
            countDownTimer.start()
        }

        btnPlus.setOnLongClickListener {
            isUpdate = false
            isRun = false
            isPlus = true
            isTouch = false
            countDownTimer.cancel()
            countDownTimerLongClick.cancel()
            countDownTimerLongClick.start()

            isUpdate = true
            isZero = false
            false
        }

        btnMinus = findViewById(R.id.btnMinus)
        btnMinus.setOnClickListener {
            isRun = false
            isUpdate = false
            isPlus = false
            isZero = false
            isTouch = false
            updateNumber()
            countDownTimerLongClick.cancel()
            countDownTimer.cancel()
            countDownTimer.start()
        }

        btnMinus.setOnLongClickListener {
            isUpdate = false
            isRun = false
            isPlus = false

            countDownTimer.cancel()
            countDownTimerLongClick.cancel()
            countDownTimerLongClick.start()
            isTouch = false

            isUpdate = true
            isZero = false
            false
        }


    }

    private fun listenerHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_UPDATE_NUMBER -> {
                        updateNumber()
                    }

                    MSG_UPDATE_NUMBER_DONE -> {

                    }
                }
            }
        }
    }

    private fun updateNumber() {
        if (isZero && number == 0) {
            return
        }

        if (isPlus) {
            number++
        } else {
            number--
        }

        val color = Color.argb(255, random.nextInt(50), random.nextInt(100), random.nextInt(150))
        textNumber.text = number.toString()
        textNumber.setTextColor(color)

    }

    companion object {
        private const val MSG_UPDATE_NUMBER = 100
        private const val MSG_UPDATE_NUMBER_DONE = 101
    }
}

