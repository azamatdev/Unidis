package uz.codic.unidis.expenses

interface ExpenseCallback{

    fun onAddClick(reason : String, expense : String, currency : Int)

}