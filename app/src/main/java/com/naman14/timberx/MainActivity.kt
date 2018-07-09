package com.naman14.timberx

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import com.naman14.timberx.ui.main.MainFragment
import kotlinx.android.synthetic.main.layout_bottomsheet_controls.*
import kotlinx.android.synthetic.main.main_activity.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.naman14.timberx.databinding.MainActivityBinding
import com.naman14.timberx.util.getService

class MainActivity : BaseActivity() {

    lateinit var viewModel: MainViewModel

    var binding: MainActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }

        val layoutParams = progressBar.layoutParams as RelativeLayout.LayoutParams
        progressBar.measure(0, 0)
        layoutParams.setMargins(0, -(progressBar.measuredHeight / 2), 0, 0)
        progressBar.layoutParams = layoutParams

        binding?.let {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }

        val mUpdateProgress = object : Runnable {
            override fun run() {
                val playing = getService()?.isPlaying ?: false
                if (playing) {
                    val position = getService()?.position()
                    viewModel.progressLiveData.postValue(position)
                    progressBar.postDelayed(this, 10)

                }
            }
        }

        viewModel.addObservers().observe(this, Observer {
            progressBar.postDelayed(mUpdateProgress, 10)
        })

    }

}