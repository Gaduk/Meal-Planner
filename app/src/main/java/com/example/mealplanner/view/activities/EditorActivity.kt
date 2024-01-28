package com.example.mealplanner.view.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.example.mealplanner.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.example.mealplanner.Constants
import com.example.mealplanner.Constants.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
import com.example.mealplanner.dataBase.DataBase
import com.example.mealplanner.dataBase.Repository
import com.example.mealplanner.databinding.ActivityEditorBinding
import com.example.mealplanner.model.Dish
import com.example.mealplanner.model.Settings
import com.example.mealplanner.viewModel.MainViewModel
import com.example.mealplanner.viewModel.MainViewModelFactory
import com.google.gson.Gson

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson
    private var dish = Dish()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewModel, dao, repository
        val dao = DataBase.getInstance(this).getDao()
        val repository: Repository = Repository(dao)
        val viewModelFactory = MainViewModelFactory(repository)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        sharedPreferences = applicationContext.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        gson = Gson()

        dish = gson.fromJson(sharedPreferences.getString("dish", ""), Dish::class.java)
        setData(dish)

        //Spinner
        val items = resources.getStringArray(R.array.dish_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        val spinner = binding.spinner
        spinner.adapter = adapter
        spinner.setSelection(dish.type)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = items[position]
                dish.type = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val breakfastChip = binding.breakfastChip
        val lunchChip = binding.lunchChip
        val dinnerChip = binding.dinnerChip

        breakfastChip.setOnCheckedChangeListener { chip, isChecked ->
            dish.isForBreakfast = isChecked
        }
        lunchChip.setOnCheckedChangeListener{ chip, isChecked ->
            dish.isForLunch = isChecked
        }
        dinnerChip.setOnCheckedChangeListener{ chip, isChecked ->
            dish.isForDinner = isChecked
        }

        //Gallery
        binding.dishImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                //Разрешение не предоставлено, необходимо запросить его у пользователя
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
                )
            } else {
                //Разрешение предоставлено
                //Можно выполнять операции, требующие разрешения здесь
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST)
            }
        }

        //ActionBar
        setSupportActionBar(binding.editorToolbar)
        enableBackButton()
        setActionBarTitle(R.string.action_bar_empty_title)


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // код, который выполняется при нажатии на кнопку "Назад" в нижней панели навигации
                sharedPreferences.edit().putString("is_saved", "false").apply()
                finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun getSettings() : Settings {
        return gson.fromJson(sharedPreferences.getString("settings", ""), Settings::class.java)
    }

    //Gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            binding.dishImage.setImageURI(imageUri)
            dish.imageURI = imageUri.toString()
        }
    }

    private fun convertUriIntoDrawable(uri: String?): Drawable? {
        return if(uri != null)
            Drawable.createFromStream(
                uri.let { contentResolver.openInputStream(it.toUri()) },
                uri.toString())
        else getDrawable(R.drawable.empty_plate)
    }
    private fun setData(dish: Dish) {
        binding.apply {
            val uri = convertUriIntoDrawable(dish.imageURI)
            dishImage.setImageDrawable(uri)
            dishNameText.setText(dish.name)
            dishCalorieText.setText(dish.calories)
            recipeText.setText(dish.recipe)
            breakfastChip.isChecked = dish.isForBreakfast
            lunchChip.isChecked = dish.isForLunch
            dinnerChip.isChecked = dish.isForDinner
        }
    }
    private fun saveDishInfo(dish: Dish) {
        dish.apply {
            name = binding.dishNameText.text.toString()
            calories = binding.dishCalorieText.text.toString()
            recipe = binding.recipeText.text.toString()
            isForBreakfast = binding.breakfastChip.isChecked
            isForLunch = binding.lunchChip.isChecked
            isForDinner = binding.dinnerChip.isChecked
        }

        val dishJson = gson.toJson(dish)
        sharedPreferences.edit().putString("dish", dishJson).apply()

        sharedPreferences.edit().putString("is_saved", "true").apply()
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
            android.R.id.home -> {
                sharedPreferences.edit().putString("is_saved", "false").apply()
                finish()
            }
            R.id.save -> {
                binding.apply {
                    if(dishNameText.text.isEmpty() ||
                            dishCalorieText.text.isEmpty() ||
                            recipeText.text.isEmpty())
                        Toast.makeText(this@EditorActivity,
                            getString(R.string.editor_error_empty_field),
                            Toast.LENGTH_LONG).show()
                    else if(!breakfastChip.isChecked &&
                        !lunchChip.isChecked &&
                        !dinnerChip.isChecked)
                        Toast.makeText(this@EditorActivity,
                            getString(R.string.editor_error_meal),
                            Toast.LENGTH_LONG).show()
                    else {
                        saveDishInfo(dish)

                        val previousDish: Dish? = getPreviousDish()
                        saveChangedDish(dish, previousDish)

                        finish()
                    }
                }
            }
        }
        return true
    }
    private fun saveChangedDish(dish: Dish?, previousDish: Dish?) {
        previousDish?.apply {
            if (dish != null) {
                val emptyDish = Dish(null, null,
                    "","",0,"",true,true,true)
                if(previousDish == emptyDish && newDishIsSaved()) {
                    mainViewModel.addToDishList(dish)
                    val isSavedJson = gson.toJson(false)
                    sharedPreferences.edit().putString("is_saved", isSavedJson).apply()
                }
                else {
                    mainViewModel.updateDishes(previousDish, dish)
                }
            }
        }
    }
    private fun newDishIsSaved() : Boolean {
        val isSavedString : String? = sharedPreferences.getString("is_saved", "false")
        return isSavedString != "false"
    }
    private fun getPreviousDish() : Dish? {
        val previousDishString : String? = sharedPreferences.getString("previous_dish", null)
        var previousDish: Dish? = null
        previousDishString?.let {
            previousDish = gson.fromJson(previousDishString, Dish::class.java)
        }
        return previousDish
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu_editor, menu)
        return true
    }

}