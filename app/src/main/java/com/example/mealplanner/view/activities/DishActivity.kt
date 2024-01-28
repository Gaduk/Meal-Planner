package com.example.mealplanner.view.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.mealplanner.model.Dish
import com.example.mealplanner.R
import com.example.mealplanner.dataBase.DataBase
import com.example.mealplanner.databinding.ActivityDishBinding
import com.google.gson.Gson

class DishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDishBinding
    private var clickedDish : Dish = Dish()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //clickedDish = intent.extras?.getSerializable("dish") as Dish

        //ActionBar
        setSupportActionBar(binding.dishToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.title = getString(R.string.action_bar_empty_title)

        DataBase.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        clickedDish = getDish()
        setData(clickedDish)
    }

    private fun getDish() : Dish {
        val sharedPreferences = applicationContext.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        return gson.fromJson(sharedPreferences.getString("dish", ""), Dish::class.java)
    }

    private fun convertUriIntoDrawable(uri: String?): Drawable? {
        return if (uri != null) {
            val uriObject = Uri.parse(uri)
            Drawable.createFromStream(contentResolver.openInputStream(uriObject), uri)
        }
        else {
            getDrawable(R.drawable.empty_plate)
        }
    }
    private fun setData(dish: Dish) {
        binding.apply {
            if(dish.imageURI == null)
                dishImage.setImageDrawable(getDrawable(R.drawable.empty_plate))
            else {
                val uri = Uri.parse(dish.imageURI)
                dishImage.setImageURI(uri)
            }
            dishNameText.text = dish.name
            dishCalorieText.text = dish.calories + " " + getString(R.string.kcal)
            recipeText.text = dish.recipe
            dishTypeText.text = resources.getStringArray(R.array.dish_types)[dish.type]
            breakfastChip.visibility = if(dish.isForBreakfast) View.VISIBLE else View.GONE
            lunchChip.visibility = if(dish.isForLunch) View.VISIBLE else View.GONE
            dinnerChip.visibility = if(dish.isForDinner) View.VISIBLE else View.GONE
        }
    }
    private fun saveDishInfo(dish: Dish) {
        val sharedPreferences = applicationContext.
        getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val dishJson = gson.toJson(dish)
        sharedPreferences.edit().putString("dish", dishJson).apply()
    }
    //ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                //val intent = Intent()
                //intent.putExtra("dish", clickedDish)
                //setResult(Activity.RESULT_OK, intent)

                finish()
            }
            R.id.edit -> {
                //saveDishInfo(clickedDish)
                val intent = Intent(this, EditorActivity::class.java)
                //intent.putExtra("dish", clickedDish)
                startActivity(intent)
            }
        }
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu_dish, menu)
        return true
    }
}