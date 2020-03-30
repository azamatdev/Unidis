package uz.codic.unidis.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import java.io.Serializable
import java.sql.Date
import java.sql.Timestamp

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "quantity") var quantity: String = "",
    @ColumnInfo(name = "salesPrice") var salesPrice: Double = 0.0,
    @ColumnInfo(name = "inputPrice") var inputPrice: Double = 0.0,
    @ColumnInfo(name = "productId") var productId: String = "",
    @ColumnInfo(name = "brand") var brand: String = ""
) : Serializable

data class Warehouse(
    var productId: String = "",
    var brandName: String = "",
    var productName: String = "",
    var quantity: Int = 0,
    var inputPrice: Double = 0.0,
    var salesPrice: Double = 0.0,
    var returnQuantity: Int = 1,
    var returnPrice: Double = 0.0
) : Serializable

data class OrderProduct(
    var name: String = "",
    var brand: String = "",
    var quantity: String = "",
    var price: Double = 0.0,
    var productId: String = "",
    var profit: Double = 0.0
)

data class Client(
    var clientId: String = "",
    var debt: Double = 0.0,
    var name: String = "",
    var number: String = "",
    var row: String = "",
    var shopNumber: String = ""
) : Serializable

data class Order(
    var clientId: String = "",
    var currency: String = "",
    var date: Long = 0,
    var exchangeRate: String = "",
    var debt: Double = 0.0,
    var givenMoney: String = "",
    var productAmount: String = "",
    var products: HashMap<String, OrderProduct> = HashMap()
) : Serializable


data class ProductModel(
    var productId: String = "",
    var name: String = "",
    var brandName: String = ""
)

data class InfoGeneral(
    var brandName: String = "",
    var quantity: Int = 0,
    var profit: Double = 0.0,
    var price : Double = 0.0,
    var date : Long = 0,
    var dayOfMonth : Int = 0,
    var month : Int = 0
):Comparable<InfoGeneral> {
    override fun compareTo(other: InfoGeneral): Int {
        return date.compareTo(other = other.date)
    }
}

data class Cash(
    var amount: Double = 0.0,
    var currency: Int = -1,
    var date : Long = 0,
    var orderId : String = ""
)

data class Chart(
    var quantity: Int = 0,
    var dayOfMonth : Int = 0,
    var month : Int = 0
)

data class Expense(
    var reason : String = "",
    var amount : Double = 0.0,
    var date : Long = 0,
    var currency : Int = 0
)