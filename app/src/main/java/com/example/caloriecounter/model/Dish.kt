package com.example.caloriecounter.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var imageURI: String? = null,
    var name: String = "",
    var calories: String = "",
    var type: Int = -1,
    var recipe: String = "",
    var isForBreakfast: Boolean = false,
    var isForLunch: Boolean = false,
    var isForDinner: Boolean = false,
    var listType : Int? = null,
    var listId : Long? = null
) : Serializable