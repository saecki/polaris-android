package agersant.polaris.features.queue

import agersant.polaris.*
import agersant.polaris.PlaybackQueue.Ordering
import agersant.polaris.databinding.FragmentQueueBinding
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class QueueFragment : Fragment() {
    private val model: QueueViewModel by viewModels()

    private lateinit var adapter: QueueAdapter
    private lateinit var binding: FragmentQueueBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)

        binding = FragmentQueueBinding.inflate(inflater)

        binding.recyclerView.setHasFixedSize(true)
        adapter = QueueAdapter(model.state, model.items.value!!)
        binding.recyclerView.adapter = adapter
        val callback: ItemTouchHelper.Callback = QueueTouchCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        val animator: DefaultItemAnimator = object : DefaultItemAnimator() {
            override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
                holder.itemView.alpha = 0f
                return false
            }

            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }
        binding.recyclerView.itemAnimator = animator

        model.playingTrack.observe(viewLifecycleOwner) { playingTrack ->
            adapter.notifyItemChanged(model.lastPlayingTrack)
            adapter.notifyItemChanged(playingTrack)
            model.lastPlayingTrack = playingTrack
        }
        model.items.observe(viewLifecycleOwner) { items ->
            binding.tutorial.visibility = when {
                items.isEmpty() -> View.VISIBLE
                else -> View.GONE
            }

            adapter.updateItems(items)
        }
        model.itemsState.observe(viewLifecycleOwner) {
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
        }
        model.ordering.observe(viewLifecycleOwner) {
            updateOrderingIcon(it)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.queue, menu)
        updateOrderingIcon(model.ordering.value!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_clear) {
            model.clear()
            return true
        } else if (itemId == R.id.action_shuffle) {
            model.shuffle()
            return true
        } else if (itemId == R.id.action_ordering_sequence || itemId == R.id.action_ordering_repeat_one || itemId == R.id.action_ordering_repeat_all) {
            setOrdering(item)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setOrdering(item: MenuItem) {
        when (item.itemId) {
            R.id.action_ordering_sequence -> model.setOrdering(Ordering.Sequence)
            R.id.action_ordering_repeat_one -> model.setOrdering(Ordering.RepeatOne)
            R.id.action_ordering_repeat_all -> model.setOrdering(Ordering.RepeatAll)
        }
    }

    private fun getIconForOrdering(ordering: Ordering): Int {
        return when (ordering) {
            Ordering.RepeatOne -> R.drawable.baseline_repeat_one_24
            Ordering.RepeatAll -> R.drawable.baseline_repeat_24
            Ordering.Sequence -> R.drawable.baseline_reorder_24
        }
    }

    private fun updateOrderingIcon(ordering: Ordering) {
        val icon = getIconForOrdering(ordering)
        val orderingItem = App.toolbar.menu.findItem(R.id.action_ordering)
        orderingItem?.setIcon(icon)
    }
}