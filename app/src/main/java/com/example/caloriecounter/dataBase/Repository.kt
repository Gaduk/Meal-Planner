package com.example.caloriecounter.dataBase

import androidx.lifecycle.LiveData
import com.example.caloriecounter.model.Dish
import com.example.caloriecounter.model.DishList

class Repository(private val dao: Dao) {
    val dishList = dao.getAllFromDishList()
    val breakfastList = dao.getAllFromBreakfastList()
    val lunchList = dao.getAllFromLunchList()
    val dinnerList = dao.getAllFromDinnerList()

    fun getFilteredDishList(name: String): LiveData<List<Dish>> {
        return dao.getAllFromFilteredDishList(name)
    }
    fun getFilteredBreakfastList(name: String): LiveData<List<Dish>> {
        return dao.getAllFromFilteredBreakfastList(name)
    }
    fun getFilteredLunchList(name: String): LiveData<List<Dish>> {
        return dao.getAllFromFilteredLunchList(name)
    }
    fun getFilteredDinnerList(name: String): LiveData<List<Dish>> {
        return  dao.getAllFromFilteredDinnerList(name)
    }

    suspend fun insertDish(dish: Dish) {
        dao.insertDish(dish)
    }
    suspend fun deleteDish(name: String, calories: String, type: Int, recipe: String,
                           isForBreakfast: Boolean, isForLunch: Boolean, isForDinner: Boolean) {
        dao.deleteDish(name, calories, type, recipe, isForBreakfast, isForLunch, isForDinner)
    }
    suspend fun deleteDishesByListType(listType: Int) {
        dao.deleteDishesByListType(listType)
    }
    suspend fun updateDishes(
       oldName: String, oldCalories: String, oldType: Int, oldRecipe: String,
        oldIsForBreakfast: Boolean, oldIsForLunch: Boolean, oldIsForDinner: Boolean,
        newImageURI: String?, newName: String, newCalories: String, newType: Int, newRecipe: String,
        newIsForBreakfast: Boolean, newIsForLunch: Boolean, newIsForDinner: Boolean
    ) {
        dao.updateDishes(
            oldName, oldCalories, oldType, oldRecipe,
            oldIsForBreakfast, oldIsForLunch, oldIsForDinner,
            newImageURI, newName, newCalories, newType, newRecipe,
            newIsForBreakfast, newIsForLunch, newIsForDinner
        )
    }
    suspend fun getDishesFromDishList() : List<Dish> {
        return dao.getDishesFromDishList()
    }


    suspend fun insertDishList(list: DishList) : Long {
        return dao.insertDishList(list)
    }
    suspend fun deleteDishList(list: DishList) {
        dao.deleteDishList(list)
    }
    suspend fun deleteAllLists() {
        dao.deleteAllLists()
    }
    suspend fun getRandomList(listName: String): DishList {
        return dao.getRandomList(listName)
    }

}