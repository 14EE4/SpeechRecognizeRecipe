package com.example.speechrecognizerecipe

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechRecognizerIntent: Intent? = null
    private var logView: TextView? = null
    private var button: Button? = null

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var logBuilder: StringBuilder = StringBuilder() // 로그를 저장할 StringBuilder
    private var logCount = 0 // 로그 카운트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logView = findViewById(R.id.logView)
        button = findViewById(R.id.button)

        checkPermissions()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR") // 한국어 설정

        speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                logView?.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            }

            override fun onBeginningOfSpeech() {
                logView?.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_light)) // 녹색으로 변경
            }

            override fun onRmsChanged(rmsdB: Float) {
                // 음성의 강도가 변경됨
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // 음성 버퍼 수신
            }

            override fun onEndOfSpeech() {
                logView?.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.white)) // 원래 색으로 되돌림
            }

            override fun onError(error: Int) {
                logBuilder.append("Error: $error\n")
                logView?.text = logBuilder.toString()
            }

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let {
                    logCount++ // 로그 카운트 증가
                    logBuilder.append("$logCount: ${it[0]}\n") // 결과 앞에 숫자를 붙임
                    logView?.text = logBuilder.toString() // 로그 표시
                }
            }

            override fun onPartialResults(partialResults: Bundle) {
                // 부분 결과
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // 이벤트 발생
            }
        })

        button?.setOnClickListener {
            if (speechRecognizer != null) {
                speechRecognizer!!.startListening(speechRecognizerIntent)
            } else {
                logView?.text = "음성 인식 서비스가 초기화되지 않았습니다."
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우
            } else {
                // 권한이 거부된 경우
                logView?.text = "음성 인식을 사용하려면 마이크 권한이 필요합니다."
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}
