package uz.codic.unidis.clients


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_client.*

import uz.codic.unidis.R
import uz.codic.unidis.data.Client

class ClientFragment : Fragment() {

    var adapter = ClientAdapter()
    var placeHolderAdapter = ClientPlaceholderAdapter()
    val database = FirebaseFirestore.getInstance()
    private lateinit var listener: ListenerRegistration
    val clientList = ArrayList<Client>()
    lateinit var animation: LayoutAnimationController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_client, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client_recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        client_recycler.adapter = placeHolderAdapter
        animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        listener = database.collection("clients").orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w("Error", "listen:error", e)
                    return@EventListener
                }

                if (!clientList.isEmpty()) {
                    clientList.clear()
                }
                for (dc in snapshots!!.documents) {
                    val client = Client(
                        dc.id,
                        dc["debt"].toString().toDouble(),
                        dc["name"].toString(),
                        dc["number"].toString(),
                        dc["row"].toString(),
                        dc["shopNumber"].toString()
                    )
//                    client.clientId = dc.id
//                    client.debt = dc["debt"].toString()
//                    client.name = dc["name"].toString()
//                    client.number = dc["number"].toString()
//                    client.row = dc["row"].toString()
//                    client.shopNumber = dc["shopNumber"].toString()
                    Log.d("TagCheck", client.clientId)
                    clientList.add(client)
                }
                client_recycler.layoutAnimation = animation
                client_recycler.adapter = adapter
                Log.d("TagCheck!", clientList[0].clientId)
                adapter.updateList(clientList)
            })


        fab_add_client.setOnClickListener {
            val addActivity = Intent(activity, AddClientActivity::class.java)
            startActivity(addActivity)
        }

    }

    override fun onPause() {
        listener.remove()
        super.onPause()
    }

    companion object {
        fun newInstance(): ClientFragment {
            val fragment = ClientFragment()
            return fragment
        }
    }
}
