package ru.tatuna.mycurrency.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.tatuna.mycurrency.BuildConfig
import ru.tatuna.mycurrency.R
import ru.tatuna.mycurrency.service.CurrencyService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, CurrencyListFragment())
            .addToBackStack("ListFragment")
            .commit()
    }
}