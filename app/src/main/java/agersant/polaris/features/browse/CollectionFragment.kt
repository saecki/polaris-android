package agersant.polaris.features.browse

import agersant.polaris.PolarisApp
import agersant.polaris.R
import agersant.polaris.api.API
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController

class CollectionFragment : Fragment() {
    private lateinit var api: API

    @ExperimentalFoundationApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)

        val state = PolarisApp.state
        api = state.api

        return ComposeView(requireContext()).apply {
            val controller = this::findNavController
            setContent {
                MaterialTheme(
                    colors = if (PolarisApp.instance.isDarkMode) darkColors() else lightColors()
                ) {
                    Collection(navController = controller, online = !api.isOffline)
                }
            }
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Collection(navController: () -> NavController, online: Boolean = true) {
    LazyVerticalGrid(
        cells = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(4.dp),
    ) {
        item {
            Section(
                name = stringResource(R.string.collection_browse),
                icon = Icons.Default.Folder,
            ) {
                val args = Bundle()
                args.putSerializable(BrowseFragment.NAVIGATION_MODE, BrowseFragment.NavigationMode.PATH)
                navController().navigate(R.id.nav_browse, args)
            }
        }
        item {
            Section(
                name = stringResource(R.string.collection_random),
                icon = Icons.Default.Shuffle,
                enabled = online,
            ) {
                val args = Bundle()
                args.putSerializable(BrowseFragment.NAVIGATION_MODE, BrowseFragment.NavigationMode.RANDOM)
                navController().navigate(R.id.nav_browse, args)
            }
        }
        item {
            Section(
                name = stringResource(R.string.collection_recent),
                icon = Icons.Default.NewReleases,
                enabled = online,
            ) {
                val args = Bundle()
                args.putSerializable(BrowseFragment.NAVIGATION_MODE, BrowseFragment.NavigationMode.RECENT)
                navController().navigate(R.id.nav_browse, args)
            }
        }
    }
}

@Composable
private fun Section(name: String, icon: ImageVector, enabled: Boolean = true, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(size = 8.dp),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = name,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
