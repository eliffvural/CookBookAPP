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
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.cookbookapp.roomdb.RecipeDatabase  // Örnek import satırı

import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.cookbookapp.databinding.FragmentRecipeBinding
import com.example.cookbookapp.model.Recipe
import com.example.cookbookapp.roomdb.RecipeDAO
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import kotlin.math.max


class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String> //izin istemek için
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent> //galeriye girmek için

    private var chosedImage: Uri? =null
    private var chosedBitmap: Bitmap? =null
    private var chosedRecipe: Recipe? =null

    private val mDisposable = CompositeDisposable() //çok fazla istek gonderdigimizde karsilasiriz.


   private lateinit var db: RecipeDatabase
   private lateinit var recipeDAO: RecipeDAO



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db = Room.databaseBuilder(requireContext(), RecipeDatabase::class.java, name = "Recipes").build()
        recipeDAO=db.recipeDAO()

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
                chosedRecipe=null
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

    private fun handleResponse(recipe: Recipe){
        binding.nameText.setText(recipe.name)
        binding.recipeText.setText(recipe.ingredient)
        val bitmap=BitmapFactory.decodeByteArray(recipe.image,0,recipe.image.size)
        binding.imageView.setImageBitmap(bitmap)
        chosedRecipe=recipe
    }

    fun saveButton(view: View){

        val name= binding.nameText.text.toString()
        val ingredient=binding.recipeText.text.toString()

        //val smallBitmap = smallBitmapCreate(chosedBitmap, 300)

        if(chosedBitmap != null){
            val smallBitmap = smallBitmapCreate(chosedBitmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50, outputStream)
            val byteArray= outputStream.toByteArray()

            val recipe= Recipe(name,ingredient,byteArray)

            //rx java

            mDisposable.add(recipeDAO
                .insert(recipe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForInsert))

        }



    }

    private fun handleResponseForInsert(){
        //bir önceki fragmenta dön
        val action= RecipeFragmentDirections.actionRecipeFragmentTolistFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    fun deleteButton(view: View){

        if(chosedRecipe!=null){
            mDisposable.add(
            recipeDAO.delete(recipe = chosedRecipe!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForInsert)
            )
        }

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

    private fun smallBitmapCreate(bitmapUserChosed: Bitmap, maxBoyut: Int) : Bitmap{
        var width= bitmapUserChosed.width
        var height= bitmapUserChosed.height

        val bitmapOrani: Double = width.toDouble()/ height.toDouble()

        if(bitmapOrani>1){

            //görsel yatay
            width = maxBoyut
            val kisaltilmisYukseklik = width/bitmapOrani
            height = kisaltilmisYukseklik.toInt()
        } else{

            //görsel dikey
            height= maxBoyut
            val kisaltilmisGenislik = height*bitmapOrani
            width=kisaltilmisGenislik.toInt()
        }

        return Bitmap.createScaledBitmap(bitmapUserChosed, width,height, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }


}