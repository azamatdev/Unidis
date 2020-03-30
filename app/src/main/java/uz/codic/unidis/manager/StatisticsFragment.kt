package uz.codic.unidis.manager


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_statistics.*

import uz.codic.unidis.R
import uz.codic.unidis.clients.returnProduct.ReturnCallback
import uz.codic.unidis.data.InfoGeneral
import uz.codic.unidis.data.Order
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import uz.codic.unidis.utils.getSevendaysBeforeTimestamp
import uz.codic.unidis.utils.getThirtyDaysBeforeTimestamp
import uz.codic.unidis.utils.getYearBeforeTimestamp


class StatisticsFragment : Fragment(), ReturnCallback, StatisticsListener, DatePickerDialog.OnDateSetListener {

    val firestore = FirebaseFirestore.getInstance()

    var brandList = ArrayList<String>()
    var adapter = StatisticsBrandAdapter(this)

    var hashMapDates = HashMap<Int, List<InfoGeneral>>()

    var orderList = ArrayList<InfoGeneral>()
    var generalList = ArrayList<InfoGeneral>()
    lateinit var ordersListener: ListenerRegistration

    val sdfDay = SimpleDateFormat("dd")
    val sdfMonth = SimpleDateFormat("MM")
    var checkedBrandName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val activity = activity as ManagerActivity
        activity.setStatisticsListener(this)
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart_brand_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        chart_brand_recycler.adapter = adapter

        setSpinner()
        getStatistics()

    }

    private fun setSpinner() {
        ArrayAdapter.createFromResource(
            context,
            R.array.date,
            R.layout.spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            // Apply the adapter to the spinner
            date_spinner.adapter = adapter
        }

        date_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> getDateRangeResult(System.currentTimeMillis(), getSevendaysBeforeTimestamp())
                    1 -> getDateRangeResult(System.currentTimeMillis(), getThirtyDaysBeforeTimestamp())
                    2 -> getDateRangeResult(System.currentTimeMillis(), getYearBeforeTimestamp())
                    3 -> {
                        val now = Calendar.getInstance()
                        var dpd = DatePickerDialog.newInstance(
                            this@StatisticsFragment,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                        )
                        dpd.setStartTitle("Kundan")
                        dpd.setEndTitle("Kungacha")
                        dpd.show(activity!!.fragmentManager, "Datepickerdialog")
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


    }

    fun getDateRangeResult(endDate: Long, startDate: Long) {
        ordersListener =
            firestore.collection("orders")
                .whereLessThan("date", endDate)
                .whereGreaterThan("date", startDate)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w("Tag", "listen:error", e)
                        return@EventListener
                    }

                    if (!orderList.isEmpty())
                        orderList.clear()
                    for (dc in snapshots!!.documents) {
                        var document = dc.toObject(Order::class.java)
                        for (order in document!!.products.values) {
                            var infoGeneral = InfoGeneral()
                            infoGeneral.brandName = order.brand
                            infoGeneral.quantity = order.quantity.toInt()
                            infoGeneral.profit = order.profit
                            infoGeneral.price = order.price
                            infoGeneral.date = document.date
                            infoGeneral.dayOfMonth = sdfDay.format(document.date).toInt()
                            infoGeneral.month = sdfMonth.format(document.date).toInt()
                            orderList.add(infoGeneral)
                        }
                    }
                    if (checkedBrandName != "")
                        doCalculation(checkedBrandName)
                })
    }

    private fun getStatistics() {
        firestore.collection("brands").get().addOnSuccessListener {
            for (snapshot in it.documents) {
//                var info = InfoGeneral()
                brandList.add(snapshot["name"] as String)
            }
            adapter.updateList(list = brandList)

            ordersListener =
                firestore.collection("orders")
                    .whereLessThan("date", System.currentTimeMillis())
                    .whereGreaterThan("date", getSevendaysBeforeTimestamp())
                    .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                        if (e != null) {
                            Log.w("Tag", "listen:error", e)
                            return@EventListener
                        }

                        if (!orderList.isEmpty())
                            orderList.clear()
                        for (dc in snapshots!!.documents) {
                            var document = dc.toObject(Order::class.java)
                            for (order in document!!.products.values) {
                                var infoGeneral = InfoGeneral()
                                infoGeneral.brandName = order.brand
                                infoGeneral.quantity = order.quantity.toInt()
                                infoGeneral.profit = order.profit
                                infoGeneral.price = order.price
                                infoGeneral.date = document.date
                                infoGeneral.dayOfMonth = sdfDay.format(document.date).toInt()
                                infoGeneral.month = sdfMonth.format(document.date).toInt()
                                orderList.add(infoGeneral)
                            }
                        }
                        doCalculation(brandList[0])
                        checkedBrandName = brandList[0]
                    })

        }

    }


    private fun doCalculation(brandName: String) {

        Collections.sort(orderList)

        val entries = ArrayList<Entry>()
        var totalQuantity = 0
        val series = LineGraphSeries<DataPoint>()
        for (i in 0 until 31) {
            var quantity = 0
            var month = 0
            for (j in 0 until orderList.size) {
                if (orderList[j].dayOfMonth == i && orderList[j].brandName == brandName) {
                    quantity += orderList[j].quantity
//                    totalQuantity += quantity
                }
            }
            if (quantity > 0)
                entries.add(Entry(i.toFloat(), quantity.toFloat()))

        }


//        graph.addSeries(series)
        val dataSet = LineDataSet(entries, brandName) // add entries to dataset
        if (context != null) {
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT)
            dataSet.setColor(ContextCompat.getColor(context!!, R.color.brandSelectedColor), 255)
            dataSet.setDrawValues(false)
            for (value in dataSet.values) {
                totalQuantity += value.y.toInt()
            }
            total_quantity.text = totalQuantity.toString()

            dataSet.lineWidth = 1f
            dataSet.circleHoleColor = ContextCompat.getColor(context!!, R.color.brandSelectedColor)

            for (i in 0 until dataSet.circleColorCount)
                dataSet.circleColors[i] = ContextCompat.getColor(context!!, R.color.brandSelectedColor)
        }
        val lineData = LineData(dataSet)

        if (chart != null) {
            chart.data = lineData
            chart.description.isEnabled = false
            loading.visibility = View.GONE
            chart.invalidate()
            chart.setGridBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
            chart.setDrawGridBackground(false)
//        val formatter = object : ValueFormatter() {
//            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                return sdf.format(value.toLong())
//            }
//        }

            val xAxis = chart.getXAxis()
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setGranularity(1f) // minimum axis-step (interval) is 1
//        xAxis.setValueFormatter(formatter)
        }
    }


//    private fun doCalculation() {
//
//    }

    override fun onBrandClick(brandName: String) {
        doCalculation(brandName)
        checkedBrandName = brandName
    }

    override fun onFirstClick() {

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
        if (startDate.timeInMillis < endDate.timeInMillis)
            getDateRangeResult(endDate = endDate.timeInMillis, startDate = startDate.timeInMillis)
        else
            Toast.makeText(context, "Iltimos, to'g'ri sanani kiriting!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StatisticsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onStop() {
        super.onStop()
        ordersListener.remove()
    }
}
