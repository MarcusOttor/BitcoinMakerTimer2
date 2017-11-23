package com.freebitcoinmaker.bitcoinmining.claimfreebtc.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.AppTools
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.R
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.db.History
import kotlinx.android.synthetic.main.redeem_activity.*
import kotlinx.android.synthetic.main.toolbar_back.*
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class RedeemActivity : BaseActivity() {

    companion object {
        val MIN_WITHDRAW = 1_000_000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_activity)

        bindCoinView()
        bind()

        toolbarText.text = "Redeem"

        updateRedeemTexts()
    }

    private fun updateRedeemTexts() {
        coinsRedeemText.text = coinsManager.getCoins().toString()
        btcRedeemText.text = "= ${AppTools.format(coinsManager.getCoins() / 100_000_000f)} BTC"
    }

    @OnClick(R.id.addCoinsText)
    fun back(view: View) {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    @OnClick(R.id.moreCoinsBtn)
    fun earnMore() {
        startActivity(Intent(this, OffersActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    @OnClick(R.id.redeemBtn)
    fun redeem() {
        if (AppTools.isNetworkAvaliable(this)) {
            if (coinsManager.getCoins() >= MIN_WITHDRAW) {
                if (AppTools.isWalletCorrect(emailText.text.toString())) {
                    var dismisser = dialogsManager.showProgressDialog(supportFragmentManager)
                    thread {
                        Thread.sleep(3000)
                        runOnUiThread {
                            database.historyDao().insert(History(
                                    SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()), coinsManager.getCoins()))
                            coinsManager.subtractCoins(coinsManager.getCoins())
                            updateCoins()
                            updateRedeemTexts()
                            dismisser.dismiss()
                            dialogsManager.showAlertDialog(supportFragmentManager,
                                    "You will receive your Satoshi in 3 - 7 days!", {
                                onBackPressed()
                            })
                        }
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Wallet is not valid!", {
                        interstitial?.show()
                    })
                }
            } else {
                dialogsManager.showAlertDialog(supportFragmentManager,
                        "Not enough Satoshi!", {
                    interstitial?.show()
                })
            }
        } else {
            dialogsManager.showAlertDialog(supportFragmentManager,
                    "No internet connection!", {
                interstitial?.show()
            })
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
}
