package eu.electricocean.quiz

data class Question (
    var id:Int? = null,
    var question:String? = null,
    var image: Int? = null,
    var options: ArrayList<String> = ArrayList<String>(),
    var correctAnswer: Int? = null
)