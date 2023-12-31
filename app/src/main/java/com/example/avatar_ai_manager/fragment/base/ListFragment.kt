package com.example.avatar_ai_manager.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.databinding.FragmentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val TAG = "ListFragment"

abstract class ListFragment<T> : BaseFragment() {

    data class ListOptions<T>(
        val header1Text: String,
        val header2Text: String,
        val listAdaptor: ClickableListAdaptor<T>,
        val getFlowList: (() -> Flow<List<T>>?)?
    )

    private var _innerBinding: FragmentListBinding? = null
    private val innerBinding get() = _innerBinding!!

    private var getFlowList: (() -> Flow<List<T>>?)? = null
    private var delayAddDatabaseObserver: (() -> Unit)? = null
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

    /*
    * The addDatabaseObserver() function is usually called in the
    * onViewCreated() of BaseFragment, but here we delay it until
    * after getFlowList has been set in setListFragmentOptions().
     */
    override fun addDatabaseObserver() {
        if (getFlowList != null) {
            super.addDatabaseObserver()
        } else {
            delayAddDatabaseObserver = { super.addDatabaseObserver() }
        }
    }

    protected fun setListFragmentOptions(options: ListOptions<T>) {
        innerBinding.header1.text = options.header1Text
        innerBinding.header2.text = options.header2Text
        innerBinding.recyclerView.adapter = options.listAdaptor
        getFlowList = options.getFlowList

        // Call and then reset addDatabaseObserver if it has been queued.
        delayAddDatabaseObserver?.invoke()
        delayAddDatabaseObserver = null
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

    override fun onDatabaseError() {
        (innerBinding.recyclerView.adapter as ListAdapter<*, *>).submitList(emptyList())
        innerBinding.progressBar.visibility = View.INVISIBLE
        innerBinding.loadingMessage.text = getString(R.string.message_network_error)
        innerBinding.loadingMessage.visibility = View.VISIBLE
        super.onDatabaseError()
    }

    /*
    * This will not submit a list if either the database is not ready or the
    * getFlowList function has not been set.
    *
    * Note that _innerBinding could be null if a Flow collection happens while
    * the Fragment is in the navigation backstack.
     */
    private fun submitAdaptorList() {
        submitAdaptorList?.cancel()
        submitAdaptorList = lifecycleScope.launch(Dispatchers.Main) {
            getFlowList?.invoke()?.collect {
                // Check here that _innerBinding != null
                _innerBinding?.let { innerBinding ->
                    @Suppress("UNCHECKED_CAST")
                    (innerBinding.recyclerView.adapter as ClickableListAdaptor<T>).submitList(it)
                    scrollToLastPosition()
                }
            }
        }
    }

    private fun scrollToLastPosition() {
        uiStateViewModel.lastScrollPosition?.let { position ->
            (innerBinding.recyclerView.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(position, 0)
            uiStateViewModel.lastScrollPosition = null
        }
    }

    protected fun saveScrollPositionAndNavigate(directions: NavDirections) {
        uiStateViewModel.lastScrollPosition =
            (innerBinding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _innerBinding = null
    }
}