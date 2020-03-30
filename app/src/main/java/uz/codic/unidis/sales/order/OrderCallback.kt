package uz.codic.unidis.sales.order

import uz.codic.unidis.data.Warehouse

interface OrderCallback{
    fun onItemClick(warehouse: Warehouse)

    fun onOrderAddClick(warehouse: Warehouse)
}