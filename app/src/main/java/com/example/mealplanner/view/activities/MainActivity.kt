package com.example.mealplanner.view.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mealplanner.R
import com.example.mealplanner.dataBase.DataBase
import com.example.mealplanner.dataBase.Repository
import com.example.mealplanner.databinding.ActivityMainBinding
import com.example.mealplanner.model.Dish
import com.example.mealplanner.model.Settings
import com.example.mealplanner.view.fragments.BreakfastFragment
import com.example.mealplanner.view.fragments.DinnerFragment
import com.example.mealplanner.view.fragments.DishListFragment
import com.example.mealplanner.view.fragments.LunchFragment
import com.example.mealplanner.viewModel.MainViewModel
import com.example.mealplanner.viewModel.MainViewModelFactory
import com.google.gson.Gson


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson
    private var currentQuery: String = ""
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewModel, dao, repository
        val dao = DataBase.getInstance(this).getDao()
        val repository: Repository = Repository(dao)
        val viewModelFactory = MainViewModelFactory(repository)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        sharedPreferences = applicationContext.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        gson = Gson()

        if (isFirstRun(this)) {
            val dishNames = ArrayList(resources.getStringArray(R.array.dish_names).asList())
            mainViewModel.setDefaultDishList(getDefaultUriList(this), dishNames)
            saveSettings(Settings())
            mainViewModel.setSettings(getSettings())
        }

        mainViewModel.setTitle(getString(R.string.main_header))

        //NavController
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        //BottomNavigationView
        val bottomNav = binding.bottomNavigation
        bottomNav.setupWithNavController(navController)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.dishes_list -> {
                    navController.navigate(R.id.dish_list_fragment)
                    mainViewModel.setTitle(getString(R.string.main_header))
                }
                R.id.breakfast -> {
                    navController.navigate(R.id.breakfast_fragment)
                    mainViewModel.setTitle(getString(R.string.breakfast_header))
                }
                R.id.lunch -> {
                    navController.navigate(R.id.lunch_fragment)
                    mainViewModel.setTitle(getString(R.string.lunch_header))
                }
                R.id.dinner -> {
                    navController.navigate(R.id.dinner_fragment)
                    mainViewModel.setTitle(getString(R.string.dinner_header))
                }
            }
            true
        }

        //ActionBar
        setSupportActionBar(binding.toolbar)
        setActionBarTitle()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // код, который выполняется при нажатии на кнопку "Назад" в нижней панели навигации
                finishAffinity()
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)



        //YandexAdsBanner
        /*
        binding.banner.setAdUnitId("R-M-3478681-1")
        binding.banner.setAdSize(stickySize(this, 385))
        val adRequest = AdRequest.Builder().build()
        binding.banner.loadAd(adRequest)
        binding.banner.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                Log.d("Yandex", "ad is loaded")
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                Log.d("Yandex", "ad is failed to load")
            }

            override fun onAdClicked() {

            }

            override fun onLeftApplication() {

            }

            override fun onReturnedToApplication() {

            }

            override fun onImpression(p0: ImpressionData?) {

            }

        })

        if(isInternetConnectionAvailable(this)) {
            mainViewModel.setAdIsLoaded(true)
        }
    }

    //Internet Connection
    private fun isInternetConnectionAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
        */
    }

    //Settings
    private fun saveSettings(settings: Settings) {
        val settingsJson = gson.toJson(settings)
        sharedPreferences.edit().putString("settings", settingsJson).apply()
    }
    private fun getSettings() : Settings {
        return gson.fromJson(sharedPreferences.getString("settings", ""), Settings::class.java)
    }

    //First Run
    private fun isFirstRun(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            prefs.edit().putBoolean("isFirstRun", false).apply()
        }
        return isFirstRun
    }

    //ActionBar
    private fun setActionBarTitle() {
        mainViewModel.getTitle().observe(this, Observer {
                title ->
            supportActionBar?.title = title
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> {
                navController.navigate(R.id.settings_activity)
            }
            R.id.info -> {
                val builder = AlertDialog.Builder(this)
                builder.setView(this.layoutInflater.inflate(R.layout.dialog_help, null))
                val dialog = builder.create()
                dialog.show()

                dialog.findViewById<Button>(R.id.ok_button)?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu_main, menu)

        val search = menu?.findItem(R.id.search)
        val searchView = search?.actionView as? SearchView
        if (searchView != null) {
            this.searchView = searchView
        }
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

        return true
    }

    //Search
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            currentQuery = query
            searchDatabase(query)
        }
        return true
    }
    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            currentQuery = query
            searchDatabase(query)
        }
        return true
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment

        mainViewModel.getFilteredDishList(searchQuery).observe(this) { list ->
            if (currentFragment is DishListFragment) {
                val adapter = currentFragment.getRecyclerAdapter()
                adapter.setList(list as ArrayList<Dish>?)
            }
        }
        mainViewModel.getFilteredBreakfast(searchQuery).observe(this) { list ->
            if (currentFragment is BreakfastFragment) {
                val adapter = currentFragment.getRecyclerAdapter()
                adapter.setList(list as ArrayList<Dish>?)
            }
        }
        mainViewModel.getFilteredLunch(searchQuery).observe(this) { list ->
            if (currentFragment is LunchFragment) {
                val adapter = currentFragment.getRecyclerAdapter()
                adapter.setList(list as ArrayList<Dish>?)
            }
        }
        mainViewModel.getFilteredDinner(searchQuery).observe(this) { list ->
            if (currentFragment is DinnerFragment) {
                val adapter = currentFragment.getRecyclerAdapter()
                adapter.setList(list as ArrayList<Dish>?)
            }
        }
    }

    //DefaultUriList
    private fun drawableToUri(context: Context, drawableId: Int): String {
        return "android.resource://${context.applicationContext.packageName}/$drawableId"
    }
    private fun getDefaultUriList(context: Context) : ArrayList<String> {
        val list : ArrayList<String> = ArrayList()
        list.add(drawableToUri(context, R.drawable.dish_apple))
        list.add(drawableToUri(context, R.drawable.dish_banana))
        list.add(drawableToUri(context, R.drawable.dish_borsh))
        list.add(drawableToUri(context, R.drawable.dish_cabbage_rolls))
        list.add(drawableToUri(context, R.drawable.dish_coffee))
        list.add(drawableToUri(context, R.drawable.dish_icecream))
        list.add(drawableToUri(context, R.drawable.dish_karbonara))
        list.add(drawableToUri(context, R.drawable.dish_tea))
        list.add(drawableToUri(context, R.drawable.dish_uha))
        list.add(drawableToUri(context, R.drawable.dish_yogurt))
        list.add(drawableToUri(context, R.drawable.dish_omelette))
        list.add(drawableToUri(context, R.drawable.dish_salad))
        return list
    }
}