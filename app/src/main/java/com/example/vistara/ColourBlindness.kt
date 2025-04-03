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
import com.example.vistara.databinding.ActivityColourblindnessBinding
import java.util.*

class ColourBlindness : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityColourblindnessBinding
    private lateinit var tts: TextToSpeech
    private val ishiharaPlates = listOf(
        IshiharaPlate("12", R.drawable.ishihara_12),
        IshiharaPlate("42", R.drawable.ishihara_42),
        IshiharaPlate("6", R.drawable.ishihara_6),
        IshiharaPlate("29", R.drawable.ishihara_29),
        IshiharaPlate("74", R.drawable.ishihara_74)
    )
    private var currentLevel = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityColourblindnessBinding.inflate(layoutInflater)
            setContentView(binding.root)

            tts = TextToSpeech(this, this)
            setupUI()
            startTest()
        } catch (e: Exception) {
            Log.e("ColourBlindness", "Initialization error", e)
            Toast.makeText(this, "Error initializing test", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupUI() {
        try {
            with(binding) {
                progressBar.max = ishiharaPlates.size * 10
                btnSpeak.setOnClickListener { speakCurrentPlate() }
                btnSubmit.setOnClickListener { checkAnswer() }
                btnSkip.setOnClickListener { skipToNext() }
                btnRestart.setOnClickListener { restartTest() }
                btnHome.setOnClickListener { finish() }
            }
        } catch (e: Exception) {
            Log.e("ColourBlindness", "UI setup error", e)
            Toast.makeText(this, "UI setup failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTest() {
        binding.tvInstruction.text = "What number do you see in the circle?"
        showIshiharaPlate(ishiharaPlates[currentLevel])
        binding.etAnswer.requestFocus()
    }

    private fun showIshiharaPlate(plate: IshiharaPlate) {
        binding.ishiharaPlate.setImageResource(plate.imageRes)
        binding.ishiharaPlate.visibility = View.INVISIBLE

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { value ->
            binding.ishiharaPlate.alpha = value.animatedValue as Float
            if (value.animatedFraction > 0.5f && binding.ishiharaPlate.visibility != View.VISIBLE) {
                binding.ishiharaPlate.visibility = View.VISIBLE
            }
        }
        animator.start()
    }

    private fun speakCurrentPlate() {
        tts.speak("What number do you see in this colored circle?",
            TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun checkAnswer() {
        val userAnswer = binding.etAnswer.text.toString().trim()
        if (userAnswer.equals(ishiharaPlates[currentLevel].number, ignoreCase = true)) {
            score += 10
            showFeedback(true)
            nextLevel()
        } else {
            showFeedback(false)
        }
    }

    private fun showFeedback(isCorrect: Boolean) {
        binding.tvFeedback.text = if (isCorrect) {
            "✅ Correct! You can see this color pattern normally."
        } else {
            "❌ Incorrect. You might have difficulty with this color combination."
        }
    }

    private fun nextLevel() {
        if (currentLevel < ishiharaPlates.size - 1) {
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
        val percentage = (score * 100) / (ishiharaPlates.size * 10)
        val message = when {
            percentage >= 80 -> "Excellent color vision! No signs of color blindness detected."
            percentage >= 50 -> "Mild color vision deficiency may be present."
            else -> "Significant color vision deficiency detected. Please consult an optometrist."
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
            Log.e("ColourBlindness", "TTS initialization failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            tts.shutdown()
        } catch (e: Exception) {
            Log.e("ColourBlindness", "Error shutting down TTS", e)
        }
    }

    data class IshiharaPlate(val number: String, val imageRes: Int)
}