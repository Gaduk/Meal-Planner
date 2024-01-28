package com.example.mealplanner.dataBase


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mealplanner.model.Dish
import com.example.mealplanner.model.DishList

@Database(entities = [Dish::class, DishList::class], version = 1)
abstract class DataBase : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var dataBase: DataBase? = null

        fun getInstance(context: Context): DataBase {
            if (dataBase == null) {
                dataBase = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "database"
                ).build()
            }
            return dataBase as DataBase
        }
    }
}
