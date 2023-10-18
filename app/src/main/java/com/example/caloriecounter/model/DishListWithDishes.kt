package com.example.caloriecounter.model

import androidx.room.Embedded
import androidx.room.Relation

//не entity
//класс для связи двух entity
data class DishListWithDishes(
    @Embedded
    var dishList: DishList,
    @Relation(parentColumn = "id", entityColumn = "listId")
    var dishes: List<Dish>
)