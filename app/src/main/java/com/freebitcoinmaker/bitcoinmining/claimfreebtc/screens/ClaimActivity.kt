package com.freebitcoinmaker.bitcoinmining.claimfreebtc.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import butterknife.OnClick
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.R
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.core.managers.PreferencesManager
import kotlinx.android.synthetic.main.activity_claim.*
import kotlinx.android.synthetic.main.toolbar_back.*

class ClaimActivity: BaseActivity(), Runnable {

    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claim)

        bindCoinView()
        bind()
        handler.post(this)

        toolbarText.text = "Claim"
    }

    @OnClick(R.id.addCoinsText)
    fun back() {
        onBackPressed()
    }

    override fun onBackPressed() {
        startActivity(Intent(this,
                MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    @OnClick(R.id.claim)
    fun claim(view: View) {
        when (view.id) {
            R.id.claim -> {
                if (preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) == 30) {
                    if (preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0) == 0) {
                        coinsManager.addCoins(500)
                        updateCoins()
                        preferencesManager.put(PreferencesManager.CLAIM_MINUTES, 0)
                        preferencesManager.put(PreferencesManager.CLAIM_SECONDS, 0)
                        dialogsManager.showAlertDialog(supportFragmentManager,
                                "Congratulations! You've been claimed 500 Satoshi!", {
                            startService()
                            interstitial?.show()
                        })
                    } else {
                        dialogsManager.showAlertDialog(supportFragmentManager,
                                "Not available now!", {
                        })
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Not available now!", {
                    })
                }
            }
        }
    }

    override fun run() {
        progressView.progress = preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0)
        progressText.text = getTimeString("${preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0)}",
                "${preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0)}")
        if (preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) == 30) {
            if (preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0) == 0) {
                progressText.text = "Ready"
            }
        }
        handler.postDelayed(this, 1000)
    }

    private fun getTimeString(mins: String, secs: String): String =
            if (mins.length == 1) { "0" + mins } else { mins } + ":" + if (secs.length == 1) { "0" + secs } else { secs }
}
