package com.example.teanote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private var imagePreview: ImageView?=null
    private var img_name: EditText?=null
    private val PICK_IMAGE_REQUEST =1
    private var filePath: Uri?=null
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var firebaseDatabase: DatabaseReference?=null
    private lateinit var drawer:DrawerLayout
    private lateinit var navView:NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
          var toolbar:Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            drawer = findViewById(R.id.drawer_layout)
            navView=findViewById(R.id.nav_view)
            navView.setNavigationItemSelectedListener(this)//using the menu item listener
            navView.setCheckedItem(R.id.nav_upload)

            val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()

        }catch(e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
        }
        imagePreview=findViewById(R.id.imagePreview)
        img_name=findViewById(R.id.img_name)
        val chooseImageBtn= findViewById<Button>(R.id.choose_image)
        val uploadImageBtn = findViewById<Button>(R.id.upload_image)

        firebaseStorage = FirebaseStorage.getInstance()
        firebaseDatabase= FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference
        chooseImageBtn.setOnClickListener { ImagePicker() }
        uploadImageBtn.setOnClickListener { uploadImage() }
        navView.setCheckedItem(R.id.nav_upload)
    }//end of onCreate


    override fun onNavigationItemSelected(item: MenuItem): Boolean {//getting your menu items to do something in a proper way

        when (item.itemId) {
            R.id.nav_upload-> {
                Toast.makeText(this, "Upload Here", Toast.LENGTH_SHORT).show()
                navView.setCheckedItem(R.id.nav_upload)
            }
            R.id.nav_images->
            goToViewer()
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else
        super.onBackPressed()
    }

    private fun ImagePicker(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

    }
    private fun goToViewer(){


        val intent=Intent(this, ItemsViewer::class.java)
        startActivity(intent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imagePreview?.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if(filePath != null){
            var postt = Posts()
            val ref = storageReference?.child("sweet_pics/" + img_name?.text.toString() )//UUID.randomUUID().toString()
            val uploadTask=ref?.putFile(filePath!!)
            val urlTask = uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    var d=img_name?.text.toString()
                    if(d=="")
                    d=java.util.UUID.randomUUID().toString()
                    postt?.name=d
                    postt?.durl=downloadUri.toString()
                    firebaseDatabase?.child("posts")?.child(postt.name)?.setValue(postt)
                    Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                    imagePreview?.setImageResource(R.drawable.ic_launcher_background)
                    img_name?.text=null
                } else {

                }
            }

        }else{
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
        }
    }

}
