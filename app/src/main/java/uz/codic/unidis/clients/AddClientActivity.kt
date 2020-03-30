package uz.codic.unidis.clients

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_add_client.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Client
import uz.codic.unidis.utils.Utils

class AddClientActivity : AppCompatActivity() {

    val firestore = FirebaseFirestore.getInstance()
    val clientList = ArrayList<Client>()
    lateinit var clientListener : ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)


        btn_back_add_client.setOnClickListener {
            finish()
            overridePendingTransition(0,R.anim.slide_to_right)
        }
        clientListener = firestore.collection("clients").addSnapshotListener { queryDocumentSnapshots, e ->
            if(!clientList.isEmpty()){
                clientList.clear()
            }
            for (dc in queryDocumentSnapshots!!.documents) {
                clientList.add(dc.toObject(Client::class.java)!!)
            }

        }
        add_client.setOnClickListener {
            if(Utils.isNetworkAvailable(this)){
                if (!edtxt_client_name.text.toString().isEmpty() &&
                    edtxt_phone.text?.length == 18
                    && !edtxt_shop_number.text.toString().isEmpty()
                ) {

                    if(userDoesntExist(edtxt_client_name.text.toString(),edtxt_shop_number.text.toString())){
                        val hashmap = HashMap<String, String>()
                        hashmap["name"] = edtxt_client_name.text.toString()
                        hashmap["number"] = edtxt_phone.text.toString()
                        hashmap["shopNumber"] = edtxt_shop_number.text.toString()
                        if (edtxt_client_debt.text.toString() == "")
                            hashmap["debt"] = "0.0"
                        else
                            hashmap["debt"] = edtxt_client_debt.text.toString()
                        if (group_choices.checkedId == R.id.choice_a)
                            hashmap["row"] = "A"
                        else
                            hashmap["row"] = "B"
                        firestore.collection("clients").add(hashmap as Map<String, String>)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Muavaffaqiyatli  saqlandi", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Serverda xato: " + it.message, Toast.LENGTH_SHORT).show()
                            }
                    }
                    else{
                        Toast.makeText(this, "Ushbu mijoz allaqachon kiritilgan!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Ma'lumotlarni kiriting", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Internet aloqasi yo'q", Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun userDoesntExist(name: String, shopNumber : String): Boolean {
        var row = ""
            if (group_choices.checkedId == R.id.choice_a)
                row = "A"
            else
                row = "B"
        for(client in clientList){
            if(client.name.toLowerCase() == name.toLowerCase() && client.shopNumber == shopNumber && client.row == row)
                return false
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,R.anim.slide_to_right)
    }

    override fun onStop() {
        clientListener.remove()
        super.onStop()
    }
}
