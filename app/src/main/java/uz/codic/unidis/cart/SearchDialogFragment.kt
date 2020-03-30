package uz.codic.unidis.cart

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_search_clients.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Product
import com.arlib.floatingsearchview.FloatingSearchView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_cart.*
import android.text.Html
import android.R.attr.textColor
import android.graphics.Color
import android.graphics.Color.parseColor
import android.support.v4.content.ContextCompat
import android.view.*
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.ArrayList
import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


class SearchDialogFragment : DialogFragment() {

    public var DIALOGTAG = "DialogTag"

    val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return activity?.layoutInflater?.inflate(R.layout.fragment_search_clients, container, false)
    }

    val clientList = ArrayList<Client>()
    val suggestionList = ArrayList<Client>()
    lateinit var mCallback: CartCallback

    fun setCallback(cartCallback: CartCallback) {
        mCallback = cartCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        search_row_tabs.removeAllTabs()
//        val ATab = search_row_tabs.newTab()
//        ATab.text = "A qator"
//        val BTab = search_row_tabs.newTab()
//        BTab.text = "B qator"
//        search_row_tabs.addTab(ATab, 0, true)
//        search_row_tabs.addTab(BTab, 1)

        firestore.collection("clients").orderBy("name", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { query ->
                if (clientList.isEmpty())
                    clientList.clear()

                for (doc in query) {
                    val client = doc.toObject(Client::class.java)
                    client.clientId = doc.id
                    Log.d("TagTest", client.name)
                    clientList.add(client)
                }
            }



        floating_search_view.setOnQueryChangeListener { oldQuery, newQuery ->

            if (!oldQuery.equals("") && newQuery.equals("")) {
                floating_search_view.clearSuggestions()
                floating_search_view.hideProgress()
            } else {

                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.
                floating_search_view.showProgress()
//                Collections.binarySearch(clientList, newQuery)
//                var row = ""
//                if(search_row_tabs.selectedTabPosition == 0)
//                    row = "A"
//                else
//                    row = "B"

//                firestore.collection("clients").orderBy("name").startAt(newQuery).endAt(newQuery + '\uf8ff').limit(5)
//                    .get().addOnSuccessListener { query ->
//                        val list = ArrayList<Client>()
//                        Log.d("FireTagSearch", query.documents.toString())
//                        for (doc in query) {
//                            val client = doc.toObject(Client::class.java)
//                            list.add(client)
//                        }
//                        floating_search_view.swapSuggestions(list)
//                    }

                floating_search_view.swapSuggestions(searchForQuery(newQuery))

//                Log.d("TagSearch - OldQuery - ", oldQuery)
//                Log.d("TagSearch - NewQuery - ", newQuery)

            }

//            mSearchView.swapSuggestions(newSuggestions)
        }


        floating_search_view.setOnBindSuggestionCallback { view, lefticon, textview, item, itemPosition ->

            val client: Client = item as Client
            textview.text = client.shopNumber + " - " + client.name
            textview.setTextColor(Color.parseColor("#424242"))
//            val text = colorSuggestion.getBody()
//                .replaceFirst(
//                    floating_search_view.query,
//                    "<font color=\"" + textLight + "\">" + floating_search_view.getQuery() + "</font>"
//                )
            textview.setText(item.body)
        }

        floating_search_view.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {

                mCallback.onSearchClick(searchSuggestion as Client)
                Log.d("SearchTag", "onSuggestionClicked()")
                floating_search_view.setSearchFocused(false)
                hideKeyboardFrom(context!!, dialog.currentFocus!!)
                dialog.cancel()
//                mLastQuery = searchSuggestion.body
            }

            override fun onSearchAction(query: String) {

                Log.d("SearchTag", "onSearchAction()")
            }
        })

    }

    fun searchForQuery(query: String): ArrayList<Client> {
        suggestionList.clear()
        for( client in clientList){
            if(client.name.toLowerCase().contains(query.toLowerCase())){
                suggestionList.add(client)
            }
        }
        return suggestionList
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    companion object {
        private val ARG_CAUGHT = "warehouse"
        fun newInstance(): SearchDialogFragment {
            val args: Bundle = Bundle()
//            args.putSerializable(ARG_CAUGHT, product)
            val fragment = SearchDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}