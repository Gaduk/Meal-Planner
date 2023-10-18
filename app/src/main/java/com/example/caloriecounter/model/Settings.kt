package com.example.caloriecounter.model


data class Settings(
    var minCalorie: Int = 2200,
    var maxCalorie: Int = 2600,
    var breakfastStake: Float = 0.2f,
    var lunchStake: Float = 0.4f,
    var dinnerStake: Float = 0.4f
)