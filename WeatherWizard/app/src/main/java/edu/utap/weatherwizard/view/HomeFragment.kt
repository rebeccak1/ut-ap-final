package edu.utap.weatherwizard.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.utap.weatherwizard.MainActivity
import edu.utap.weatherwizard.MainViewModel
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.databinding.FragmentHomeBinding

class HomeFragment :
    Fragment(R.layout.fragment_home) {
    private val viewModel: MainViewModel by activityViewModels()

    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder#getBindingAdapterPosition()
    // Getting the position of the selected item is unfortunately complicated
    // This always returns a valid index.
    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    // Touch helpers provide functionality like detecting swipes or moving
    // entries in a recycler view.  Here we do swipe left to delete
    private fun initTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START)
            {
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    val position = getPos(viewHolder)
                    Log.d(javaClass.simpleName, "Swipe delete $position")
                    viewModel.removePhotoAt(position)
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
    }
    // No need for onCreateView because we passed R.layout to Fragment constructor
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")
        val mainActivity = (requireActivity() as MainActivity)

        // Long press to edit.
//        val adapter = PhotoMetaAdapter(viewModel)
//
//        val rv = binding.photosRV
//        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
//        rv.addItemDecoration(itemDecor)
//        rv.adapter = adapter
//        rv.layoutManager = LinearLayoutManager(rv.context)
//        // Swipe left to delete
//        initTouchHelper().attachToRecyclerView(rv)
//
//        // XXX Write me, onclick listeners and observers
//        viewModel.observeCityMeta().observe(viewLifecycleOwner) {
//            Log.d("XXX", "observing photometa")
//            adapter.submitList(it)
//        }
    }
}