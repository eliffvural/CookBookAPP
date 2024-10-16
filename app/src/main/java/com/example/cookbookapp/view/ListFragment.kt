package com.example.cookbookapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.cookbookapp.adapter.RecipeAdapter
import com.example.cookbookapp.databinding.FragmentListBinding
import com.example.cookbookapp.model.Recipe
import com.example.cookbookapp.roomdb.RecipeDAO
import com.example.cookbookapp.roomdb.RecipeDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: RecipeDatabase
    private lateinit var recipeDAO: RecipeDAO
    private val mDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Database ve DAO'yu burada başlatıyoruz
        db = Room.databaseBuilder(requireContext(), RecipeDatabase::class.java, "Recipes")
            .build()
        recipeDAO = db.recipeDAO()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { addNew(it) }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getDatas()
    }

    private fun getDatas() {
        mDisposable.add(
            recipeDAO.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(recipes: List<Recipe>) {
        val adapter = RecipeAdapter(recipes)
        binding.recyclerView.adapter = adapter // recyclerView ID'sini kullandık
    }

    fun addNew (view: View) {
        val action = ListFragmentDirections.actionListFragmentToRecipeFragment(information = "new", id = 0)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
}

