package com.example.cookbookapp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cookbookapp.databinding.FragmentRecipeBinding
import com.example.cookbookapp.databinding.RecyclerRowBinding

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeHolder>(){
    class RecipeHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        TODO("Not yet implemented")
    }
}