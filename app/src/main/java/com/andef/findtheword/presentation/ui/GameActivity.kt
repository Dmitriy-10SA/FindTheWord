package com.andef.findtheword.presentation.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.andef.findtheword.R
import com.andef.findtheword.databinding.ActivityGameBinding
import com.andef.findtheword.presentation.adapter.AnagramAdapter
import com.andef.findtheword.presentation.app.FindTheWordApplication
import com.andef.findtheword.presentation.factory.ViewModelFactory
import javax.inject.Inject


class GameActivity : AppCompatActivity() {
    private val component by lazy {
        (application as FindTheWordApplication).component
    }

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    @Inject
    lateinit var anagramAdapter: AnagramAdapter

    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settings =
            application.getSharedPreferences(PREFS_FILE_WORD_AND_ANAGRAMS, Context.MODE_PRIVATE)

        initViews()
        initViewModel()
    }

    private fun initViews() {
        binding.textViewCurrentWord.apply {
            text = intent.getStringExtra(EXTRA_WORD)
        }

        binding.recyclerViewWords.apply {
            adapter = anagramAdapter
        }

        binding.buttonCheckAndAddWord.setOnClickListener {
            val word = binding.editTextInputWord.text.toString().trim().lowercase()
            if (word.isNotEmpty() && !word.contains(" ")) {
                viewModel.checkWord(word)
            } else if (word.contains(" ")) {
                getToast(R.string.space_in_word)
            } else {
                getToast(R.string.input_word_exc)
            }
        }
        binding.buttonHome.setOnClickListener { showClearDialog() }
    }

    private fun initViewModel() {
        viewModel.getAnagrams().observe(this) {
            anagramAdapter.anagrams = it.toList()
            anagramAdapter.submitList(it.toList())
        }
        viewModel.isWord.observe(this) { isWord ->
            if (isWord) {
                val anagram = binding.editTextInputWord.text.toString().trim().lowercase()
                val word = binding.textViewCurrentWord.text.toString().trim().lowercase()
                if (viewModel.checkAnagramForWord(word, anagram)) {
                    viewModel.addAnagram(anagram)
                    binding.editTextInputWord.text.clear()
                } else {
                    getToast(R.string.is_not_anagram)
                }
            } else {
                getToast(R.string.word_not_found)
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = VISIBLE
            } else {
                binding.progressBar.visibility = GONE
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
        binding.textViewCurrentWord.text = word
        val anagrams = settings.getString(PREF_ANAGRAMS, null)?.split("/")
        if (anagrams.isNullOrEmpty() || (anagrams.isNotEmpty() && anagrams[0] == "")) {
            viewModel.clearAnagrams()
            return
        }
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
        backgroundView.findViewById<Button>(R.id.buttonRandomWord).apply {
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
        prefEditor.putString(PREF_WORD, binding.textViewCurrentWord.text.toString().trim().lowercase())
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