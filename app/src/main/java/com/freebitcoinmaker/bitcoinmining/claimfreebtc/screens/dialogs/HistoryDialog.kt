package com.freebitcoinmaker.bitcoinmining.claimfreebtc.screens.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.AppTools
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.R
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.core.MyApplication
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.db.History
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.db.HistoryDatabase
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.inject.AppModule
import com.freebitcoinmaker.bitcoinmining.claimfreebtc.inject.DaggerAppComponent
import kotlinx.android.synthetic.main.history_item.view.*
import javax.inject.Inject

class HistoryDialog : DialogFragment() {

    @Inject lateinit var db: HistoryDatabase

    @BindView(R.id.recycler)
    lateinit var recycler: RecyclerView
    @BindView(R.id.noHistoryText)
    lateinit var noHistoryText: TextView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var view = inflater?.inflate(R.layout.history_dialog, container, false)

        DaggerAppComponent.builder()
                .appModule(AppModule(activity))
                .mainModule((activity.application as MyApplication).mainModule)
                .build().inject(this)

        ButterKnife.bind(this, view !!)

        initializeList()

        return view
    }

    private fun initializeList() {
        recycler.setHasFixedSize(true)
        recycler.layoutManager =
                LinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL, false)
        val history = db.historyDao().getAll()
        recycler.adapter = HistoryPayAdapter(history)
        if (history.isEmpty()) {
            recycler.visibility = View.GONE
            noHistoryText.visibility = View.VISIBLE
        } else {
            noHistoryText.visibility = View.GONE
            recycler.adapter.notifyDataSetChanged()
        }

    }

    @OnClick(R.id.close)
    fun okClick() {
        dismiss()
    }

    inner class HistoryPayAdapter(private val data: List<History>) : RecyclerView.Adapter<HistoryPayAdapter.HistoryPayViewHolder>() {
        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: HistoryPayViewHolder?, position: Int) {
            holder?.itemView?.apply {
                paymentNumber.text = "${position + 1}"
                paymentAmount.text = "${AppTools.format(data[position].amount / 100_000_000f)} BTC"
                paymentDate.text = data[position].date
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
                HistoryPayViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.history_item, parent, false))

        inner class HistoryPayViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
    }
}
