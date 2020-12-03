package agersant.polaris

import agersant.polaris.PlaybackQueue.Ordering
import java.io.Serializable

class PlaybackQueueState(
    var queueItems: List<CollectionItem> = listOf(),
    var queueIndex: Int = -1,
    var queueOrdering: Ordering = Ordering.Sequence,
    var trackProgress: Float = 0f,
) : Serializable {

    companion object {
        const val VERSION = 4
    }
}