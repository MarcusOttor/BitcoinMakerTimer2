package com.freebitcoinmaker.bitcoinmining.claimfreebtc.inject

import com.freebitcoinmaker.bitcoinmining.claimfreebtc.core.MyApplication
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.core.services.ClaimService
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.screens.BaseActivity
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.screens.dialogs.*
import dagger.Component

@Component(modules = arrayOf(AppModule::class, MainModule::class))
interface AppComponent {

    fun inject(screen: BaseActivity)
    fun inject(app: MyApplication)
    fun inject(dialog: LoginDialog)
    fun inject(dialog: SignupDialog)
    fun inject(dialog: PromocodeDialog)
    fun inject(service: ClaimService)
    fun inject(dialog: HistoryDialog)
}
