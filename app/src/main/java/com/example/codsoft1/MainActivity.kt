package com.example.codsoft1

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.codsoft1.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var countDownTimer: CountDownTimer? = null
    private val tipsPool: List<String> = listOf(
        "Tip: Limit Yourself to 3–5 Tasks per Day.",
        "Tip: Manage your time wisely.",
        "Tip: Take short breaks to recharge.",
        "Tip: Focus on one task at a time.",
        "Tip: Stay hydrated for better concentration.",
        "Tip: Avoid multitasking to increase efficiency.",
        "Tip: Start your day with the hardest task.",
        "Tip: Set realistic deadlines for your tasks.",
        "Tip: Reflect on your achievements daily."
    )
    private lateinit var tips: List<String>
    private var currentTipIndex = 0
    private val interval = 1500L
    private val totalDuration = 4000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tips = tipsPool.shuffled().take(3)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle button click to navigate and cancel the timer
        binding.button2.setOnClickListener {
            navigateToMainActivity2()
        }

        // Start the countdown with tips and animations
        startTipsCountdown()
    }

    private fun startTipsCountdown() {
        countDownTimer = object : CountDownTimer(totalDuration, interval) {
            override fun onTick(millisUntilFinished: Long) {
                if (currentTipIndex < tips.size) {
                    binding.textView4.text = tips[currentTipIndex] // Show current tip
                    currentTipIndex++ // Move to the next tip
                    animateCountdownText()
                }
            }

            override fun onFinish() {
                // Transition to the next activity
                animateTransitionToNextActivity()
            }
        }
        countDownTimer?.start()
    }

    private fun animateCountdownText() {
        val scaleAnimation = ScaleAnimation(
            1.0f, 1.5f, // Start and end X scale
            1.0f, 1.5f, // Start and end Y scale
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X
            Animation.RELATIVE_TO_SELF, 0.5f // Pivot Y
        ).apply {
            duration = 500 // Animation duration
            repeatMode = Animation.REVERSE // Reverse the animation
            repeatCount = 1 // Play the animation twice
        }

        binding.textView4.startAnimation(scaleAnimation)
    }

    private fun animateTransitionToNextActivity() {
        val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f).apply {
            duration = 500 // Animation duration
            fillAfter = true // Keep the state after animation
        }

        binding.root.startAnimation(fadeOutAnimation)

        // Start the next activity after animation ends
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                navigateToMainActivity2()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun navigateToMainActivity2() {
        countDownTimer?.cancel() // Cancel the timer to avoid memory leaks
        countDownTimer = null
        val intent = Intent(this@MainActivity, MainActivity2::class.java)
        startActivity(intent)
        finish() // Close current activity to prevent returning back
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel() // Ensure timer is canceled when activity is destroyed
        countDownTimer = null
    }
}
