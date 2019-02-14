package com.mattpjohnson.pairing

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.support.wear.widget.WearableRecyclerView
import android.support.wear.widget.WearableLinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


class MainActivity : WearableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()


        val recyclerView = findViewById<WearableRecyclerView>(R.id.first_letter_recycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.isEdgeItemsCenteringEnabled = true
        recyclerView.layoutManager = WearableLinearLayoutManager(this)


        val queue = Volley.newRequestQueue(this)
        val url = "https://api.airtable.com/v0/" + com.mattpjohnson.pairing.BuildConfig.airtableBase + "/Pairs?view=Grid%20view"

        val stringRequest = object : JsonObjectRequest(
            Request.Method.GET, url,
            null,
            Response.Listener<JSONObject> { response ->
                val records = response.getJSONArray("records")

                val pairsGroupedByFirstLetter = HashMap<String, MutableList<PairMenuItem>>()
                val menuItems = ArrayList<PairMenuItem>()

                for (i in 0 until records.length()) {
                    val record = records.getJSONObject(i)
                    val fields = record.getJSONObject("fields")
                    val images = fields.optJSONArray("Image")
                    var imageUrl: String? = null

                    if (images != null) {
                        imageUrl = (images[0] as JSONObject)
                            .optJSONObject("thumbnails")
                            .optJSONObject("small")
                            .optString("url")
                    }

                    val menuItem = PairMenuItem(
                        fields.optString("Pair"),
                        fields.optString("Mnemonic"),
                        imageUrl
                    )

                    val firstLetter = fields.optString("First Letter")
                    if (!pairsGroupedByFirstLetter.containsKey(firstLetter)) {
                        pairsGroupedByFirstLetter[firstLetter] = mutableListOf()
                    }

                    pairsGroupedByFirstLetter[firstLetter]?.add(menuItem)

                    menuItems.add(menuItem)
                }

                recyclerView.adapter = MainMenuAdapter(this, menuItems, object : MainMenuAdapter.AdapterCallback {
                    override fun onItemClicked(menuPosition: Int?) {}
                })

                recyclerView.requestFocus()
            },
            Response.ErrorListener {}) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = "Bearer " + com.mattpjohnson.pairing.BuildConfig.airtableApiKey

                return params
            }
        }

        queue.add(stringRequest)
    }
}