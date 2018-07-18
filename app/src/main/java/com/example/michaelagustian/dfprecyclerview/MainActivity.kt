package com.example.michaelagustian.dfprecyclerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    // A banner ad is placed in every 8th position in the RecyclerView.
    val ITEMS_PER_AD = 8
    val AD_UNIT_ID = "ca-app-pub-3940256099942544/4177191030"

    // The RecyclerView that holds and displays banner ads and menu items.
    private var recyclerView: RecyclerView? = null

    // List of banner ads and MenuItems that populate the RecyclerView.
//    private val recyclerViewItems: List<Objects> = ArrayList()
    private val recyclerViewItems: MutableList<Any> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManager

        addMenuItemsFromJson()
        addBannerAds()
        loadBannerAds()

        val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> = MenuAdapter(this, recyclerViewItems)
        recyclerView?.adapter = adapter
    }

    override fun onResume() {
        for (item in recyclerViewItems) {
            if (item is AdView) {
                item.resume()
            }
        }
        super.onResume()
    }

    override fun onPause() {
        for (item in recyclerViewItems) {
            if (item is AdView) {
                item.pause()
            }
        }
        super.onPause()
    }

    override fun onDestroy() {
        for (item in recyclerViewItems) {
            if (item is AdView) {
                item.destroy()
            }
        }
        super.onDestroy()
    }

    private fun loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(0)
    }

    private fun loadBannerAd(index: Int) {

        if (index >= recyclerViewItems.size) {
            return
        }

        val item = recyclerViewItems[index] as? AdView
                ?: throw ClassCastException("Expected item at index " + index + " to be a banner ad"
                        + " ad.")

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        item.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to" + " load the next banner ad in the items list.")
                loadBannerAd(index + ITEMS_PER_AD)
            }
        }

        // Load the banner ad.
        item.loadAd(AdRequest.Builder().build())
    }

    private fun addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        var i = 0
        while (i <= recyclerViewItems.size) {
            val adView = AdView(this@MainActivity)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = AD_UNIT_ID
            recyclerViewItems.add(i, adView)
            i += ITEMS_PER_AD
        }
    }

    private fun addMenuItemsFromJson() {
        try {
//            val jsonDataString = readJsonDataFromFile()
//            Log.d("jsonDataString", jsonDataString)
//            val menuItemsJsonArray = JSONArray(jsonDataString)

//            val jsonDataString = getString(R.string.menu_items_json)
//            val gson = GsonBuilder().setPrettyPrinting().create()
//            val menuItemsJsonArray: List<Menu> = gson.fromJson(jsonDataString, object : TypeToken<List<Menu>>() {}.type)

            val menuItemsJsonArray = addMenuList()

            for (i in 0 until menuItemsJsonArray.size) {

//                val menuItemObject = menuItemsJsonArray.getJSONObject(i)
//
//                val menuItemName = menuItemObject.getString("name")
//                val menuItemDescription = menuItemObject.getString("description")
//                val menuItemPrice = menuItemObject.getString("price")
//                val menuItemCategory = menuItemObject.getString("category")
//                val menuItemImageName = menuItemObject.getString("photo")

                val menuItemName = menuItemsJsonArray[i].name
                val menuItemDescription = menuItemsJsonArray[i].description
                val menuItemPrice = menuItemsJsonArray[i].price
                val menuItemCategory = menuItemsJsonArray[i].category
                val menuItemImageName = menuItemsJsonArray[i].imageName

                val menuItem = Menu(menuItemName, menuItemDescription, menuItemPrice, menuItemCategory, menuItemImageName)
                recyclerViewItems.add(menuItem)
                Log.d("recyclerView", "ke-$i isinya $menuItemName")
            }
        } catch (exception: IOException) {
            Log.e(MainActivity::class.java.name, "Unable to parse JSON file.", exception)
        } catch (exception: JSONException) {
            Log.e(MainActivity::class.java.name, "Unable to parse JSON file.", exception)
        }
    }

    @Throws(IOException::class)
    private fun readJsonDataFromFile(): String {
        var inputStream: InputStream? = null
        val builder = StringBuilder()

        try {
            inputStream = resources.openRawResource(R.raw.menu_items)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream!!, "UTF-8"))

            while (bufferedReader.readLine() != null) {
                builder.append(bufferedReader.readLine())
            }
        } finally {
            inputStream?.close()
        }

        return String(builder)
    }

    private fun addMenuList(): List<Menu> {
        val menuItemsJsonArray: MutableList<Menu> = mutableListOf()

        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))
        menuItemsJsonArray.add(Menu("Cherry Avocado", "cherries, garlic, serrano, mayo", "$7.00", "Dinner - Salads", "menu_item_image"))

        return menuItemsJsonArray
    }
}
