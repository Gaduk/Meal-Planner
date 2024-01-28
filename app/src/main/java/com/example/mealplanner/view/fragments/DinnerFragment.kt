package com.example.mealplanner.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealplanner.view.adapters.RecyclerAdapter
import com.example.mealplanner.databinding.FragmentDinnerBinding
import com.example.mealplanner.model.Dish
import com.example.mealplanner.model.Settings
import com.example.mealplanner.viewModel.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DinnerFragment : Fragment() {

    lateinit var binding: FragmentDinnerBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    lateinit var navController: NavController
    lateinit var adapter : RecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDinnerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        setRecyclerViewLayoutManager()
        setRecyclerViewAdapter()

        binding.updateButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                mainViewModel.setSettings(getSettings())
                mainViewModel.saveDinner()
            }
        }

        //YandexAds
        if(mainViewModel.getAdIsLoaded()) {
            val marginRightInDp = 24
            val marginBottomInDp = 64
            val density = resources.displayMetrics.density
            val params: ConstraintLayout.LayoutParams = binding.updateButton.layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(0, 0,
                (marginRightInDp * density).toInt(), (marginBottomInDp * density).toInt())
            binding.updateButton.layoutParams = params
        }
    }

    private fun setRecyclerViewLayoutManager() {
        binding.dinnerRecyclerView.layoutManager = LinearLayoutManager(this.context)

    }
    private fun setRecyclerViewAdapter() {
        adapter = RecyclerAdapter(navController)
        binding.dinnerRecyclerView.adapter = adapter
        mainViewModel.getDinnerList().observe(requireActivity(), Observer {
                dinnerList -> adapter.setList(dinnerList as ArrayList<Dish>?)
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

        fun newInstance() = DinnerFragment()
    }
}