package com.andef.findtheword.presentation.ui

import android.content.Context
import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                viewModel.addAnagram(editTextInputWord.text.toString().trim().lowercase())
                editTextInputWord.text.clear()
            }
            else {
                Toast.makeText(this, R.string.word_not_found, Toast.LENGTH_SHORT).show()
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
        }
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

    private fun getToast(stringRes: Int) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_WORD = "word"
        private const val EXTRA_CONTINUE_GAME = "continue"

        fun newIntent(context: Context, word: String, continueGame: Boolean): Intent {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(EXTRA_WORD, word)
            intent.putExtra(EXTRA_CONTINUE_GAME, continueGame)
            return intent
        }
    }
}