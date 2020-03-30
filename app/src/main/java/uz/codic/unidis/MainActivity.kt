package uz.codic.unidis

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import uz.codic.unidis.clients.ClientActivity
import uz.codic.unidis.expenses.ExpensesActivity
import uz.codic.unidis.fixedPrice.FixedPriceActivity
import uz.codic.unidis.input.InputActivity
import uz.codic.unidis.manager.ManagerActivity
import uz.codic.unidis.sales.order.OrderActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //1.Menu Input
        menu_input.setOnClickListener {
            val main = Intent(this, InputActivity::class.java)
            startActivity(main)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //2.OrderActivity
        menu_sales.setOnClickListener {
            val main = Intent(this, OrderActivity::class.java)
            startActivity(main)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //3.OrderActivity
        menu_expenses.setOnClickListener {
            val main = Intent(this, ExpensesActivity::class.java)
            startActivity(main)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //4.Clients
        menu_clients.setOnClickListener {
            val main = Intent(this, ClientActivity::class.java)
            startActivity(main)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //5.Fixed Price
        menu_manager.setOnClickListener {
            val main = Intent(this, ManagerActivity::class.java)
            startActivity(main)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //6.Fixed Price
        menu_fixed_price.setOnClickListener {
            val main = Intent(this, FixedPriceActivity::class.java)
            startActivity(main)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0)
    }


    //    override fun onResume() {
//        super.onPause()
//    }
}
