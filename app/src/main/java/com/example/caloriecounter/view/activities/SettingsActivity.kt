package com.example.caloriecounter.view.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.caloriecounter.R
import com.example.caloriecounter.dataBase.DataBase
import com.example.caloriecounter.dataBase.Repository
import com.example.caloriecounter.databinding.ActivitySettingsBinding
import com.example.caloriecounter.model.Settings
import com.example.caloriecounter.viewModel.MainViewModel
import com.example.caloriecounter.viewModel.MainViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewModel, dao, repository
        val dao = DataBase.getInstance(this).getDao()
        val repository: Repository = Repository(dao)
        val viewModelFactory = MainViewModelFactory(repository)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        sharedPreferences = applicationContext.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        gson = Gson()

        setData()

        binding.defaultButton.setOnClickListener {
            setDefaultSettings()
        }

        //ActionBar
        setSupportActionBar(binding.settingsToolbar)
        enableBackButton()
        setActionBarTitle(R.string.action_bar_empty_title)

    }
    private fun getSettings() : Settings {
        return gson.fromJson(sharedPreferences.getString("settings", ""), Settings::class.java)
    }

    private fun setDefaultSettings() {
        val defaultMinCalorie : Int = 2200
        val defaultMaxCalorie : Int = 2600
        val defaultBreakfastStake : Float = 0.2f
        val defaultLunchStake : Float = 0.4f
        val defaultDinnerStake: Float = 0.4f
        binding.apply {
            minCaloriesEditText.setText(defaultMinCalorie.toString())
            maxCaloriesEditText.setText(defaultMaxCalorie.toString())
            breakfastEditText.setText((defaultBreakfastStake*100).toInt().toString())
            lunchEditText.setText((defaultLunchStake*100).toInt().toString())
            dinnerEditText.setText((defaultDinnerStake*100).toInt().toString())
        }
    }
    private fun setData() {
        val settings = getSettings()
        binding.apply {
            minCaloriesEditText.setText(settings.minCalorie.toString())
            maxCaloriesEditText.setText(settings.maxCalorie.toString())
            breakfastEditText.setText((settings.breakfastStake * 100).toInt().toString())
            lunchEditText.setText((settings.lunchStake * 100).toInt().toString())
            dinnerEditText.setText((settings.dinnerStake * 100).toInt().toString())
        }
    }
    private fun saveData() {
        val settings = Settings()
        binding.apply {
            settings.minCalorie = minCaloriesEditText.text.toString().toInt()
            settings.maxCalorie = maxCaloriesEditText.text.toString().toInt()
            settings.breakfastStake = breakfastEditText.text.toString().toFloat() / 100
            settings.lunchStake = lunchEditText.text.toString().toFloat() / 100
            settings.dinnerStake = dinnerEditText.text.toString().toFloat() / 100
        }
        val settingsJson = gson.toJson(settings)
        sharedPreferences.edit().putString("settings", settingsJson).apply()
    }



    //ActionBar
    private fun enableBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }
    private fun setActionBarTitle(titleId: Int) {
        supportActionBar?.title = getString(titleId)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.save -> {
                binding.apply {
                    if (minCaloriesEditText.text.isEmpty() ||
                        maxCaloriesEditText.text.isEmpty() ||
                        breakfastEditText.text.isEmpty() ||
                        lunchEditText.text.isEmpty() ||
                        dinnerEditText.text.isEmpty())
                        Toast.makeText(this@SettingsActivity,
                            getString(R.string.settings_error_empty_field),
                            Toast.LENGTH_LONG).show()
                    else if(minCaloriesEditText.text.toString().toInt() >=
                        maxCaloriesEditText.text.toString().toInt()) {
                        Toast.makeText(
                            this@SettingsActivity,
                            getString(R.string.settings_error_invalid_calories),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else if(breakfastEditText.text.toString().toInt() +
                        lunchEditText.text.toString().toInt() +
                        dinnerEditText.text.toString().toInt() != 100)
                        Toast.makeText(this@SettingsActivity,
                            getString(R.string.settings_error_invalid_percents),
                            Toast.LENGTH_LONG).show()
                    else {
                        saveData()
                        finish()
                    }
                }
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu_settings, menu)
        return true
    }
}