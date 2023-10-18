package com.example.caloriecounter.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caloriecounter.dataBase.Repository
import com.example.caloriecounter.enums.ListType
import com.example.caloriecounter.enums.Type
import com.example.caloriecounter.model.Dish
import com.example.caloriecounter.model.Settings
import kotlinx.coroutines.*


open class MainViewModel(private val repository: Repository) : ViewModel() {

    private val title by lazy { MutableLiveData<String>("Список блюд") }

    private var dishList: ArrayList<Dish> = ArrayList()

    private val defaultMinCalorie: Int = 2200
    private val defaultMaxCalorie: Int = 2600
    private val defaultBreakfastStake: Float = 0.2f
    private val defaultLunchStake: Float = 0.4f
    private val defaultDinnerStake: Float = 0.4f

    private var minCalorie: Int = defaultMinCalorie
    private var maxCalorie: Int = defaultMaxCalorie
    private var breakfastStake: Float = defaultBreakfastStake
    private var lunchStake: Float = defaultLunchStake
    private var dinnerStake: Float = defaultDinnerStake

    private val maxNumberOfIterations = 10000

    fun setTitle(title: String) {
        this.title.value = title
    }
    fun getTitle() : LiveData<String> {
        return title
    }

    fun deleteDish(dish: Dish) {
        viewModelScope.launch {
            repository.deleteDish(dish.name, dish.calories, dish.type, dish.recipe,
                dish.isForBreakfast, dish.isForLunch, dish.isForDinner)
        }
    }

    suspend fun saveBreakfast() {
        repository.deleteDishesByListType(ListType.BREAKFAST.ordinal)
        for(dish in generateBreakfast()) {
            dish.id = null
            dish.listType = ListType.BREAKFAST.ordinal
            repository.insertDish(dish)
        }
    }
    suspend fun saveLunch() {
        repository.deleteDishesByListType(ListType.LUNCH.ordinal)
        for(dish in generateLunch()) {
            dish.id = null
            dish.listType = ListType.LUNCH.ordinal
            repository.insertDish(dish)
        }
    }
    suspend fun saveDinner() {
        repository.deleteDishesByListType(ListType.DINNER.ordinal)
        for(dish in generateDinner()) {
            dish.id = null
            dish.listType = ListType.DINNER.ordinal
            repository.insertDish(dish)
        }
    }

    fun getDishList(): LiveData<List<Dish>> {
        return repository.dishList
    }
    fun getBreakfastList(): LiveData<List<Dish>> {
        return repository.breakfastList
    }
    fun getLunchList(): LiveData<List<Dish>> {
        return repository.lunchList
    }
    fun getDinnerList(): LiveData<List<Dish>> {
        return repository.dinnerList
    }
    fun getFilteredDishList(nameFilter: String): LiveData<List<Dish>> {
        return repository.getFilteredDishList(nameFilter)
    }
    fun getFilteredBreakfast(nameFilter: String): LiveData<List<Dish>> {
        return repository.getFilteredBreakfastList(nameFilter)
    }
    fun getFilteredLunch(nameFilter: String): LiveData<List<Dish>> {
        return repository.getFilteredLunchList(nameFilter)
    }
    fun getFilteredDinner(nameFilter: String): LiveData<List<Dish>> {
        return repository.getFilteredDinnerList(nameFilter)
    }
    
    fun setSettings(settings: Settings) {
        minCalorie = settings.minCalorie
        maxCalorie = settings.maxCalorie
        breakfastStake = settings.breakfastStake
        lunchStake = settings.lunchStake
        dinnerStake = settings.dinnerStake
    }
    private suspend fun setDishList() {
        dishList = repository.getDishesFromDishList() as ArrayList<Dish>
    }

