package com.example.mealplanner.dataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.example.mealplanner.model.Dish
import com.example.mealplanner.model.DishList

@Dao
interface Dao {
    @Insert
    suspend fun insertDish(dish: Dish)
    @Query("DELETE FROM dishes WHERE " +
            "name = :name AND calories = :calories AND type = :type AND recipe = :recipe AND " +
            "isForBreakfast = :isForBreakfast AND isForLunch = :isForLunch AND " +
            "isForDinner = :isForDinner")
    suspend fun deleteDish(name: String, calories: String, type: Int, recipe: String,
    isForBreakfast: Boolean, isForLunch: Boolean, isForDinner: Boolean)
    @Query("UPDATE dishes SET imageURI = :newImageURI, name = :newName, calories = :newCalories, type = :newType, recipe = :newRecipe, " +
            "isForBreakfast = :newIsForBreakfast, isForLunch = :newIsForLunch, " +
            "isForDinner = :newIsForDinner " +
            "WHERE name = :oldName AND calories = :oldCalories " +
            "AND type = :oldType AND recipe = :oldRecipe AND isForBreakfast = :oldIsForBreakfast " +
            "AND isForLunch = :oldIsForLunch AND isForDinner = :oldIsForDinner")
    suspend fun updateDishes(
        oldName: String, oldCalories: String, oldType: Int, oldRecipe: String,
        oldIsForBreakfast: Boolean, oldIsForLunch: Boolean, oldIsForDinner: Boolean,
        newImageURI: String?, newName: String, newCalories: String, newType: Int, newRecipe: String,
        newIsForBreakfast: Boolean, newIsForLunch: Boolean, newIsForDinner: Boolean)
    @Query("DELETE FROM dishes WHERE listType = :listType")
    suspend fun deleteDishesByListType(listType: Int)
    @Query("SELECT * FROM dishes WHERE listType = 0")
    fun getAllFromDishList(): LiveData<List<Dish>>
    @Query("SELECT * FROM dishes WHERE listType = 1")
    fun getAllFromBreakfastList(): LiveData<List<Dish>>
    @Query("SELECT * FROM dishes WHERE listType = 2")
    fun getAllFromLunchList(): LiveData<List<Dish>>
    @Query("SELECT * FROM dishes WHERE listType = 3")
    fun getAllFromDinnerList(): LiveData<List<Dish>>
    @Query("SELECT * FROM dishes WHERE listType = 0 AND name LIKE :name")
    fun getAllFromFilteredDishList(name: String): LiveData<List<Dish>>
    @Query("SELECT * FROM dishes WHERE listType = 1 AND name LIKE :name")
    fun getAllFromFilteredBreakfastList(name: String): LiveData<List<Dish>>
    @Query("SELECT * FROM dishes WHERE listType = 2 AND name LIKE :name")
    fun getAllFromFilteredLunchList(name: String): LiveData<List<Dish>>
    @Query("SELECT * FROM dishes WHERE listType = 3 AND name LIKE :name")
    fun getAllFromFilteredDinnerList(name: String): LiveData<List<Dish>>
    @Query("SELECT * FROM dishes WHERE listType = 4")
    fun getClickedDish(): LiveData<Dish>
    @Query("SELECT * FROM dishes WHERE listType = 0")
    suspend fun getDishesFromDishList() : List<Dish>


    @Insert
    suspend fun insertDishList(list: DishList) : Long
    @Delete
    suspend fun deleteDishList(list: DishList)
    @Query("DELETE FROM dish_lists")
    suspend fun deleteAllLists()
    @Query("SELECT * FROM dish_lists WHERE name = 'breakfast'")
    fun getAllFromBreakfasts(): LiveData<List<DishList>>
    @Query("SELECT * FROM dish_lists WHERE name = 'lunch'")
    fun getAllFromLunches(): LiveData<List<DishList>>
    @Query("SELECT * FROM dish_lists WHERE name = 'dinner'")
    fun getAllFromDinners(): LiveData<List<DishList>>
    @Query("SELECT * FROM dish_lists WHERE name = :listName ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomList(listName: String): DishList

}