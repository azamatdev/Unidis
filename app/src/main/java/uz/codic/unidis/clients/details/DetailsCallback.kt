package uz.codic.unidis.clients.details

import uz.codic.unidis.data.Order

interface DetailsCallback{
    fun onItemClick(order : Order)
}