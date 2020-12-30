import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:get_it/get_it.dart';
import 'package:polaris/platform/api.dart';
import 'package:polaris/platform/dto.dart' as dto;
import 'package:polaris/ui/strings.dart';
import 'package:polaris/ui/utils/format.dart';
import 'package:polaris/ui/utils/thumbnail.dart';

final getIt = GetIt.instance;

class AlbumDetails extends StatefulWidget {
  final dto.Directory album;

  AlbumDetails(this.album, {Key key}) : super(key: key);

  @override
  _AlbumDetailsState createState() => _AlbumDetailsState();
}

class _AlbumDetailsState extends State<AlbumDetails> {
  List<dto.Song> _songs;

  @override
  initState() {
    super.initState();
    _fetchData();
  }

  @override
  void didUpdateWidget(AlbumDetails oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.album.path != widget.album.path) {
      _fetchData();
    }
  }

  void _fetchData() async {
    setState(() {
      _songs = null;
    });

    final api = getIt<API>();
    // TODO error handling
    final content = await api.browse(widget.album.path);
    final songs = content.where((f) => f.isSong()).map((f) => f.asSong()).toList();

    setState(() {
      _songs = songs;
    });
  }

  @override
  Widget build(BuildContext context) {
    // TODO landscape mode

    var slivers = <Widget>[];

    // App bar
    slivers.add(SliverAppBar(
      stretch: true,
      expandedHeight: 128,
      automaticallyImplyLeading: false,
      flexibleSpace: FlexibleSpaceBar(
        stretchModes: <StretchMode>[
          StretchMode.zoomBackground,
          StretchMode.fadeTitle,
        ],
        background: Thumbnail(widget.album.artwork),
      ),
    ));

    // TODO loading spinner
    // TODO handle zero songs
    // TODO animate in
    // TODO multi-disc albums

    // Header
    slivers.add(SliverList(
      delegate: SliverChildListDelegate([
        Padding(
          padding: const EdgeInsets.all(16.0),
          child: DefaultTextStyle(
              style: Theme.of(context).textTheme.headline5, child: Text(widget.album.album ?? unknownAlbum)),
        ),
        Padding(
          padding: const EdgeInsets.fromLTRB(16.0, 0, 16.0, 16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              DefaultTextStyle(
                  style: Theme.of(context).textTheme.bodyText2, child: Text(widget.album.artist ?? unknownArtist)),
              DefaultTextStyle(
                  style: Theme.of(context).textTheme.caption, child: Text(widget.album.year?.toString() ?? '')),
            ],
          ),
        ),
      ]),
    ));

    // Content
    if (_songs != null) {
      slivers.add(SliverList(
        delegate: SliverChildListDelegate(_songs.map((song) => Song(song, widget.album.artwork)).toList()),
      ));
    }

    return Scaffold(
      body: CustomScrollView(
        physics: AlwaysScrollableScrollPhysics(parent: BouncingScrollPhysics()),
        slivers: slivers,
      ),
    );
  }
}

class Song extends StatelessWidget {
  final String albumArtwork;
  final dto.Song song;

  Song(this.song, this.albumArtwork, {Key key})
      : assert(song != null),
        super(key: key);

  String getSubtitle() {
    final artist = song.formatArtist();
    List<String> components = [artist];
    if (song.duration != null) {
      components.add(formatDuration(Duration(seconds: song.duration)));
    }
    return components.join(' · ');
  }

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: ListThumbnail(albumArtwork ?? song.artwork),
      title: Text(song.formatTrackNumberAndTitle(), overflow: TextOverflow.ellipsis),
      subtitle: Text(getSubtitle(), overflow: TextOverflow.ellipsis),
      trailing: Icon(Icons.more_vert),
      dense: true,
    );
  }
}
