package agersant.polaris.features.player

import agersant.polaris.R
import agersant.polaris.Song
import agersant.polaris.databinding.ViewDetailsBinding
import agersant.polaris.databinding.ViewDetailsItemBinding
import agersant.polaris.util.formatTime
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible

fun Context.showDetailsDialog(song: Song): AlertDialog {
    val inflater = LayoutInflater.from(this)
    val detailsBinding = ViewDetailsBinding.inflate(inflater).apply {
        scrollView.setOnScrollChangeListener { v, _, _, _, _ ->
            topDivider.isVisible = (v.canScrollVertically(-1))
            bottomDivider.isVisible = (v.canScrollVertically(1))
        }

        fun addValue(@StringRes labelRes: Int, value: String?) {
            if (value != null) {
                val itemView = ViewDetailsItemBinding.inflate(inflater, detailsItems, true)
                itemView.label.text = getString(labelRes)
                itemView.value.text = value
            }
        }

        fun addValue(@StringRes labelRes: Int, value: Int) {
            if (value != -1) {
                val itemView = ViewDetailsItemBinding.inflate(inflater, detailsItems, true)
                itemView.label.text = getString(labelRes)
                itemView.value.text = value.toString()
            }
        }

        addValue(R.string.details_title, song.title)
        addValue(R.string.details_album, song.album)
        addValue(R.string.details_artist, song.artist)
        addValue(R.string.details_album_artist, song.albumArtist)
        addValue(R.string.details_composer, song.composer)
        addValue(R.string.details_lyricist, song.lyricist)
        addValue(R.string.details_genre, song.genre)
        addValue(R.string.details_release_label, song.label)
        addValue(R.string.details_year, song.year)
        addValue(R.string.details_track_number, song.trackNumber)
        addValue(R.string.details_disc_number, song.discNumber)
        if (song.duration != -1) {
            addValue(R.string.details_duration, formatTime(song.duration))
        }
    }

    val dialog = AlertDialog.Builder(this)
        .setTitle(R.string.details)
        .setView(detailsBinding.root)
        .setPositiveButton(android.R.string.ok, null)
        .create()

    Handler(mainLooper).post {
        detailsBinding.topDivider.isVisible = detailsBinding.scrollView.canScrollVertically(-1)
        detailsBinding.bottomDivider.isVisible = detailsBinding.scrollView.canScrollVertically(1)
    }

    dialog.show()

    return dialog
}
