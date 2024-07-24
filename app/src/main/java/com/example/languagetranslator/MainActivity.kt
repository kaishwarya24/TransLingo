package com.example.languagetranslator

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //UI Views
    private lateinit var sourceLanguageEt: EditText
    private lateinit var targetLanguageTv: TextView
    private lateinit var sourceLanguageChooseBtn: MaterialButton
    private lateinit var targetLanguageChooseBtn: MaterialButton
    private lateinit var translateBtn: MaterialButton

    companion object {
        private const val  TAG = "MAIN_TAG"
    }


    // will contain list with language code and title
    private var languageArrayList : ArrayList<ModelLanguage>? = null
    // default languages
    private var sourcelanguageCode = "en"
    private var sourcelanguageTitle = "English"
    private var targetLanguageCode = "ur"
    private var targetLanguageTitle = "Urdu"


    private lateinit var translatorOptions: TranslatorOptions

    private lateinit var  translator: Translator

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceLanguageEt = findViewById(R.id.sourceLanguageEt)
        targetLanguageTv = findViewById(R.id.targetLanguageTv)
        sourceLanguageChooseBtn = findViewById(R.id.sourceLanguageChooseBtn)
        targetLanguageChooseBtn = findViewById(R.id. targetLanguageChooseBtn)
        translateBtn = findViewById(R.id.translateBtn)

        // init setup process dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)


        loadAvailableLanguages()
        //handle sourceLanguageChooseBtn click, choose source language (from list) which you want to translate
        sourceLanguageChooseBtn.setOnClickListener {
            sourceLanguageChoose()

        }

//handle targetLanguageChooseBtn click, choose target language (from list) to which you want to translate
            targetLanguageChooseBtn.setOnClickListener {
                targetLanguageChoose()
            }

//handle translateBtn click, translate text to desired Language
                translateBtn.setOnClickListener {
                    validateData()
                }

    }
    private var sourceLanguageText = ""
    private fun validateData() {

        sourceLanguageText = sourceLanguageEt.text.toString().trim()

        Log.d(TAG,  "validateData: sourceLanguageText: $sourceLanguageText")

        if (sourceLanguageText.isEmpty()) {
//            showLockTaskEscapeMessage()
        }
        else{
                startTranslation()

            }
    }

    private fun startTranslation() {
//set progress message and show
        progressDialog.setMessage("Processing language model ... ")
        progressDialog.show()

//init TronslatorOptions with source and torget languages e.g. en and ur
        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourcelanguageCode)
            .setTargetLanguage(targetLanguageCode)
            .build()
        translator = Translation.getClient(translatorOptions)

//init DownloodConditions with option to requireWifi (Optionol)
        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

//start downloading translation model if required (will download ist time)
        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
//translation model ready to be translated, Lets translate
                Log.d(TAG,  "startTranslation: model ready, start translation ... ")

                progressDialog.setMessage("Translating ... ")
                translator.translate(sourceLanguageText)
                    .addOnSuccessListener { translatedText->
                        Log.d(TAG , "startTranslation: translatedText: $translatedText")
                        progressDialog.dismiss()
                        targetLanguageTv.text = translatedText
                    }
                    .addOnFailureListener{e->
                        Log.e(TAG , "startTranslation:" , e)
                        Toast.makeText(this, "failed due to ${e.message}", Toast.LENGTH_SHORT).show()

                        // message

                    }
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Log.e(TAG , "startTranslation: ", e)
                Toast.makeText(this, "failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadAvailableLanguages(){

        languageArrayList = ArrayList()
        val languageCodeList = TranslateLanguage.getAllLanguages()

        for (languageCode in languageCodeList){
            val languageTitle = Locale(languageCode).displayLanguage

            Log.d(TAG,  "loadAvailableLanguages: LanguageCode: $languageCode")
            Log.d(TAG,  "loadAvailableLanguages: languageTitle: $languageTitle")

            val modelLanguage = ModelLanguage(languageCode, languageTitle)

            languageArrayList !!. add(modelLanguage)
        }

    }
    private  fun  sourceLanguageChoose(){
        val popupMenu = PopupMenu(this , sourceLanguageChooseBtn)

        for ( i in languageArrayList !!.indices){
            popupMenu.menu.add(Menu.NONE , i , i , languageArrayList!![i].languageTitle)
        }
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val position = menuItem.itemId
            sourcelanguageCode = languageArrayList!![position]!!.languageCode
            sourcelanguageTitle = languageArrayList!![position]!!.languageTitle
            sourceLanguageChooseBtn.text = sourcelanguageTitle
            sourceLanguageEt.hint = "enter $sourcelanguageTitle"
            Log.d(TAG , "sourceLanguageChoose : sourceLanguageCode : $sourcelanguageCode")
            Log.d(TAG , "sourceLanguageChoose : sourceLanguageTitle : $sourcelanguageTitle")





            false
        }
    }

    private  fun targetLanguageChoose(){
        val  popupMenu = PopupMenu(this , targetLanguageChooseBtn)
        for ( i in languageArrayList!!.indices){
            popupMenu.menu.add(Menu.NONE , i , i , languageArrayList!![i]!!.languageTitle)

        }
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val position = menuItem.itemId

            targetLanguageCode = languageArrayList!![position]!!.languageCode
            targetLanguageTitle = languageArrayList!![position]!!.languageTitle

            targetLanguageChooseBtn.text = targetLanguageTitle



            Log.d(TAG , "targetLanguageChoose : targetLanguageCode: $targetLanguageCode")
            Log.d(TAG , "targetLanguageChoose : targetLanguageCode: $targetLanguageTitle")




            false

        }


    }
}