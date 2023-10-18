package com.example.caloriecounter.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caloriecounter.model.Dish
import com.example.caloriecounter.R
import com.example.caloriecounter.view.adapters.RecyclerAdapter
import com.example.caloriecounter.viewModel.MainViewModel
import com.example.caloriecounter.databinding.FragmentDishListBinding
import com.example.caloriecounter.model.Settings
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DishListFragment : Fragment() {

    private lateinit var binding: FragmentDishListBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter : RecyclerAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDishListBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().applicationContext.getSharedPreferences("my_prefs",
            Context.MODE_PRIVATE)
        gson = Gson()

        //NavController
        val navController = findNavController()

        val addButton = binding.addButton
        addButton.setOnClickListener {
            val dishListJson = Gson().toJson(Dish(null, null,
                "","",0,"",true,true,true))
            val sharedPreferences =
                requireActivity().applicationContext.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.putString("previous_dish", dishListJson)?.apply()
            sharedPreferences?.edit()?.putString("dish", dishListJson)?.apply()
            navController.navigate(R.id.editor_activity)
        }

        //RecyclerAdapter
        binding.dishListRecyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = RecyclerAdapter(navController)
        binding.dishListRecyclerView.adapter = adapter
        mainViewModel.getDishList().observe(requireActivity(), Observer {
                dishList ->
            adapter.setList(dishList as ArrayList<Dish>?)
        })

    }

    fun getRecyclerAdapter() : RecyclerAdapter {
        return adapter
    }

    //Context menu
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            101 ->
            {
                val dish = adapter.getItemByPosition(item.groupId)
                mainViewModel.deleteDish(dish)
            }
        }
        return super.onContextItemSelected(item)
    }
    private fun getSettings() : Settings {
        val sharedPreferences = requireActivity().applicationContext
            .getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return Gson().fromJson(sharedPreferences.getString("settings", ""), Settings::class.java)
    }

    companion object {
        fun newInstance() = DishListFragment()
    }
}
