package com.example.avatar_ai_manager.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.databinding.FragmentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val TAG = "ListFragment"

abstract class ListFragment<T, VH : RecyclerView.ViewHolder> : BaseFragment() {

    data class Options<T, LA>(
        val header1Text: String,
        val header2Text: String,
        val listAdaptor: LA,
        val getFlowList: (() -> Flow<List<T>>?)?
    )

    private var _innerBinding: FragmentListBinding? = null
    private val innerBinding get() = _innerBinding!!

    private var getFlowList: (() -> Flow<List<T>>?)? = null
    private var submitAdaptorList: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _innerBinding = FragmentListBinding.inflate(inflater, outerBinding.fragmentContainer, true)
        return outerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        innerBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    protected fun setListFragmentOptions(options: Options<T, ListAdapter<T, VH>>) {
        innerBinding.header1.text = options.header1Text
        innerBinding.header2.text = options.header2Text
        innerBinding.recyclerView.adapter = options.listAdaptor
        getFlowList = options.getFlowList
        submitAdaptorList()
    }

    override fun onDatabaseError() {
        (innerBinding.recyclerView.adapter as ListAdapter<*, *>).submitList(emptyList())
        innerBinding.progressBar.visibility = View.INVISIBLE
        innerBinding.loadingMessage.text = getString(R.string.message_network_error)
        innerBinding.loadingMessage.visibility = View.VISIBLE
        super.onDatabaseError()
    }

    override fun onDatabaseLoading() {
        (innerBinding.recyclerView.adapter as ListAdapter<*, *>).submitList(emptyList())
        innerBinding.progressBar.visibility = View.VISIBLE
        innerBinding.loadingMessage.text = getString(R.string.message_loading)
        innerBinding.loadingMessage.visibility = View.VISIBLE
        super.onDatabaseLoading()
    }

    override fun onDatabaseReady() {
        innerBinding.progressBar.visibility = View.INVISIBLE
        innerBinding.loadingMessage.visibility = View.INVISIBLE
        submitAdaptorList()
        super.onDatabaseReady()
    }

    /*
    * This will not submit a list if either the database is not ready or the
    * getFlowList function has not been set.
     */
    private fun submitAdaptorList() {
        submitAdaptorList?.cancel()
        submitAdaptorList = lifecycleScope.launch(Dispatchers.Main) {
            getFlowList?.invoke()?.collect {
                @Suppress("UNCHECKED_CAST")
                (innerBinding.recyclerView.adapter as ListAdapter<T, VH>).submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _innerBinding = null
    }
}