package com.example.cookbookapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.cookbookapp.databinding.FragmentListBinding
import com.example.cookbookapp.roomdb.RecipeDAO
import com.example.cookbookapp.roomdb.RecipeDatabase
import io.reactivex.rxjava3.disposables.CompositeDisposable


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private lateinit var db:  RecipeDatabase
    private lateinit var recipeDAO: RecipeDAO
    private val mDisposable= CompositeDisposable()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener{addNew(it)}


    }

     fun addNew(view: View){

        val action = ListFragmentDirections.actionListFragmentToRecipeFragment(information ="new", id=0)
        Navigation.findNavController(view).navigate(action)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }



}