    fun updateDishes(oldDish: Dish, newDish: Dish) {
        viewModelScope.launch {
            repository.updateDishes(
                oldDish.name, oldDish.calories, oldDish.type, oldDish.recipe,
                oldDish.isForBreakfast, oldDish.isForLunch, oldDish.isForDinner,
                newDish.imageURI, newDish.name, newDish.calories, newDish.type, newDish.recipe,
                newDish.isForBreakfast, newDish.isForLunch, newDish.isForDinner
            )
        }
    }
    fun setDefaultDishList(uriList : ArrayList<String>) {
        addToDishList(
            Dish(
                null,
                uriList[2],
                "Борщ", "260", Type.SOUP.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[3],
                "Голубцы", "390", Type.MAIN_DISH.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[4],
                "Кофе", "30", Type.DRINK.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[5],
                "Мороженое", "200", Type.DESSERT.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[6],
                "Паста карбонара", "350", Type.MAIN_DISH.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[7],
                "Чай", "30", Type.DRINK.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[8],
                "Уха", "250", Type.SOUP.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[9],
                "Йогурт", "150", Type.SNACK.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[10],
                "Омлет", "200", Type.OTHER.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[11],
                "Овощной салат", "195", Type.OTHER.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[0],
                "Яблоко", "105", Type.OTHER.ordinal,"Текст рецепта",
                true, true, true)
        )
        addToDishList(
            Dish(
                null,
                uriList[1],
                "Банан", "95", Type.OTHER.ordinal,"Текст рецепта",
                true, true, true)
        )
    }
    fun addToDishList(dish: Dish) {
        viewModelScope.launch {
            dish.listType = ListType.DISH_LIST.ordinal
            repository.insertDish(dish)
        }
    }

    private fun doTypeFiltration(dishes : ArrayList<Dish>?, vararg types: Type) : ArrayList<Dish> {
        val array: ArrayList<Dish> = ArrayList()
        if (dishes != null) {
            for(dish in dishes)
                for(type in types)
                    if(dish.type == type.ordinal) array.add(dish)
        }
        return array
    }
    private fun doBreakfastFiltration(dishes : ArrayList<Dish>) : ArrayList<Dish> {
        val array: ArrayList<Dish> = ArrayList()
        for(dish in dishes)
            if(dish.isForBreakfast) array.add(dish)
        return array
    }
    private fun doLunchFiltration(dishes : ArrayList<Dish>) : ArrayList<Dish> {
        val array: ArrayList<Dish> = ArrayList()
        for(dish in dishes)
            if(dish.isForLunch) array.add(dish)
        return array
    }
    private fun doDinnerFiltration(dishes : ArrayList<Dish>) : ArrayList<Dish> {
        val array: ArrayList<Dish> = ArrayList()
        for(dish in dishes)
            if(dish.isForDinner) array.add(dish)
        return array
    }

