package uz.codic.unidis

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main.*
import uz.codic.unidis.clients.ClientFragment
import uz.codic.unidis.fixedPrice.FixedPriceActivity
import uz.codic.unidis.input.CheckFragment
import uz.codic.unidis.input.WarehouseFragment
import uz.codic.unidis.sales.SalesFragment

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().replace(R.id.main_container, ClientFragment()).commit()
        nav_view.setCheckedItem(R.id.nav_input)
        toolbar.title = "Savdo"


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        when (item.itemId) {
//            R.id.action_settings -> return true
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_input -> {
                // Handle the camera action
                supportFragmentManager.beginTransaction().replace(R.id.main_container, WarehouseFragment()).commit()
                toolbar.title = "Kirim"
            }
            R.id.nav_sales -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_container, SalesFragment()).commit()
                toolbar.title = "Savdo"

            }
            R.id.nav_expenses -> {
                val fixedPrice = Intent(this, FixedPriceActivity::class.java)
                startActivity(fixedPrice)
//                supportFragmentManager.beginTransaction().replace(R.id.main_container, CheckFragment()).commit()
//                toolbar.title = "Xarajat"

            }
            R.id.nav_clients -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_container, ClientFragment.newInstance())
                    .commit()
                toolbar.title = "Mijozlar"
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
