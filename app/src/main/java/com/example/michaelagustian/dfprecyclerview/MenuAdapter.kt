package com.example.michaelagustian.dfprecyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdView
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import android.widget.RelativeLayout




class MenuAdapter(context: Context, items: List<Any>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MENU_ITEM_VIEW_TYPE = 0
    private val BANNER_AD_VIEW_TYPE = 1
    private val BANNER_DFP_VIEW_TYPE = 3

    private val mContext: Context = context
    private val mItems: List<Any> = items

    inner class MenuItemViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val menuItemName: TextView = view.findViewById(R.id.menu_item_name)
        val menuItemDescription: TextView = view.findViewById(R.id.menu_item_description)
        val menuItemPrice: TextView = view.findViewById(R.id.menu_item_price)
        val menuItemCategory: TextView = view.findViewById(R.id.menu_item_category)
        val menuItemImage: ImageView = view.findViewById(R.id.menu_item_image)
    }

    inner class DfpAdViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
//        val adContext: PublisherAdView = view.findViewById(R.id.ad_context)
        val adContext: RelativeLayout = view.findViewById(R.id.ad_context)
    }

    inner class AdViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return if (position % (mContext as MainActivity).ITEMS_PER_AD == 0)
//            BANNER_AD_VIEW_TYPE
            BANNER_DFP_VIEW_TYPE
        else
            MENU_ITEM_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MENU_ITEM_VIEW_TYPE -> {
                val menuItemLayoutView = LayoutInflater.from(mContext).inflate(R.layout.item_menu, parent, false)
                MenuItemViewHolder(menuItemLayoutView)
            }
            BANNER_AD_VIEW_TYPE -> {
                val bannerLayoutView = LayoutInflater.from(mContext).inflate(R.layout.item_ad, parent, false)
                AdViewHolder(bannerLayoutView)
            }
            else -> {
                val bannerLayoutView = LayoutInflater.from(mContext).inflate(R.layout.item_dfp_ad, parent, false)
                DfpAdViewHolder(bannerLayoutView)
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            MENU_ITEM_VIEW_TYPE -> {
                val menuItemHolder = holder as MenuItemViewHolder
                val menuItem = mItems[position] as Menu

                // Add the menu item details to the menu item view.
                menuItemHolder.menuItemImage.setImageResource(R.drawable.menu_item_image)
                menuItemHolder.menuItemName.text = menuItem.name
                menuItemHolder.menuItemPrice.text = menuItem.price
                menuItemHolder.menuItemCategory.text = menuItem.category
                menuItemHolder.menuItemDescription.text = menuItem.description
            }
            BANNER_AD_VIEW_TYPE -> {
                /**
                 * AdMob
                 */
                val bannerHolder = holder as AdViewHolder
                val adView = mItems[position] as AdView
                val adCardView = bannerHolder.itemView as ViewGroup
                // The DfpAdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // DfpAdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled DfpAdViewHolder.
                if (adCardView.childCount > 0) {
                    adCardView.removeAllViews()
                }
                if (adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }

                // Add the banner ad to the ad view.
                adCardView.addView(adView)
            }
            else -> {
                /**
                 * dfp
                 */
                val bannerHolder = holder as DfpAdViewHolder
//                val adView = mItems[position] as AdView
//                val adView = mItems[position] as PublisherAdView

                val adView = PublisherAdView(mContext)
                adView.setAdSizes(AdSize.BANNER)
                adView.adUnitId = (mContext as MainActivity).DFP_UNIT_ID

                bannerHolder.adContext.addView(adView)

                // Initiate a generic request to load it with an ad
                val adRequest = PublisherAdRequest.Builder().build()
                val androidId = getDeviceId()

                PublisherAdRequest.Builder().addTestDevice(androidId)
                adView.loadAd(adRequest)

//                AdRequest.Builder().addTestDevice(androidId)
//                adView.loadAd(adRequest)
//                bannerHolder.adContext.setAdSizes(AdSize.BANNER)
//                bannerHolder.adContext.adUnitId = (mContext as MainActivity).AD_UNIT_ID
//                bannerHolder.adContext.loadAd(adRequest)
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        return Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
    }

}