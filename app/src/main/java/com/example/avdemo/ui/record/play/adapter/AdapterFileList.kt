package com.example.avdemo.ui.record.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.avdemo.R

/**
 * desc: 录音文件 <br/>
 * time: 2019/10/25 14:50 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */

class AdapterFileList(val context: Context, val list: ArrayList<String>,
                      val onItemClick: OnItemClickListener) :
        RecyclerView.Adapter<AdapterFileList.FileListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        return FileListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycle_file_list, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {
        holder.tvFileName.text = list[position]
        holder.tvFileName.setOnClickListener {
            onItemClick.onItemClick(list[position])
        }
    }

    class FileListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName by lazy { itemView.findViewById<TextView>(R.id.tv_item_file_name) }
    }

    interface OnItemClickListener {
        fun onItemClick(fileName: String)
    }
}