package uz.codic.unidis.cart

import uz.codic.unidis.data.Product

interface CartCallback{
    fun deleteProduct(product: Product, position: Int)

    fun editProduct(product: Product)

    fun onOrderEditClick(product: Product)

    fun onSearchClick(client: Client)
}