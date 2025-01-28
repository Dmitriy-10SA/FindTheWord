package com.andef.findtheword.presentation.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import com.andef.findtheword.R
import com.andef.findtheword.presentation.adapter.AnagramAdapter


class GameActivity : AppCompatActivity() {
    private lateinit var textViewCurrentWord: TextView

    private lateinit var anagramAdapter: AnagramAdapter
    private lateinit var recyclerViewWords: RecyclerView

    private lateinit var editTextInputWord: EditText

    private lateinit var buttonCheckAndAddWord: Button
    private lateinit var buttonHome: Button

    private lateinit var progressBar: ProgressBar

    private lateinit var viewModel: GameViewModel

    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settings = application.getSharedPreferences(PREFS_FILE_WORD_AND_ANAGRAMS, Context.MODE_PRIVATE)

        initViews()
        initViewModel()
    }

    private fun initViews() {
        textViewCurrentWord = findViewById<TextView?>(R.id.textViewCurrentWord).apply {
            text = intent.getStringExtra(EXTRA_WORD)
        }

        recyclerViewWords = findViewById<RecyclerView?>(R.id.recyclerViewWords).apply {
            anagramAdapter = AnagramAdapter()
            adapter = anagramAdapter
        }

        editTextInputWord = findViewById(R.id.editTextInputWord)

        progressBar = findViewById(R.id.progressBar)

        buttonCheckAndAddWord = findViewById<Button?>(R.id.buttonCheckAndAddWord).apply {
            setOnClickListener {
                val word = editTextInputWord.text.toString().trim().lowercase()
                if (word.isNotEmpty() && !word.contains(" ")) {
                    viewModel.checkWord(word)
                }
                else if (word.contains(" ")) {
                    getToast(R.string.space_in_word)
                }
                else {
                    getToast(R.string.input_word_exc)
                }
            }
        }
        buttonHome = findViewById<Button?>(R.id.buttonHome).apply {
            setOnClickListener {
                showClearDialog()
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        viewModel.getAnagrams().observe(this) {
            anagramAdapter.anagrams = it.toList()
        }
        viewModel.isWord.observe(this) { isWord ->
            if (isWord) {
                val anagram = editTextInputWord.text.toString().trim().lowercase()
                val word = textViewCurrentWord.text.toString().trim().lowercase()
                if (viewModel.checkAnagramForWord(word, anagram)) {
                    viewModel.addAnagram(anagram)
                    editTextInputWord.text.clear()
                } else {
                    getToast(R.string.is_not_anagram)
                }
            }
            else {
                getToast(R.string.word_not_found)
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = VISIBLE
            }
            else {
                progressBar.visibility = GONE
            }
        }
        viewModel.isItWas.observe(this) {
            Toast.makeText(this, R.string.word_equals, Toast.LENGTH_SHORT).show()
        }
        viewModel.checkInternet.observe(this) {
            getToast(R.string.check_internet)
        }
        if (!intent.getBooleanExtra(EXTRA_CONTINUE_GAME, false)) {
            viewModel.clearAnagrams()
        } else {
            lastGame()
        }
    }

    private fun lastGame() {
        val word = settings.getString(PREF_WORD, "")
        if (word == "") {
            getToast(R.string.is_not_last_game)
            finish()
        }
        textViewCurrentWord.text = word
        val anagrams =  settings.getString(PREF_ANAGRAMS, "")?.split("/") ?: listOf()
        anagramAdapter.anagrams = anagrams
        viewModel.resumeAnagrams(anagrams.toHashSet())
    }

    private fun showClearDialog() {
        val backgroundView = layoutInflater.inflate(R.layout.alert_dialog_custom, null)
        backgroundView.findViewById<TextView>(R.id.textViewTitle).apply {
            text = getString(R.string.you_sure)
        }
        backgroundView.findViewById<EditText>(R.id.editTextDialog).apply {
            visibility = GONE
        }
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
            finish()
         }
    }

    override fun onPause() {
        savePref()
        super.onPause()
    }

    override fun onDestroy() {
        savePref()
        super.onDestroy()
    }

    private fun savePref() {
        val settings = application
            .getSharedPreferences(PREFS_FILE_WORD_AND_ANAGRAMS, Context.MODE_PRIVATE)
        val prefEditor = settings.edit()
        prefEditor.putString(PREF_WORD, textViewCurrentWord.text.toString().trim().lowercase())
        val stringBuilder = StringBuilder()
        for (word in anagramAdapter.anagrams.withIndex()) {
            if (word.index != anagramAdapter.anagrams.size - 1) {
                stringBuilder.append("${word.value}/")
            } else {
                stringBuilder.append(word.value)
            }
        }
        prefEditor.putString(PREF_ANAGRAMS, stringBuilder.toString())
        prefEditor.apply()
    }

    private fun getToast(stringRes: Int) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_WORD = "word"
        private const val EXTRA_CONTINUE_GAME = "continue"

        private const val PREFS_FILE_WORD_AND_ANAGRAMS = "word and anagrams"
        private const val PREF_WORD = "word"
        private const val PREF_ANAGRAMS = "anagrams"

        fun newIntent(context: Context, word: String, continueGame: Boolean): Intent {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(EXTRA_WORD, word)
            intent.putExtra(EXTRA_CONTINUE_GAME, continueGame)
            return intent
        }
    }
}