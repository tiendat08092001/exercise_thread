package com.example.exercise_thread


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var numberIsShowing: Int = 0
    lateinit var textNumber: TextView
    private lateinit var handler: Handler
    private var isUpdate: Boolean = false
    private var isClickPlusButton: Boolean = false
    private var isClickMinusButton: Boolean = false
    private lateinit var clickPlusBTNThread: Thread
    private lateinit var clickMinusBTNThread: Thread
    private lateinit var touchMinusThread: Thread
    private lateinit var touchPlusThread: Thread
    private lateinit var autoUpdateNumber: Thread
    private var y1: Float = 0F
    private var y2: Float = 0F


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listenerHandler()
        initThread()
        textNumber = findViewById(R.id.textview_showing_number)
        textNumber.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    y1 = motionEvent.y
                    isUpdate = false
                    autoUpdateNumber.interrupt()
                }

                MotionEvent.ACTION_UP -> {
                    isClickPlusButton = false
                    isClickMinusButton = false
                    isUpdate = true
                    autoUpdateNumber.start()
                    touchPlusThread.interrupt()
                    touchMinusThread.interrupt()
                }

                MotionEvent.ACTION_MOVE -> {
                    y2 = motionEvent.y

                    if (y1 > y2) {
                        isClickPlusButton = true
                        isUpdate = false
                        touchPlusThread.start()
                        autoUpdateNumber.interrupt()

                    } else if (y1 < y2) {
                        isClickMinusButton = true
                        isUpdate = false
                        touchMinusThread.start()
                        autoUpdateNumber.interrupt()
                    }
                }


            }


            true
        }

        val btnPlus = findViewById<Button>(R.id.btnPlus)

        btnPlus.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isUpdate = false
                    autoUpdateNumber.interrupt()
                    isClickPlusButton = true
                    clickPlusBTNThread.start()
                }

                MotionEvent.ACTION_UP -> {
                    numberIsShowing += 1
                    textNumber.text = numberIsShowing.toString()
                    isUpdate = true
                    isClickPlusButton = false
                    clickPlusBTNThread.interrupt()
                    autoUpdateNumber.start()
                }
            }
            true
        }


        val btnMinus = findViewById<Button>(R.id.btnMinus)
        btnMinus.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isUpdate = false
                    isClickMinusButton = true
                    clickMinusBTNThread.start()
                    autoUpdateNumber.interrupt()
                }

                MotionEvent.ACTION_UP -> {
                    numberIsShowing -= 1
                    textNumber.text = numberIsShowing.toString()
                    isUpdate = true
                    isClickMinusButton = false
                    clickMinusBTNThread.interrupt()
                    autoUpdateNumber.start()

                }
            }
            true
        }

    }


    private fun listenerHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_UPDATE_NUMBER -> {
                        textNumber.text = msg.arg1.toString()
                    }

                    MSG_UPDATE_NUMBER_DONE -> {

                    }
                }
            }
        }
    }


    private fun plusThread(timeSleep: Long, delay: Long, isTouch: Boolean = false): Thread {
        val a = Thread {
            Thread.sleep(delay)
            while (true) {
                if (!isClickPlusButton) {
                    break
                }
                numberIsShowing += 1
                val message = Message()
                message.what = MSG_UPDATE_NUMBER
                message.arg1 = numberIsShowing
                handler.sendMessage(message)
                Thread.sleep(timeSleep)

                if (isTouch) {
                    break
                }
            }
        }
        return a
    }

    private fun minusThread(timeSleep: Long, delay: Long, isTouch: Boolean = false): Thread {
        val a = Thread {
            Thread.sleep(delay)
            while (true) {
                if (!isClickMinusButton) {
                    break
                }
                numberIsShowing -= 1
                val message = Message()
                message.what = MSG_UPDATE_NUMBER
                message.arg1 = numberIsShowing
                handler.sendMessage(message)
                Thread.sleep(timeSleep)

                if (isTouch) {
                    break
                }
            }

        }
        return a
    }

    private fun autoUpdateThread(): Thread {
        val a = Thread {
            Thread.sleep(2000)
            while (isUpdate) {
                if (numberIsShowing == 0) {
                    break
                }

                if (numberIsShowing > 0) {
                    val message = Message()
                    numberIsShowing -= 1
                    message.arg1 = numberIsShowing
                    message.what = MSG_UPDATE_NUMBER
                    handler.sendMessage(message)
                    Thread.sleep(50)
                } else if (numberIsShowing < 0) {
                    val message = Message()
                    numberIsShowing += 1
                    message.arg1 = numberIsShowing
                    message.what = MSG_UPDATE_NUMBER
                    handler.sendMessage(message)
                    Thread.sleep(50)
                }


            }
            handler.sendEmptyMessage(MSG_UPDATE_NUMBER_DONE)
        }
        return a
    }

    private fun initThread() {
        clickPlusBTNThread = plusThread(50, 1500, false)
        clickMinusBTNThread = minusThread(50, 1500, false)
        touchMinusThread = minusThread(50, 0, true)
        touchPlusThread = plusThread(50, 0, true)
        autoUpdateNumber = autoUpdateThread()
    }

    companion object {
        private const val MSG_UPDATE_NUMBER = 100
        private const val MSG_UPDATE_NUMBER_DONE = 101
    }
}