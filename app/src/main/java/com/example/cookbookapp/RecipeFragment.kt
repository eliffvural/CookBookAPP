package com.example.cookbookapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cookbookapp.databinding.FragmentListBinding
import com.example.cookbookapp.databinding.FragmentRecipeBinding
import com.google.android.material.snackbar.Snackbar


class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String> //izin istemek için
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent> //galeriye girmek için

    private var chosedImage: Uri? =null
    private var chosedBitmap: Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

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
        binding.saveButton.setOnClickListener { saveButton(it) }
        binding.deleteButton.setOnClickListener { deleteButton(it) }

        arguments?.let {
            val information= RecipeFragmentArgs.fromBundle(it).information

            if (information=="new"){
                //will add new recipe
                binding.deleteButton.isEnabled= false
                binding.saveButton.isEnabled=true
            }
            else{
                //will show added recipe
                binding.deleteButton.isEnabled= true
                binding.saveButton.isEnabled=false
            }

        }
    }

    fun saveButton(view: View){

    }

    fun deleteButton(view: View){

    }

    fun selectImage(view: View){

        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
            //izin verilmemis, izin istememiz gerek
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                //snackbar göstermemiz lazım, kullanicidan neden izin istediğimizi bir kez daha söyleyerek izin almamız lazım
                 Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım!", Snackbar.LENGTH_INDEFINITE).setAction(
                     "İzin Ver",
                     {

                     //izin isteyeceğiz}
                     }
                 ).show()
                }
            else{
                //izin isteyeceğiz.
            }
        }
        else{
            //izin verilmis, galeriye gidebilirim


        }

    }

    private fun registerLauncher(){

        activityResultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result-> if (result.resultCode==AppCompatActivity.RESULT_OK){
                val intentFromResult= result.data
            if (intentFromResult != null){
                //kullanicinin sectigi gorselin nerede kayıtlı oldugunu gosteriyor:
                intentFromResult.data
            }
        }
        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result->
            if (result){
                //izin verildi
                //galeriye gidebiliriz

            }
            else{
                //izin verilmedi
                Toast.makeText(requireContext(), "İzin verilmedi!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}