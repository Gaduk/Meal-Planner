package com.example.caloriecounter.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriecounter.model.Dish
import com.example.caloriecounter.R
import com.example.caloriecounter.databinding.TemplateDishBinding
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList


class RecyclerAdapter(private val navController: NavController):
    RecyclerView.Adapter<RecyclerAdapter.VHolder>() {
    private var itemList: ArrayList<Dish> = ArrayList()
    inner class VHolder(item: View): RecyclerView.ViewHolder(item), View.OnCreateContextMenuListener
    {
        private val binding = TemplateDishBinding.bind(item)
        fun bind(dish: Dish) = with(binding){
            if(dish.imageURI == null)
                templateImage.setImageDrawable(itemView.context.getDrawable(R.drawable.empty_plate))
            else {
                val uri = Uri.parse(dish.imageURI)
                templateImage.setImageURI(uri)
            }
            templateName.text = dish.name;
            templateCalorie.text = dish.calories + " " + itemView.context.getString(R.string.kcal)
            template.setOnCreateContextMenuListener(this@VHolder)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.add(adapterPosition, 101, 1, v?.context?.getString(R.string.delete))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.template_dish, parent,
            false)
        return VHolder(view)
    }
    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.bind(itemList[position])
        holder.itemView.setOnClickListener {
            val sharedPreferences = navController.context.applicationContext.
            getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val gson = Gson()

            val dishJson = gson.toJson(itemList[holder.adapterPosition])
            sharedPreferences.edit().putString("dish", dishJson).apply()

            val previousDishJson = gson.toJson(itemList[holder.adapterPosition])
            sharedPreferences.edit().putString("previous_dish", previousDishJson).apply()

            navController.navigate(R.id.dish_activity)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    fun setList(list: ArrayList<Dish>?) {
        if (list != null) {
            itemList = list
        }
        notifyDataSetChanged()
    }
    fun getItemByPosition(position: Int): Dish
    {
        return itemList[position]
    }
}