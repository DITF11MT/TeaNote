package com.example.teanote

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.shape_of_item.view.*


class RecyclerCustomAdapter(val arrayList: ArrayList<Posts>):RecyclerView.Adapter<CustomViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val cusomtView=layoutInflater.inflate(R.layout.shape_of_item,parent,false)
        return CustomViewHolder(cusomtView)
    }

    override fun getItemCount(): Int {

        return arrayList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.text.text=arrayList[position].name
        Picasso.get().load(arrayList[position].durl).into(holder.img)
        holder.posts=arrayList[position]
    }
}

class CustomViewHolder(val customView:View,var posts: Posts?=null):RecyclerView.ViewHolder(customView){
    val text=customView.findViewById<TextView>(R.id.textView_item)
    val img=customView.findViewById<ImageView>(R.id.imageView_item)
    val btn=customView.findViewById<ImageButton>(R.id.imageView_btn)

    init {

        customView.imageView_btn.setOnClickListener{
            val popup=PopupMenu(customView.context,it)
            popup.inflate(R.menu.item_menu)
            popup.setOnMenuItemClickListener {

                if(it.title.toString().equals("share",false)){//getting your menu items to do something in a bad way(check MainActivity)
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Image from FireBase called:${posts?.name}  "+posts?.durl

                    )
                    sendIntent.type = "text/plain"
                  customView.context.startActivity(sendIntent)

                }
                true
                if(it.title.toString().equals("download",false)){
                    val url=posts?.durl
                    val name=posts?.name
                    downloadFile(customView.context,name!!,".jpeg",
                        Environment.getExternalStorageState()+"/Images",url)
                }
                true
            }
            popup.show()

        }
    }



    fun downloadFile(
        context: Context,
        fileName: String,
        fileExtension: String,
        destinationDirectory: String?,
        url: String?
    ) {
        try{
        val downloadmanager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(destinationDirectory,fileName+fileExtension)//it creates a folder in your device (seen by the user) and stores images in it


        downloadmanager.enqueue(request)}
        catch (e:Exception){
            Toast.makeText(customView.context,e.toString(),Toast.LENGTH_LONG).show()
        }
    }


}
