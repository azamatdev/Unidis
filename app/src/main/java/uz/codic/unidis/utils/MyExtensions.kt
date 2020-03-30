package uz.codic.unidis.utils

import android.content.Context
import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.Toast
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

// here this refers to the receiver object, THAT instance which we are calling the extension function

/**
    Toast functions
 1. For activity
 2. For fragment
 **/
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun toast(context: Context, message: String) {
    context.toast(message)
}

/***
 * Get formatted number
 * 1. Double
 * 2. Integer
 */

fun getFormattedNumber(number: Double): String {
    return when {
        number in 1000.0..9999.0 -> DecimalFormat("#,###.##").format(
            number.toBigDecimal().setScale(
                1,
                RoundingMode.HALF_EVEN
            )
        )
        number in 10000.0..99999.0 -> DecimalFormat("##,###.##").format(
            number.toBigDecimal().setScale(
                1,
                RoundingMode.HALF_EVEN
            )
        )
        number in 100000.0..999999.0 -> DecimalFormat("###,###.##").format(
            number.toBigDecimal().setScale(
                1,
                RoundingMode.HALF_EVEN
            )
        )
        number > 1000000 -> DecimalFormat("#,###.##").format(number.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN))
        else -> number.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString()
    }

}

fun getFormattedNumber(number: Int): String {
    return when {
        number in 1000..9999 -> DecimalFormat("#,###.##").format(
            number.toBigDecimal().setScale(
                1,
                RoundingMode.HALF_EVEN
            )
        )
        number in 10000..99999 -> DecimalFormat("##,###.##").format(
            number.toBigDecimal().setScale(
                1,
                RoundingMode.HALF_EVEN
            )
        )
        number in 100000..999999 -> DecimalFormat("###,###.##").format(
            number.toBigDecimal().setScale(
                1,
                RoundingMode.HALF_EVEN
            )
        )
        number > 1000000 -> DecimalFormat("#,###.##").format(number.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN))
        else -> number.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString()
    }

}


/**
 * Show date Ranges
 */

fun getSevenDaysBefore(): Long{
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())
    calendar.add(Calendar.DAY_OF_YEAR, -7)
    return calendar.getTime().time
}

fun getSevendaysBeforeTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())
    calendar.add(Calendar.DAY_OF_YEAR, -7)
    return calendar.getTime().time
}

fun getThirtyDaysBeforeTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())
    calendar.add(Calendar.DAY_OF_YEAR, -30)
    return calendar.getTime().time
}

fun getYearBeforeTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())
    calendar.add(Calendar.DAY_OF_YEAR, -365)
    return calendar.getTime().time
}


/**
 * Log
 */

fun log(tag : String, message: String){
    Log.d(tag,message)
}

