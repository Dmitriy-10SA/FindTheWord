package com.andef.findtheword.presentation.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.andef.findtheword.R


class MainActivity : AppCompatActivity() {
    private lateinit var buttonNewGame: Button
    private lateinit var buttonContinueGame: Button
    private lateinit var buttonFinish: Button

    private lateinit var progressBarMain: ProgressBar

    private lateinit var viewModel: MainViewModel

    private var word = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initViewModel()
    }

    private fun initViews() {
        buttonNewGame = findViewById<Button?>(R.id.buttonNewGame).apply {
            setOnClickListener { showInputDialog() }
        }
        buttonContinueGame = findViewById<Button?>(R.id.buttonContinueGame).apply {
            setOnClickListener { lastGameScreen() }
        }
        buttonFinish = findViewById<Button?>(R.id.buttonFinish).apply {
            setOnClickListener { finish() }
        }

        progressBarMain = findViewById(R.id.progressBarMain)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.isWord.observe(this) { isWord ->
            if (isWord) {
                newGameScreen(word)
            }
            else {
                showUserWordDialog()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) progressBarMain.visibility = VISIBLE
            else progressBarMain.visibility = GONE
        }
        viewModel.checkInternet.observe(this) {
            getToast(R.string.check_internet)
        }
    }

    private fun showUserWordDialog() {
        val backgroundView = layoutInflater.inflate(R.layout.alert_dialog_custom, null)
        backgroundView.findViewById<TextView>(R.id.textViewTitle).apply {
            text = getString(R.string.word_not_found_continue)
            gravity = Gravity.CENTER
        }
        backgroundView.findViewById<Button>(R.id.buttonRandomWord).apply { visibility = GONE }
        backgroundView.findViewById<EditText>(R.id.editTextDialog).apply { visibility = GONE }
        val buttonCancel = backgroundView.findViewById<Button>(R.id.buttonCancel)
        val buttonOk = backgroundView.findViewById<Button>(R.id.buttonOk).apply {
            text = getString(R.string.yes)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(backgroundView)
            .setCancelable(false)
            .create()

        dialog.show()

        buttonCancel.setOnClickListener { dialog.dismiss() }
        buttonOk.setOnClickListener {
            dialog.dismiss()
            newGameScreen(this.word)
        }
    }

    private fun showInputDialog() {
        val backgroundView = layoutInflater.inflate(R.layout.alert_dialog_custom, null)
        val editTextDialog = backgroundView.findViewById<EditText>(R.id.editTextDialog)
        val buttonCancel = backgroundView.findViewById<Button>(R.id.buttonCancel)
        val buttonOk = backgroundView.findViewById<Button>(R.id.buttonOk)
        val buttonRandomWord = backgroundView.findViewById<Button>(R.id.buttonRandomWord)

        val dialog = AlertDialog.Builder(this)
            .setView(backgroundView)
            .setCancelable(false)
            .create()

        dialog.show()

        buttonCancel.setOnClickListener { dialog.dismiss() }
        buttonOk.setOnClickListener {
            val word = editTextDialog.text.toString().trim().lowercase()
            if (word.isNotEmpty() && !word.contains(" ")) {
                dialog.dismiss()
                this.word = word
                viewModel.checkWord(word)
            } else if (word.contains(" ")) {
                 getToast(R.string.space_in_word)
            } else {
                getToast(R.string.input_word_exc)
            }
        }
        buttonRandomWord.setOnClickListener {
            val randomWord = viewModel.getRandomWord()
            editTextDialog.setText(randomWord.trim().lowercase())
        }
    }

    private fun getToast(stringRes: Int) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()
    }

    private fun lastGameScreen() {
        val intent = GameActivity.newIntent(this, "", true)
        startActivity(intent)
    }

    private fun newGameScreen(word: String) {
        val intent = GameActivity.newIntent(this, word, false)
        startActivity(intent)
    }
}