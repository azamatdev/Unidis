package uz.codic.unidis.clients

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_client.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Client

class ClientActivity : AppCompatActivity() {

    var adapter = ClientAdapter()
    var placeHolderAdapter = ClientPlaceholderAdapter()
    val database = FirebaseFirestore.getInstance()
    private lateinit var listener: ListenerRegistration
    val clientList = ArrayList<Client>()
    lateinit var animation: LayoutAnimationController

    override fun onDestroy() {
        overridePendingTransition(0, 0)
        super.onDestroy()
        Log.d("Destroy", "Call")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        client_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        client_recycler.adapter = placeHolderAdapter
        animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
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
            val addActivity = Intent(this, AddClientActivity::class.java)
            startActivity(addActivity)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        btn_back.setOnClickListener {
            finish()
            overridePendingTransition(0,R.anim.slide_to_right)
        }
    }

    override fun onPause() {
        listener.remove()
        super.onPause()
    }

    override fun onResume() {
        if(!clientList.isEmpty())
        database.collection("clients").orderBy("name", Query.Direction.ASCENDING)
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
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,R.anim.slide_to_right)
    }


}
