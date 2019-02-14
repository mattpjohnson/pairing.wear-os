package com.mattpjohnson.pairing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.TextView
import android.widget.RelativeLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


class MainMenuAdapter(
    private val context: Context,
    dataArgs: ArrayList<PairMenuItem>,
    private val callback: AdapterCallback?
) : RecyclerView.Adapter<MainMenuAdapter.RecyclerViewHolder>() {

    private var dataSource = ArrayList<PairMenuItem>()

    private val drawableIcon: String? = null

    interface AdapterCallback {
        fun onItemClicked(menuPosition: Int?)
    }


    init {
        this.dataSource = dataArgs
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pair_menu_item, parent, false)

        return RecyclerViewHolder(view)
    }

    class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var menuContainer: RelativeLayout
        internal var pairTextView: TextView
        internal var mnemonicTextView: TextView
        internal var menuIcon: ImageView

        init {
            menuContainer = view.findViewById(R.id.pair_menu_container)
            pairTextView = view.findViewById(R.id.pair_menu_pair)
            mnemonicTextView = view.findViewById(R.id.pair_menu_mnemonic)
            menuIcon = view.findViewById(R.id.pair_menu_icon)
        }
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val data_provider = dataSource[position]

        holder.pairTextView.text = data_provider.pair
        holder.mnemonicTextView.text = data_provider.mnemonic
        holder.menuIcon.setImageIcon(null)
        if (data_provider.imageUrl != null) {
            DownloadImageTask(holder.menuIcon)
                .execute(data_provider.imageUrl)
        }
        holder.menuContainer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                callback?.onItemClicked(position)
            }
        })
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }
}

private class DownloadImageTask(internal var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg urls: String): Bitmap? {
        val urldisplay = urls[0]
        var mIcon11: Bitmap? = null
        try {
            val `in` = java.net.URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mIcon11
    }

    override fun onPostExecute(result: Bitmap) {
        bmImage.setImageBitmap(result)
    }
}

class PairMenuItem(val pair: String?, val mnemonic: String?, val imageUrl: String?)