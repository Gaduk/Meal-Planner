package com.example.mealplanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dish_lists")
data class DishList(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String = "" //breakfast, lunch, dinner
)

