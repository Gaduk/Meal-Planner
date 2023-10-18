package com.example.caloriecounter.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caloriecounter.view.adapters.RecyclerAdapter
import com.example.caloriecounter.databinding.FragmentLunchBinding
import com.example.caloriecounter.model.Dish
import com.example.caloriecounter.model.Settings
import com.example.caloriecounter.viewModel.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LunchFragment : Fragment() {

    lateinit var binding: FragmentLunchBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    lateinit var navController: NavController
    lateinit var adapter : RecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLunchBinding.inflate(inflater)
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
                mainViewModel.saveLunch()
            }
        }
    }
    private fun setRecyclerViewLayoutManager() {
        binding.lunchRecyclerView.layoutManager = LinearLayoutManager(this.context)

    }
    private fun setRecyclerViewAdapter() {
        adapter = RecyclerAdapter(navController)
        binding.lunchRecyclerView.adapter = adapter
        mainViewModel.getLunchList().observe(requireActivity(), Observer {
                lunchList -> adapter.setList(lunchList as ArrayList<Dish>?)
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
        fun newInstance() = LunchFragment()
    }
}