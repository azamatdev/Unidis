package uz.codic.unidis.manager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_manager.*
import kotlinx.android.synthetic.main.fragment_general.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Cash
import uz.codic.unidis.data.InfoGeneral
import uz.codic.unidis.data.Order
import uz.codic.unidis.utils.getFormattedNumber
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat

class ManagerActivity : AppCompatActivity() {

    val firestore = FirebaseFirestore.getInstance()

    var listener : StatisticsListener? = null
    lateinit var viewPagerAdapter : ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        firestore.collection("cash").addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
            if (e != null) {
                Log.w("Tag", "listen:error", e)
                return@EventListener
            }
            var uzs = 0.0
            var usd = 0.0

            for(document in snapshots!!.documents){
                var cash = document.toObject(Cash::class.java)!!
                if(cash.currency == 0)
                    usd += cash.amount
                else
                    uzs += cash.amount
            }

            cash_total_uzs.text = "${getFormattedNumber(uzs)}"
            cash_total_usd.text = "${getFormattedNumber(usd)} $"
        })
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        view_pager.adapter = viewPagerAdapter

        viewpagertab.setViewPager(view_pager)

        btn_back.setOnClickListener {
            finish()
            overridePendingTransition(0,R.anim.slide_to_right)
        }



    }

    fun setStatisticsListener(listener: StatisticsListener){
        this.listener = listener
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,R.anim.slide_to_right)
    }


}
