package uz.codic.unidis.sales


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.fragment_sales.*

import uz.codic.unidis.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class SalesFragment : Fragment() {

    lateinit var adapter : SalesBrandAdapter

    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var brandListener : ListenerRegistration
    lateinit var brandList : ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SalesBrandAdapter()

        sales_recycler.layoutManager = LinearLayoutManager(context)
        sales_recycler.adapter = adapter

        brandList = ArrayList()

        brandListener =  firestore.collection("brands").addSnapshotListener(
            EventListener { queryDocumentSnapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@EventListener
                }
                if (!brandList.isEmpty()) {
                    brandList.clear()
                }
                for (dc in queryDocumentSnapshots!!.documents) {
                    brandList.add(dc.data!!["name"] as String)
                }
                adapter.updateList(brandList)
            }
        )

    }

    override fun onPause() {
        brandListener.remove()
        super.onPause()
    }
}
