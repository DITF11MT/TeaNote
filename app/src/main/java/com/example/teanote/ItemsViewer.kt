package com.example.teanote

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.*

class ItemsViewer : AppCompatActivity() {
    lateinit var fireList:ArrayList<Posts>
    lateinit var firebaseDatabase: DatabaseReference
    lateinit var recycleVal: RecyclerView
    lateinit var swipper: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_viewer)


            Toast.makeText(this, "Refresh.",Toast.LENGTH_SHORT).show()

        swipper=findViewById(R.id.swipee)
        recycleVal=findViewById(R.id.recycler_view)
        recycleVal.layoutManager= GridLayoutManager(this,2)
        fireList=ArrayList<Posts>()
        firebaseDatabase= FirebaseDatabase.getInstance().reference.child("posts")
        /*
          In RecyclerView if you access it from synthetic properties it may make the process of rendering the images a
          bit slower so its better if you assign it to a variable just like this example.

        */
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(snappy: DataSnapshot in p0.children){
                        fireList.add(snappy.getValue(Posts::class.java)!!)
                    }
                }
            }
        })
        swipper.setColorSchemeColors(Color.RED)
        runOnUiThread {
            recycleVal.adapter = RecyclerCustomAdapter(fireList)
        }

        swipper.setOnRefreshListener {
            runOnUiThread {
                recycleVal.adapter = RecyclerCustomAdapter(fireList)
            }
            swipper.isRefreshing=false
        }
    }
}
