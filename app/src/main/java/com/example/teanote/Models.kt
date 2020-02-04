package com.example.teanote

class Posts(var name:String,var durl:String){
    constructor():this("","")
}
//whenever you want to retrieve data from firebase as a model, you have to put a consturctor with defualt values