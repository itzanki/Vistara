package com.example.vistara

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.vistara.databinding.ActivityColourrecognitionBinding
import java.util.*
import kotlin.random.Random

class ColorRecognition : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityColourrecognitionBinding
    private lateinit var tts: TextToSpeech
    private val colors = listOf(
        ColorItem("Red", Color.RED),
        ColorItem("Green", Color.GREEN),
        ColorItem("Blue", Color.BLUE),
        ColorItem("Yellow", Color.YELLOW),
        ColorItem("Magenta", Color.MAGENTA),
        ColorItem("Cyan", Color.CYAN)
    )
    private var currentColorIndex = 0
    private var score = 0
    private var currentColorName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityColourrecognitionBinding.inflate(layoutInflater)
            setContentView(binding.root)

            tts = TextToSpeech(this, this)
            setupUI()
            startTest()
        } catch (e: Exception) {
            Log.e("ColorRecognition", "Initialization error", e)
            Toast.makeText(this, "Error initializing test", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupUI() {
        try {
            with(binding) {
                progressBar.max = colors.size * 10
                btnSpeak.setOnClickListener { speakCurrentColor() }
                btnSubmit.setOnClickListener { checkAnswer() }
                btnSkip.setOnClickListener { skipToNext() }
                btnRestart.setOnClickListener { restartTest() }
                btnHome.setOnClickListener { finish() }
            }
        } catch (e: Exception) {
            Log.e("ColorRecognition", "UI setup error", e)
            Toast.makeText(this, "UI setup failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTest() {
        binding.tvInstruction.text = "What color do you see?"
        showRandomColor()
        binding.etAnswer.requestFocus()
    }

    private fun showRandomColor() {
        currentColorIndex = Random.nextInt(colors.size)
        val colorItem = colors[currentColorIndex]
        currentColorName = colorItem.name

        binding.colorDisplay.setBackgroundColor(colorItem.color)
        binding.colorDisplay.visibility = View.INVISIBLE

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { value ->
            binding.colorDisplay.alpha = value.animatedValue as Float
            if (value.animatedFraction > 0.5f && binding.colorDisplay.visibility != View.VISIBLE) {
                binding.colorDisplay.visibility = View.VISIBLE
            }
        }
        animator.start()
    }

    private fun speakCurrentColor() {
        tts.speak("What color is this?", TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun checkAnswer() {
        val userAnswer = binding.etAnswer.text.toString().trim()
        if (userAnswer.equals(currentColorName, ignoreCase = true)) {
            score += 10
            showFeedback(true)
            nextLevel()
        } else {
            showFeedback(false)
        }
    }

    private fun showFeedback(isCorrect: Boolean) {
        binding.tvFeedback.text = if (isCorrect) {
            "✅ Correct! You identified the color correctly."
        } else {
            "❌ Incorrect. The correct color was $currentColorName."
        }
    }

    private fun nextLevel() {
        if (score < colors.size * 10) {
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
        val percentage = (score * 100) / (colors.size * 10)
        val message = when {
            percentage >= 80 -> "Excellent color recognition! No issues detected."
            percentage >= 50 -> "Some color recognition difficulties detected."
            else -> "Significant color recognition issues detected. Please consult a specialist."
        }

        binding.testContainer.visibility = View.GONE
        binding.resultContainer.visibility = View.VISIBLE
        binding.tvResult.text = "Your score: $percentage%\n$message"
        binding.progressBar.progress = percentage
    }

    private fun restartTest() {
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
            Log.e("ColorRecognition", "TTS initialization failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            tts.shutdown()
        } catch (e: Exception) {
            Log.e("ColorRecognition", "Error shutting down TTS", e)
        }
    }

    data class ColorItem(val name: String, val color: Int)
}