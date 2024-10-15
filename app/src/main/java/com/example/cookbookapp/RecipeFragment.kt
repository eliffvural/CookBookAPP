package com.example.cookbookapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cookbookapp.databinding.FragmentListBinding
import com.example.cookbookapp.databinding.FragmentRecipeBinding


class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener{selectImage(it)}
        binding.save.setOnClickListener { save(it) }
        binding.delete.setOnClickListener { selectImage(it) }

        arguments?.let {
            val information= RecipeFragmentArgs.fromBundle(it).information

            if (information=="new"){
                //will add new recipe
                binding.delete.isEnabled= false
                binding.save.isEnabled=true
            }
            else{
                //will show added recipe
                binding.delete.isEnabled= true
                binding.save.isEnabled=false
            }

        }
    }

    fun save(view: View){

    }

    fun delete(view: View){

    }

    fun selectImage(view: View){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}