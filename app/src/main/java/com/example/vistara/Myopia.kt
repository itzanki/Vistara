package com.example.vistara

import android.animation.ValueAnimator
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.vistara.databinding.ActivityMyopiaBinding
import java.util.*

class Myopia : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityMyopiaBinding
    private lateinit var tts: TextToSpeech
    private val snellenChart = listOf(
        SnellenRow("E", 100f),
        SnellenRow("FP", 80f),
        SnellenRow("TOZ", 60f),
        SnellenRow("LPED", 40f),
        SnellenRow("PECFD", 30f),
        SnellenRow("EDFCZP", 20f)
    )
    private var currentLevel = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Myopia", "Activity created")

        try {
            // Initialize Data Binding
            binding = DataBindingUtil.setContentView(this, R.layout.activity_myopia)

            // Initialize TTS
            tts = TextToSpeech(this, this)

            setupUI()
            startTest()
        } catch (e: Exception) {
            Log.e("Myopia", "Initialization error", e)
            Toast.makeText(this, "Error initializing test", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupUI() {
        try {
            with(binding) {
                progressBar.max = snellenChart.size * 10
                btnSpeak.setOnClickListener { speakCurrentRow() }
                btnSubmit.setOnClickListener { checkAnswer() }
                btnSkip.setOnClickListener { skipToNext() }
                btnRestart.setOnClickListener { restartTest() }
                btnHome.setOnClickListener { finish() }
            }
        } catch (e: Exception) {
            Log.e("Myopia", "UI setup error", e)
            Toast.makeText(this, "UI setup failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTest() {
        binding.tvInstruction.text = "Stand 10 feet away and read the letters"
        animateTextSize(snellenChart[currentLevel])
        binding.etAnswer.requestFocus()
    }

    private fun animateTextSize(row: SnellenRow) {
        binding.tvTestText.text = row.text
        binding.tvTestText.visibility = View.INVISIBLE

        val animator = ValueAnimator.ofFloat(10f, row.size)
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { value ->
            binding.tvTestText.textSize = value.animatedValue as Float
            if (value.animatedFraction > 0.5f && binding.tvTestText.visibility != View.VISIBLE) {
                binding.tvTestText.visibility = View.VISIBLE
            }
        }
        animator.start()
    }

    private fun speakCurrentRow() {
        tts.speak("Can you read this? ${snellenChart[currentLevel].text}",
            TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun checkAnswer() {
        val userAnswer = binding.etAnswer.text.toString().trim()
        if (userAnswer.equals(snellenChart[currentLevel].text, ignoreCase = true)) {
            score += 10
            showFeedback(true)
            nextLevel()
        } else {
            showFeedback(false)
        }
    }

    private fun showFeedback(isCorrect: Boolean) {
        binding.tvFeedback.text = if (isCorrect) {
            "✅ Correct! Your vision at this distance is good."
        } else {
            "❌ Incorrect. Let's try again."
        }
    }

    private fun nextLevel() {
        if (currentLevel < snellenChart.size - 1) {
            currentLevel++
            binding.etAnswer.text?.clear()
            startTest()
        } else {
            finishTest()
        }
    }

    private fun skipToNext() {
        nextLevel()
    }

    private fun finishTest() {
        val percentage = (score * 100) / (snellenChart.size * 10)
        val message = when {
            percentage >= 80 -> "Excellent vision! No signs of myopia detected."
            percentage >= 50 -> "Mild myopia may be present. Consider an eye exam."
            else -> "Significant myopia detected. Please consult an optometrist."
        }

        binding.testContainer.visibility = View.GONE
        binding.resultContainer.visibility = View.VISIBLE
        binding.tvResult.text = "Your score: $percentage%\n$message"
        binding.progressBar.progress = percentage
    }

    private fun restartTest() {
        currentLevel = 0
        score = 0
        binding.testContainer.visibility = View.VISIBLE
        binding.resultContainer.visibility = View.GONE
        binding.progressBar.progress = 0
        startTest()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
            Log.e("Myopia", "TTS initialization failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            tts.shutdown()
        } catch (e: Exception) {
            Log.e("Myopia", "Error shutting down TTS", e)
        }
    }

    data class SnellenRow(val text: String, val size: Float)
}