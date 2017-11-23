package com.freebitcoinmaker.bitcoinmining.claimfreebtc.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.AppTools
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.R
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.core.analytics.Analytics
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.core.managers.PreferencesManager
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindCoinView()
        bind()

        toolbarText.text = "Bitcoin Maker"
        
        if (intent.getBooleanExtra("notification", false)) {
            coinsManager.addCoins(100)
            updateCoins()
            Analytics.report(Analytics.NOTIFICATION)
            dialogsManager.showAlertDialog(supportFragmentManager,
                    "You got 100 Satoshi!", {
                interstitial?.show()
            })
        }

        checkBonusCoins()
    }

    @OnClick(R.id.addCoinsText)
    fun back() {
        startActivity(Intent(this, OffersActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    @OnClick(R.id.claim, R.id.offers, R.id.logOut, R.id.share, R.id.rateUs, R.id.ticketcsGame, R.id.history)
    fun controlMain(view: View) {
        when (view.id) {
            R.id.claim -> {
                startActivity(Intent(this, ClaimActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
            R.id.offers -> {
                startActivity(Intent(this, OffersActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
            R.id.share -> {
                var mess = "I'am using this app to get free Bitcoin: \"https://play.google.com/store/apps/details?id=" +
                        packageName + "\"" + " Here is my invite code: " +
                        preferencesManager.get(PreferencesManager.INVITE_CODE, "") +
                        " Install an app and enter this code to get 1000 Satoshi!"
                try {
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, mess), "Share"))
                } catch (ex: Exception) {}
            }
            R.id.rateUs -> {
                dialogsManager.showAlertDialog(supportFragmentManager, "Ple".plus("ase, ").plus("rat").plus("e us")
                        .plus(" 5").plus(" sta").plus("rs!"), {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
                    } catch (ex: Exception) {}
                })
            }
            R.id.logOut -> {
                dialogsManager.showAdvAlertDialog(supportFragmentManager, "Are you sure?", {
                    preferencesManager.deleteAll()
                    coinsManager.deleteall()
                    startActivity(Intent(this, StartActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }, {})
            }
            R.id.ticketcsGame -> {
                if (AppTools.isNetworkAvaliable(this)) {
                    startActivity(Intent(this, GameActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No internet connection!", {
                        interstitial?.show()
                    })
                }
            }
            R.id.history -> {
                dialogsManager.showHistoryDialog(supportFragmentManager)
            }
        }
    }

    override fun onBackPressed() {
        dialogsManager.showAdvAlertDialog(supportFragmentManager, "Do you really want to exit?", {
            finish()
        }, {
            interstitial?.show()
        })
    }

    private fun checkBonusCoins() {
        if (preferencesManager.get(PreferencesManager.LAST_CHECKED, 0L) <= System.currentTimeMillis()) {
            preferencesManager.put(PreferencesManager.LAST_CHECKED, (System.currentTimeMillis() + (60 * 60 * 1000)))
            retrofitManager.invitecoins(preferencesManager.get(PreferencesManager.USERNAME, ""),
                    preferencesManager.get(PreferencesManager.PASSWORD, ""), { coins ->
                if (coins > 0) {
                    coinsManager.addCoins(coins)
                    updateCoins()
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Someone entered your invite code! You got $coins Satoshi!", {})
                }
            }, {})
        }
    }
}
