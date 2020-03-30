package uz.codic.unidis.expenses

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_client.*
import kotlinx.android.synthetic.main.activity_expenses.*
import kotlinx.android.synthetic.main.activity_expenses.btn_back
import uz.codic.unidis.R
import uz.codic.unidis.data.Expense
import uz.codic.unidis.utils.*
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ExpensesActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, ExpenseCallback {


    val df = SimpleDateFormat("dd MMM")
    val firestore = FirebaseFirestore.getInstance()
    var listener: ListenerRegistration? = null

    val expenseDialog = ExpenseAddDialog.newInstance()

    var adapter = ExpenseAdapter()
    var expenseList = ArrayList<Expense>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses)

        showLastSevenDays()

        btn_choose_date_range.setOnClickListener {
            val now = Calendar.getInstance()
            var dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.setStartTitle("Kundan")
            dpd.setEndTitle("Kungacha")
            dpd.show(fragmentManager, "Datepickerdialog")
        }

        btn_back.setOnClickListener {
            finish()
            overridePendingTransition(0, R.anim.slide_to_right)
        }

        recycler_expense.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_expense.adapter = adapter
        recycler_expense.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        listener = firestore.collection("expenses").orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w("Error", "listen:error", e)
                    return@EventListener
                }

                if (!expenseList.isEmpty()) {
                    expenseList.clear()
                }
                var totalUZS = 0.0
                var totalUSD = 0.0
                for (dc in snapshots!!.documents) {
                    val expense = dc.toObject(Expense::class.java)
                    if (expense?.currency == 0) {
                        totalUSD += expense.amount
                    } else
                        totalUZS += expense!!.amount
                    expenseList.add(expense)
                }

                expense_total_uzs.text =
                    getFormattedNumber(totalUZS) + " so'm"
                expense_total_usd.text = getFormattedNumber(totalUSD) + " $"
                recycler_expense.layoutAnimation =
                    AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
                adapter.updateList(expenseList)
            })

        btn_add_expense.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                val fm = supportFragmentManager
                val orderDialogFragment =
                    ExpenseAddDialog.newInstance()
                orderDialogFragment.setCallback(this)
                orderDialogFragment.show(fm, "fragment_edit_name")
            } else {
                toast("Iltimos, internet aloqasini tiklang!")
            }
        }
    }

    private fun showLastSevenDays() {
        expense_start_date.text = df.format(Date(getSevenDaysBefore()))
        expense_end_date.text = df.format(Date(System.currentTimeMillis()))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_to_right)
    }

    override fun onDateSet(
        view: DatePickerDialog?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int,
        yearEnd: Int,
        monthOfYearEnd: Int,
        dayOfMonthEnd: Int
    ) {
        val startDate = Calendar.getInstance()
        startDate.set(year, monthOfYear, dayOfMonth)
        val endDate = Calendar.getInstance()
        endDate.set(yearEnd, monthOfYearEnd, dayOfMonthEnd)

        if (startDate.timeInMillis < endDate.timeInMillis) {
            expense_start_date.text = df.format(startDate.timeInMillis)
            expense_end_date.text = df.format(endDate.timeInMillis)
            getDateRangeExpenses(startDate.timeInMillis, endDate.timeInMillis)

        } else
            toast("Iltimos, to'g'ri sanani kiriting!")
    }

    override fun onStop() {
        if (listener != null)
            listener!!.remove()
        super.onStop()
    }

    fun getDateRangeExpenses(startDate : Long, endDate : Long){

        log("TAGCHECK", startDate.toString())
        log("TAGCHECK", endDate.toString())
        firestore.collection("expenses")
            .whereLessThan("date", endDate)
            .whereGreaterThan("date", startDate)
            .addSnapshotListener(EventListener<QuerySnapshot>{snapshots,e ->
                if(e != null){
                    Log.w("TagError", "listen:error" + e)
                    return@EventListener
                }


                if (!expenseList.isEmpty()) {
                    expenseList.clear()
                }
                var totalUZS = 0.0
                var totalUSD = 0.0
                for (dc in snapshots!!.documents) {
                    val expense = dc.toObject(Expense::class.java)
                    if (expense?.currency == 0) {
                        totalUSD += expense.amount
                    } else
                        totalUZS += expense!!.amount
                    expenseList.add(expense)
                }

                log("TAGCHECK", expenseList.size.toString())
                expense_total_uzs.text =
                    getFormattedNumber(totalUZS) + " so'm"
                expense_total_usd.text = getFormattedNumber(totalUSD) + " $"
                recycler_expense.layoutAnimation =
                    AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
                adapter.updateList(expenseList)


            })
    }
    override fun onAddClick(reason: String, expense: String, currency: Int) {
        var hashMap = HashMap<String, Any>()
        hashMap["reason"] = reason
        hashMap["currency"] = currency
        hashMap["amount"] = expense
        hashMap["date"] = System.currentTimeMillis()
        firestore.collection("expenses").add(hashMap)
            .addOnSuccessListener {
                expenseDialog.onSuccess()
            }
    }
}
