package uz.codic.unidis

data class brandsWithProducts(val name:String, val products: List<product>)

data class product(val name : String)