    private suspend fun generateBreakfast() : ArrayList<Dish> {
        setDishList()
        val list = doBreakfastFiltration(dishList)
        val drinkList = doTypeFiltration(list, Type.DRINK)
        val commonList = doTypeFiltration(list, Type.SNACK, Type.OTHER, Type.MAIN_DISH, Type.DESSERT)

        var restCalories: Int = (maxCalorie * breakfastStake).toInt()
        val breakfast: ArrayList<Dish> = ArrayList()
        var dish: Dish

        if(drinkList.isNotEmpty()) {
            dish = drinkList.random()
            if (dish.calories.toInt() <= restCalories) {
                breakfast.add(dish)
                restCalories -= dish.calories.toInt()
            }
        }
        else return breakfast

        var k = 0
        if(commonList.isNotEmpty()) {
            while (restCalories > 0 && k < maxNumberOfIterations) {
                k++
                dish = commonList.random()
                if (restCalories - dish.calories.toInt() <
                    maxCalorie - minCalorie) {
                    breakfast.add(dish)
                    restCalories -= dish.calories.toInt()
                }
            }
            if(k < maxNumberOfIterations) {
                breakfast.remove(dish)
                restCalories += dish.calories.toInt()
            }
            else {
                breakfast.clear()
                return breakfast
            }
        }
        return breakfast
    }
    private suspend fun generateLunch() : ArrayList<Dish> {
        setDishList()
        val list = doLunchFiltration(dishList)
        val drinkList = doTypeFiltration(list, Type.DRINK)
        val soupList = doTypeFiltration(list, Type.SOUP)
        val mainDishList = doTypeFiltration(list, Type.MAIN_DISH)
        val commonList = doTypeFiltration(list, Type.SNACK, Type.OTHER, Type.DESSERT)

        var restCalories: Int = (maxCalorie * lunchStake).toInt()
        val lunch: ArrayList<Dish> = ArrayList()
        var dish: Dish

        if(drinkList.isNotEmpty()) {
            dish = drinkList.random()
            if (dish.calories.toInt() <= restCalories) {
                lunch.add(dish)
                restCalories -= dish.calories.toInt()
            }
        }
        else return lunch

        if(mainDishList.isNotEmpty()) {
            dish = mainDishList.random()
            if (dish.calories.toInt() <= restCalories) {
                lunch.add(dish)
                restCalories -= dish.calories.toInt()
            }
        }
        else return lunch

        if(soupList.isNotEmpty()) {
            dish = soupList.random()
            if (dish.calories.toInt() <= restCalories) {
                lunch.add(dish)
                restCalories -= dish.calories.toInt()
            }
        }
        else return lunch

        var k = 0
        if(commonList.isNotEmpty()) {
            while (restCalories > 0 && k < maxNumberOfIterations) {
                k++
                dish = commonList.random()
                if (restCalories - dish.calories.toInt() <
                    maxCalorie - minCalorie) {
                    lunch.add(dish)
                    restCalories -= dish.calories.toInt()
                }
            }
            if(k < maxNumberOfIterations) {
                lunch.remove(dish)
                restCalories += dish.calories.toInt()
            }
            else {
                lunch.clear()
                return lunch
            }
        }

        return lunch
    }
    private suspend fun generateDinner() : ArrayList<Dish> {
        setDishList()
        val list = doDinnerFiltration(dishList)
        val drinkList = doTypeFiltration(list, Type.DRINK)
        val mainDishList = doTypeFiltration(list, Type.MAIN_DISH)
        val commonList = doTypeFiltration(list, Type.SNACK, Type.OTHER, Type.DESSERT, Type.SOUP)

        var restCalories: Int = (maxCalorie * dinnerStake).toInt()
        val lunch: ArrayList<Dish> = ArrayList()
        var dish: Dish

        if(drinkList.isNotEmpty()) {
            dish = drinkList.random()
            if (dish.calories.toInt() <= restCalories) {
                lunch.add(dish)
                restCalories -= dish.calories.toInt()
            }
        }
        else return lunch

        if(mainDishList.isNotEmpty()) {
            dish = mainDishList.random()
            if (dish.calories.toInt() <= restCalories) {
                lunch.add(dish)
                restCalories -= dish.calories.toInt()
            }
        }
        else return lunch

        var k = 0
        var soupIsInList = false
        if(commonList.isNotEmpty()) {
            while (restCalories > 0 && k < maxNumberOfIterations) {
                k++
                dish = commonList.random()
                if (restCalories - dish.calories.toInt() <
                    maxCalorie - minCalorie) {
                    if(dish.type == Type.SOUP.ordinal && soupIsInList)
                        continue
                    if(dish.type == Type.SOUP.ordinal && !soupIsInList)
                        soupIsInList = true
                    lunch.add(dish)
                    restCalories -= dish.calories.toInt()
                }
            }
            if(k < maxNumberOfIterations) {
                lunch.remove(dish)
                restCalories += dish.calories.toInt()
            }
            else {
                lunch.clear()
                return lunch
            }
        }

        return lunch
    }
}