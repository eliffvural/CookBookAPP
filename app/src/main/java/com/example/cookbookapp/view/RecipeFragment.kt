package com.example.cookbookapp.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.graphics.ImageDecoder
import android.os.Build
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

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){

            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //izin verilmemis, izin istememiz gerek
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //snackbar göstermemiz lazım, kullanicidan neden izin istediğimizi bir kez daha söyleyerek izin almamız lazım
                    Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım!", Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin Ver",
                        {

                            //izin isteyeceğiz}
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    ).show()
                }
                else{
                    //izin isteyeceğiz.

                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }
            }
            else{
                //izin verilmis, galeriye gidebilirim

                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


            }

        }else{

            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin verilmemis, izin istememiz gerek
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //snackbar göstermemiz lazım, kullanicidan neden izin istediğimizi bir kez daha söyleyerek izin almamız lazım
                    Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım!", Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin Ver",
                        {

                            //izin isteyeceğiz}
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    ).show()
                }
                else{
                    //izin isteyeceğiz.

                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }
            }
            else{
                //izin verilmis, galeriye gidebilirim

                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


            }
        }



    }

    private fun registerLauncher(){

        activityResultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result-> if (result.resultCode==AppCompatActivity.RESULT_OK){
                val intentFromResult= result.data
            if (intentFromResult != null){
                //kullanicinin sectigi gorselin nerede kayıtlı oldugunu gosteriyor:
                chosedImage=intentFromResult.data


                try{
                    if(Build.VERSION.SDK_INT>=28){
                        val source= ImageDecoder.createSource(requireActivity().contentResolver, chosedImage !!)
                        chosedBitmap=ImageDecoder.decodeBitmap(source)
                        binding.imageView.setImageBitmap(chosedBitmap)

                    }
                    else{
                        chosedBitmap=MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,chosedImage)
                        binding.imageView.setImageBitmap(chosedBitmap)
                    }
                } catch (e: Exception){
                    println(e.localizedMessage)
                }



                //secilen gorseli bitmap e ceviric
            }
        }
        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result->
            if (result){
                //izin verildi
                //galeriye gidebiliriz
                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

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