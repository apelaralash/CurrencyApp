package ru.tatuna.mycurrency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.tatuna.mycurrency.databinding.CurrencyListItemBinding
import ru.tatuna.mycurrency.pojo.CurrencyItem

class CurrencyListAdapter(private val listener: ItemClickListener) : RecyclerView.Adapter<CurrencyListAdapter.CurrencyViewHolder>(), View.OnClickListener {

    private val currencyList: MutableList<CurrencyItem> = mutableListOf()

    class CurrencyViewHolder(private val binding: CurrencyListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyItem) {
            binding.currencyName.text = item.name
            binding.currencyValue.text = item.value.toString() + " $"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = CurrencyListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(this)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.itemView.tag = currencyList[position]
        holder.bind(currencyList[position])
    }

    override fun getItemCount(): Int = currencyList.size

    fun setItems(list: List<CurrencyItem>) {
        currencyList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        val item = p0?.tag
        if (item is CurrencyItem) listener.onItemClick(item.name, item.value)
    }

}