[33m04e5e275[m[33m ([m[1;36mHEAD[m[33m)[m temporary save extended position of album view
[33mce6a565f[m reset scroll position when manually reloading random page
[33m0898e5ea[m replace signatures requiring ArrayList with the List interface
[33m803cda4d[m save scroll position of browse view
[33mfa4e35bf[m small tweaks to the album view
[33me43d66b9[m add clear queue confirmation dialog
[33m911e168b[m add queue all button to album view
[33m9b5987de[m move error message in browse fragment in front of recyclerview
[33mb1c919b1[m use viewmodel to cache data while browsing collection
[33m407157c9[m add fast scroll bar to discography view
[33m99a9f80c[m sort albums in explorer display mode by year
[33mc347b24a[m replace remaining bitmap with vector icons
[33m5b6418e7[m unify explorer item
[33md652540c[m unify margin on collection and queue items
[33mcdc7dc7a[m improve album view layout when text is wrapped
[33m08ef9a9d[m add duration and simplified cache icon to queue item
[33m501d1a73[m add duration to album item
[33m03a5d943[m don't show scrollbars on small collections
[33m329ea2fc[m improve constraints in album view
[33m005cd603[m add fast scrolling bar
[33m7a035e81[m add separate landscape layout for album view
[33m9a77ea40[m design changes
[33m0bccbcec[m pick header colors of album view depending on artwork
[33mde3ca97e[m Add collapsing header layout in album view
[33mdfc4dc6b[m remove dividers of disc headers in album view
[33mab02b054[m add a corner radius to artworks in the collection view
[33mdedd0e28[m[33m ([m[1;31morigin/api[m[33m, [m[1;32mapi[m[33m)[m add api version 7 and clean up code
[33m0f204135[m convert PolarisScrobbleService to kotlin
[33m6029433a[m replace FetchAudioTask with suspend fun
[33m107433a5[m convert playbackqueue and service to kotlin
[33m08a0be52[m update remote and local api
[33m04e578fe[m return InputStream instead of ResponseBody in IRemoteAPI.getThumbnail
[33m2ee09b56[m remove unused api method getAudio(): ResponseBody
[33m125b6612[m add coroutines
[33m5833163f[m update gradle and android gradle plugin
[33m659f3364[m add missing clean task
[33ma5f03e0d[m use coroutines ktor and kotlinx serialization for fetching api versions
[33mbde129b0[m convert local api to kotlin
[33mffe7b7e2[m display additional properties in details dialog
[33m9d9100fb[m add additional fields to colleciton item
[33m8fe508e7[m support multiple thumbnail sizes
[33m903e9801[m replace deprecated ExtractorMediaSource with ProgressiveMediaSource
[33me1b02d84[m udpate offline cache for embedded artworks
[33m3aa9f426[m[33m ([m[1;31morigin/navigation[m[33m, [m[1;32mnavigation[m[33m)[m update to navigation 2.4.0 for multiple backstack support
[33mbb93efb6[m[33m ([m[1;31morigin/gradle-kotlin[m[33m, [m[1;32mgradle-kotlin[m[33m)[m add gradle check dependency updates plugin
[33m7dbd507b[m rename Dependencies to Deps
[33md866ec82[m remove deprecated and unnecessary dependency declaration
[33m0b28e720[m define dependencies in buildSrc
[33m0d4c8ab2[m migrate gradle to kotlin dsl
[33mbdb07a4e[m[33m ([m[1;31morigin/player[m[33m, [m[1;32mplayer[m[33m)[m omit non existent details properties
[33m342e19ba[m make unknown detail values more subtle
[33m22b890da[m hide duration when loading new song
[33m8e850b4d[m update deprecated exoplayer code
[33m6c0f72bd[m stop seekbar updates when player fragment is not visible
[33mf1162a12[m fix warnings
[33m06f0939e[m update dependencies
[33m35675f3b[m downgrade exoplayer core and upgrade flac extension
[33m21753346[m handle player options item selected better
[33mf0a4815c[m refactor BackdropLayout
[33m10bc1195[m remove unused SquareLayout
[33m22690417[m fix warning and clean up MainActivity
[33mc4d4cccc[m now playing ui changes
[33mbe8c2e47[m change order of elements in details dialog
[33m9b08c457[m use lazy initialized unknown string in details and player fragment
[33m0045dd9b[m update dependencies
[33m3b424242[m use details prefix for details strings
[33m6ecf14b6[m make showDetails an extension function of context
[33m870b3985[m prevent accidentally clicking backdrop menu through front layout
[33md2a5c61d[m clean up PlayerFragment
[33maec788e0[m fix player fragment controls min height
[33m1d55d793[m create top level show details dialog function
[33m402150d4[m fix artwork alignment in player fragment
[33md65130a5[m improve resizing of player fragment and refactor out backdrop menu
[33m4e6279b1[m add scroll view to the player details dialog
[33m58c76402[m add duration and year properties
[33ma3f52bab[m add details dialog to the player fragment
[33mfb3c0673[m convert player fragment to kotlin
[33mb5d15bd8[m fix progressbar mode and seekbar NaN bug
[33m38cda866[m move playback buttons below seekbar
[33m89c4c003[m redesing the player fragment
[33mb9bc62ac[m[33m ([m[1;31morigin/master[m[33m, [m[1;31morigin/HEAD[m[33m, [m[1;31magersant/master[m[33m, [m[1;32mmaster[m[33m)[m Removed noise
[33me013fd40[m Merge branch 'beta'
[33mb68f8d97[m Update CURRENT_VERSION.txt
[33m2d557c4f[m Implement Backdrop (#34)
[33m86d8f2bb[m Select discography display mode if at least one item has an artwork (#35)
[33mc66ebbf0[m Gitignore (#33)
[33m33119619[m  replaced activity navigation with jetpack fragment navigation (#32)
[33m04c09676[m Updated release notes, versionCode and versionName
[33me49b07f6[m Merge d3ecf6a74c8450df189ddd4601621b3ad668f809 into beta
[33md3ecf6a7[m fixed subtitle text color of toolbar (#31)
[33m5c8013b2[m Updated release notes, versionCode and versionName
[33m8afd6376[m Merge 3a6666c2bf91ae6de603a092d7051269057b9f48 into beta
[33m3a6666c2[m Fixed typo
[33m1ee2a093[m Merge branch 'master' of https://github.com/agersant/polaris-android
[33m52203369[m Changelog
[33m5b01a5c0[m Dark mode (#29)
[33m7eb6fac6[m Updated release notes, versionCode and versionName
[33mfe2531cc[m Merge ac64a3faed784e29b3176e1b3e3cbc357b72905f into beta
[33mac64a3fa[m Remove google play safeguards
[33m7d472c5f[m Syntax fix
[33m40774f16[m git config
[33mad5828b6[m Removed unused lines
[33m33c284b3[m Updated release notes, versionCode and versionName
[33mb37ee381[m Merge 8cdf44019e9e6d51ec4a15bbc2af256cae5f67ef into beta
[33m8cdf4401[m Manual git tagging
[33m32f0f55a[m Updated release notes, versionCode and versionName
[33m88f3b04a[m Merge 18bf026249462c333806d4c83884c76cde1b1c94 into beta
[33m18bf0262[m Updated tag name
[33mb0ce867d[m Merge 2c0e4157074f92e1e9b478a7a155580824997fbf into beta
[33m2c0e4157[m Tagging update
[33mc8e4e0c2[m Updated release notes, versionCode and versionName
[33m3b088f84[m Merge 95dd9b33fd8cced9ed930bcafe4c0845b3770ad1 into beta
[33m95dd9b33[m Google play safeguard
[33mc236a9bc[m Fixed master branch receiving production tags
[33m6018538d[m Updated release notes and versionCode
[33m294498f3[m Merge f18d81fa30e6c2d2e51264cb097f4100d36dbe72 into beta
[33mf18d81fa[m Removed google play safeguards
[33m29882679[m Updated release notes and versionCode
[33m0eee3a56[m Merge 46f98ccc8bac445506e588dba1a63d7a28b03326 into beta
[33m46f98ccc[m Trying to add quotes until they stick
[33m32582bd3[m Merge ad0af35068d22e91ae63d86b53c4f0bfc570cea3 into beta
[33mad0af350[m Removed redundant backslahes
[33m083a739d[m Merge 3e8c559218a762367bec98e486c35302fc5d4294 into beta
[33m3e8c5592[m Add quotes around version name
[33m956c6eac[m Merge 03f5b12bdd56e3c4b1d19062d6b4aabf25d2d24a into beta
[33m03f5b12b[m Simpler build.gradle pattern matching
[33m1c9a8c99[m Cosmetic changes
[33m23dd2bd5[m Updated release notes and versionCode
[33md230b50b[m Cosmetic change
[33me9fde639[m Merge 86eb314cd0b1b41a1b6a1719389fa1c8cd823adc into beta
[33m86eb314c[m Promote to production flow
[33m4b317a83[m Updated release notes and versionCode
[33mdc4cee47[m Merge 890bebb45fc18168c613105463fc34906402064c into beta
[33m890bebb4[m Cosmetic change
[33mf8c06e93[m Updated release notes and versionCode
[33m5e3ce569[m Support deploying any branch to beta
[33mbcb730f5[m Merge master into beta
[33m7bf9137f[m Tentative fix for CI not pushing to beta
[33me237f45e[m Updated maintenance guide for beta release
[33m0802eef8[m Merge branch 'master' into beta
[33m5841a8f2[m Fixed yaml syntax
[33ma06a703c[m Manual trigger of beta builds
[33md3206ab2[m remove version name from master
[33m2208c6fe[m Release tagging
[33m0a019554[m Updated release notes and versionCode
[33m9f81371a[m Merge branch 'master' into beta
[33mc80283ff[m Indent build.gradle with tabs
[33mf560a36c[m Avoid sabotaging "printVersionCode" in gradle file
[33m06616408[m Updated release notes and versionCode
[33m69d2f42a[m Merge branch 'master' into beta
[33m25b3f196[m Removed accidental build artefact
[33md5082681[m Commit gradle file changes
[33m15ea4506[m Fixed incorrect version code handling
[33mbd71ee44[m Updated release notes and versionCode
[33m51d5ef95[m Merge branch 'master' into beta
[33mc5178ea3[m Fixed Fastfile syntax
[33m7bdbeaf6[m Merge branch 'master' into beta
[33m7f89b3f4[m Restore version names in master
[33m1bae12f9[m Seed version code
[33m63643b0c[m Branch-based beta release flow
[33m5bd96420[m Leave Google Play alone while working on CI upgrades
[33ma5d31f50[m No version codes or names in master
[33m15016d25[m Updated release notes
[33md9775f78[m Update deprecated set-env
[33m2cbe4048[m Updated release notes
[33mbefcde97[m Base image bump
[33mbd7cd7bc[m Fixed ruby version
[33m7ef54376[m Fixed yaml syntax
[33me372b077[m Tentative fix for fastlane install
[33m4d89ae27[m Release 0.8.6 RC1
[33m1bc0a1ee[m Merge branch 'master' of https://github.com/agersant/polaris-android
[33m4d1ec6df[m Android trash
[33mbbc3d4bc[m Fixed a bug where Android client could send malformed cookie headers
[33mb0b5fe9f[m Merge pull request #27 from Poussinou/patch-1
[33me6ebc5a3[m Update README.md
[33me5898051[m Updated release notes
[33m0cb6dcf3[m[33m ([m[1;33mtag: v0.8.5[m[33m, [m[1;33mtag: production-release-candidate[m[33m, [m[1;33mtag: production[m[33m, [m[1;33mtag: beta-release-candidate[m[33m, [m[1;33mtag: beta[m[33m)[m Tentative fix for release deploy
[33m682c9542[m Updated release notes
[33m9aa0746e[m Added version code
[33m9186c40c[m Updated release notes
[33mda6944e7[m Fixed promote script
[33md9ca3f69[m Version notes
[33m315138ff[m Merge branch 'master' of https://github.com/agersant/polaris-android
[33ma3570e47[m Cosmetic change
[33me07b358a[m Updated maintenance guide
[33m39b1589e[m Updated release notes
[33m7ddc53dd[m Merge branch 'master' of https://github.com/agersant/polaris-android
[33m6a71e380[m Added app icon to fastlane
[33mb684d5fb[m Updated release notes
[33ma05e3733[m NDK version bump
[33m326d2bf9[m Use dedicated upload key to sign builds
[33mc130fab2[m Tentative build fix
[33m5300f955[m Project noise
[33m26836e77[m Updated dependencies
[33m287da53d[m Standard Android gitignore
[33maae5ab76[m Gradle update
[33mce093563[m Allow promotions to production
[33m0fc6870f[m Updated release notes
[33m53e242fc[m Override local tags in release scripts
[33m093519ba[m Checkout source to read version name
[33m43ace36d[m Run gradle task from directory where it is defined
[33m9f654215[m Tentative fix for missing version number
[33mdb02afbc[m Merge branch 'master' of https://github.com/agersant/polaris-android
[33m62ccda9a[m Cosmetic changes
[33m64924c26[m Updated release notes
[33mb2f53a71[m Added workflow to promote to production
[33m80f00c55[m Bumped version number
[33m4ca818bb[m Updated beta release candidate tag name
[33mf61b5994[m Added gradle command to output current vresion name
[33mfa64573c[m Updated release notes
[33m6236b044[m Dont commit release notes unless upload succeeds, fixed branch name
[33m2c099314[m Added package name
[33m8e995e9e[m Merge jobs
[33m2e630446[m Removed broken caching mechanism
[33mdcebbb3b[m Push release notes to master
[33m705559c0[m Updated docs
[33m209351c5[m Fixed options syntax
[33m59df3090[m Added github token for commit action
[33m69e70d7d[m Copy Release Notes
[33m042811f2[m Used cached fastlane for beta build
[33md7ce583e[m Use existing action to commit release notes
[33m7508b346[m Cache fastlane garbage for faster build
[33me0029783[m Added maintenance guide
[33m16bcbb42[m Checkout source before publishing
[33m6935fe95[m Fixed indentation
[33md884b863[m Named pipelines
[33m98c5602e[m Added pipeline for beta release
[33mfd789d70[m Dont automate user facing version names
[33m9394b638[m Correctly locate keystore
[33m8c7d52f2[m Release build and signing
[33m2be1032b[m Automatic version code
[33m0df84cb3[m Re-order values in grade build file
[33me23e8204[m Fixed gradle syntax error
[33m295a9eb7[m Mark gradle script as executable
[33m65ad2d16[m Moved Fastfile to expected location
[33mce35142c[m Removed unused file
[33m014c4563[m Opt-out of analytics
[33mc0dc65a1[m Use latest version of checkout action
[33m816b22ca[m Checkout polaris-android before building
[33mf64bae8f[m Build debug binary in CI, attempt #1
[33m2a55ba43[m[33m ([m[1;33mtag: v0.8.4[m[33m)[m Merge pull request #24 from agersant/fastlane
[33m1b8e6a0c[m Added changelog from latest version
[33m1829ec6a[m Added screenshots
[33m3a9a6683[m Added text metadata
[33ma87c7871[m File structure
[33m52266f4e[m Version bump
[33m3de2ed43[m Version bump
[33m63bd81cd[m Added support for API version 5
[33m6e6e0315[m Version bump
[33mde20ae45[m API version 4 support
[33mf43ea49b[m Bumped version number
[33mc0752304[m Mediasession tweaks
[33me1c34d0f[m Merge remote-tracking branch 'origin/master'
[33m8c150ab8[m Bumped version number
[33m0f85c38c[m Added adaptive icon
[33m445142c4[m Bumped version number
[33m8a02be44[m Project noise
[33mee42a856[m Add support for audio focus
[33m1da9f273[m Merge pull request #20 from Darksecond/media-session-support
[33mb1a4dee9[m Replace spaces with tab
[33m9d42e39b[m Fix issues as discussed
[33m3ecd1053[m Add MediaSession support
[33m5ebb6397[m Merge remote-tracking branch 'origin/master'
[33m9dab1b67[m Bumped version number
[33m08727f0b[m Added support for decoding FLAC files
[33m7b1c5cc9[m Updated dependencies
[33m8000280a[m Project junk
[33m25578c55[m Update README.md
[33mf481da8e[m Bumped version number
[33md89939f2[m Fixed a bug where the first API request after starting the client would fail
[33mcee96b9c[m Bumped version number
[33m63b5c424[m Merge branch 'rocket'
[33meba4e6ed[m Adds support for API level 23
[33m3602309c[m Bumped version number
[33mc1139324[m Adds backwards compatibility with API version 2 servers
[33m94168f0e[m Merge branch 'master' into rocket
[33mf9682282[m Fixed crashes when talking to server that hasn't been setup
[33m61df739d[m Bumped version number
[33mac4973c8[m Merge branch 'master' into rocket
[33m04387278[m Bumped version number
[33m84b30922[m Removed build pollution from version control
[33m7e4cf80a[m Updated dependencies
[33mf46be106[m Fixed rare crash
[33md29e50bf[m Avoid hitting a 401 on every request before submitting credentials
[33ma8e5e39e[m Tentative fix for tutorial overlap bug
[33mf0564119[m Merge branch 'master' into rocket
[33mc013e5b8[m Allow protocol redirects
[33mcf0dee86[m Gradle update things
[33m34fceb63[m Merge pull request #17 from Holi0317/master
[33ma86a1983[m Allow https in server url
[33m88ee962b[m Version bump
[33m6205618d[m Merge remote-tracking branch 'origin/rocket' into rocket
[33m09c1d0ab[m Merge branch 'master' into rocket
[33m73975bc0[m Lint
[33m80f134a4[m Correctly bump version number
[33m1c184a15[m Merge branch 'master' into rocket
[33mbe7b4b30[m Build junk
[33m8b625d1d[m Updated dependency
[33m8bd6be79[m Bumped version number
[33m9e24ea15[m Merge branch 'master' into rocket
[33m6fcd8ba3[m Tentative fix for bug where queue overlaps tutorial
[33m1f0058f6[m Tentative fix for issue where playback does not progress to next song
[33m436d73b6[m Hide buffering state when not trying to play music
[33m4e21ddb7[m Bumped version number
[33mafd1b498[m Merge branch 'master' into rocket
[33m27cc18dd[m Tentative fix for a bug where tutorial overlaps playlist
[33m716de551[m Bumped version number
[33mbc483426[m Merge branch 'master' into rocket
[33md88c348f[m Fixed a bug where buffering state would be outdated
[33mb4d22ddb[m Bumped version number
[33mbdb89240[m Merge branch 'master' into rocket
[33m33b6c7e2[m Fixed a bug where current song was not restored after cold boot
[33m50ae16ae[m Bumped version number
[33m38a5361c[m URI encode paths for last.fm scrobbling
[33ma7eccae1[m Merge branch 'master' into rocket
[33m74a3dbde[m Added support for last.fm scrobbling
[33m850f156b[m Missing file from previous commit
[33mb9f414d4[m Use string constant for notification channel description
[33mbaf56654[m Disable notification sounds on Android O (tentative)
[33m83ea193c[m Bumped version number
[33m37fd1c80[m Fixed a semi-rare crash when manipulating playback queue
[33mabf54b31[m Bumped version number
[33m48a1eecd[m Fixed potential issue where queue would not update
[33m74a1f157[m Tentative fix for a bug where the queue tutorial was overlapping the queue
[33m0fd84812[m Updated dependency
[33m47aafc6e[m Bumped version number
[33m0968954e[m Addressed all linter warnings
[33m5fa91e2a[m Adds support for API level 25
[33m234add94[m Bumped version number
[33m8f59c0e7[m Moved audio opening IO away from main thread
[33m959af68c[m Moved main thread I/O to async thread
[33m6f737fa3[m Fixed warning about potential leaks
[33m328b46ef[m Fixed a bug where file handles were not being closed
[33m5d853f6e[m Fixed a bug where download completion wasn't reported in UI
[33me7e9381b[m Removed boilerplate
[33m28e04e6a[m Migrate away from deprecated API
[33m0deaf6d4[m Save state every 5 seconds
[33mbd09a087[m Fixed issues around service restart
[33mf2ab8001[m Separate service for queue downloads
[33m259f5a1c[m Service rename
[33m6d626bd8[m Removed service binding
[33md1e2c741[m Resurrect service whenever audio plays
[33m0a585cd6[m Allow HTTP traffic
[33m10500bb0[m Removed unused imports
[33mabceb811[m Autocleanup
[33m8545572a[m Got things working again
[33m8d6c9040[m Updated all the things. Broke service
[33me6d877f2[m More migration away from deprecated API
[33m4b0c79f7[m Migrated away from deprecated constants
[33m5346751c[m Migrate away from deprecated API
[33md0f68227[m Tooling and dependencies updates
[33m48af95d1[m Added notification channel
[33ma46a9816[m URI encode VFS path when hitting endpoints
[33m5280a437[m Merge remote-tracking branch 'origin/master'
[33m5ee212bc[m Updated toolchain and dependencies
[33m21094c82[m Added link to beta
[33m0df3e71b[m Bumped version number
[33mc260d08f[m Fixed a bug where swipe/drag was not reliable in playback queue
[33m7c011250[m Bumped version number
[33m287f6121[m Fixed offline mode bugs
[33md6ad39a0[m Bumped version number
[33m82a43252[m Fixed a rare bug where app would lose its service
[33m41f0c1eb[m Fixed rare crash
[33m30a38e56[m Bumped version number
[33m1b4c9a24[m Fixed a crash when playing songs from an album without artwork
[33m4c655fc0[m Bumped version number
[33mce77dd3b[m Removed fade out when clearing playlist
[33m31c45538[m Bumped offline cache version number due to collection item changes
[33me26d7c42[m Class renames for clarity
[33m85510a43[m Added disc numbers to album content view
[33m56019000[m Fixed a bug where tracks in multi-discs album weren't sorted by disc
[33mefb537fa[m Merge remote-tracking branch 'origin/master'
[33m1605464a[m Fixed a bug where items weren't sorted correctly in browse screens
[33m13dc1a6f[m Create README.md
[33m47dad5df[m Bumped version number
[33mda3908ed[m Fixed a bug where current track progress would temporarily look wrong
[33m68ae79f9[m Added album art to notifications
[33m63307845[m Bumped version number
[33m5b06c4cb[m Tentative fix for rare display bug while reordering items
[33mc3685947[m CNR issue
[33m28db1735[m Improved responsiveness of queue activity
[33m9dcdd531[m Added TODO
[33m0f925ddd[m Moved background file download to the thread executor
[33m06e87420[m Fixed error when opening queue activity
[33m0ca5a0b3[m Fixed an issue where queue items were being re-created needlessly
[33m69f588bf[m Fetch images in parallel
[33m1276e41b[m Removed unecessary import
[33m09ad3811[m Fixed offline cache assertion on new installs
[33m39525273[m Bumped version number
[33m5e35182a[m Fixed intermitent crash
[33m518823ec[m Bumped version number
[33mb1142bfc[m Improved responsiveness of the collection screens
[33m00861c7c[m Fixed a 1px glitch on the left of album art while swiping
[33md6a37bcb[m Fixed 1px glitches at the bottom of items while swiping
[33mc5b84873[m Added indicator in player activity when playback is buffering
[33ma1133e90[m Fixed a bug where activities didn't have a white background
[33m359ece94[m Fixed errors when receiving unexpected replies from server
[33md4ba1ac6[m Updated debug artwork
[33md8e59c23[m Bumped version number
[33m8abbe8ee[m Fixed more linter warnings
[33meb913d0c[m Addressed more linter warnings
[33m43b03a57[m Fixed visual glitch after queuing item
[33m2de5fc76[m Fixed linter warnings
[33m47eaab65[m Fixed a few linter warnings
[33mffe9f9f6[m Fixed state tracking issues when the queue finishes playing
[33mbde0290c[m Fixed issue where service would stop preemptively
[33m9a62a3e4[m Slight improvements to consistency of service lifetime
[33m3423642c[m Removed unused code
[33m213260e3[m Made http:// optional in server address
[33m7a5db6be[m Bumped version number
[33m3c432fb8[m Fixed crashes when using blank connection settings
[33m53dd8cdd[m Fixed more state saving inconsistencies
[33m327c4cb7[m Bumped version number
[33mb7058dba[m Tentative fix for a bug where saved/restore did not work
[33m780e5349[m Clarified log messages
[33mf3aa8ad0[m Removed unused class
[33mb3f59942[m Bumped version number
[33m557ad6d2[m Fixed a bug where browse activity would reload items when restored
[33m23fe2e70[m Bumped version number
[33m1f47a321[m Fixed a bug where playback didn't resume from correct location after restart
[33m8827728e[m Reimplemented download queue
[33mb16d30d1[m Fixed a bug where playlist didn't display which items are downloading
[33m8f68e07b[m Replaced mediaplayer with exoplayer
[33m33eec572[m Never give up on current track
[33m19d184e1[m Fixed a bug where current track wasn't highlighted in playlist
[33m688ed69f[m Fixed media playback errors
[33m5e828200[m Bumped version number
[33m11608b47[m Fixed a bug where offline cache did not work
[33m3780f2df[m Missing file from previous commit
[33mcf37dbd8[m Migrated core singletons to a god-service
[33m12b0a3b1[m Bumped version number
[33m10fd3228[m FIxed a bug where notification came back after closing the app
[33m95e55e26[m Tentative fix #2 for freezes on startup
[33m62a473e6[m Cleanup
[33m933dc954[m Bumped version number
[33m566db504[m Notification can now be tapped to access music player activity
[33mf23610c4[m Fixed a bug where notification never went away
[33mb67ceb59[m Fixed a bug where playback didn't start when pressing play
[33m6ab95c90[m Fixed a bug where playback would resume when launching the app
[33mfa8651e1[m Gradle update
[33mcf9b78a2[m Bumped version number
[33m457fc267[m Added TODO
[33m12615f7d[m Bumped version number
[33m995622c3[m Bumped version number
[33me7e8e1e5[m Missing file from previous commit
[33macae45a8[m Tentative fix for long hang upon opening the app
[33m66a726ef[m Added notification icon
[33md8dab58a[m Added strings for notification area
[33ma895e135[m Added system notifications to control playback
[33m460fb7a9[m Enabled strict mode for dev
[33m2add5421[m Added tutorial when playback queue is empty
[33m73aa7b97[m Bumped version number
[33m58e3e1ab[m Implemented download retries when streaming fails
[33m21ce301f[m Fixed possible null pointer exception under bad connectivity scenario
[33ma7ee1ffb[m Better UX when failing to fetch collection content
[33m0ef13694[m Disabled minification to get usable crash callstacks from live
[33mee032e61[m Restore current track progress on app start
[33m6eb3fe9a[m Fixed a crash when queuing a track after fresh install
[33mbd81162c[m Fixed a bug where existing cache items were no longer readable
[33m439d1f86[m Fixed a bug were items weren't sorted in collection
[33meafb1130[m Fixed a bug where items weren't sorted in collection
[33me69ad252[m Fixed a bug where unplayable items would queue when flattening in offline mode
[33m0eb376bd[m Gradle update
[33md7455051[m Bumped version number
[33m77f828bd[m Deserialization change to accomodate 2.0 API change
[33m7e0382a6[m Bumped version number
[33me7969918[m Added "recently added albums" feature
[33m16894d43[m Bumped version number
[33m7656059a[m Fixed network errors when using incorrect credentials
[33m57fbf35f[m Added icon for playlist entries currently being downloaded
[33m0d124ddf[m Fixed a bug where small cache overflows would make us waste a download
[33mfa019310[m Autoformat
[33m4bfb928f[m Fixed playback error when rapidly changing active track in the playlist
[33mbc3fa976[m Hide value of the password fields in the preferences screen
[33m98e0c220[m Fixed a bug where bottom navigation bar looked crop in landscape mode
[33m416d0b3a[m Updated dependencies
[33m6803566a[m Bumped version number
[33m4e6614cd[m Removed build spam from okhttp
[33m0a832817[m Replaced Volley by OkHttp + Gson
[33ma0aa13dd[m Pause playback when unplugging headset
[33m840ef125[m Build config noise
[33mb1332a2b[m Updated store media
[33m012cc188[m build system noise
[33mf72c401a[m Added store media
[33m5ff1845b[m Persist queue state when app shuts down
[33m8855d619[m Removed unused variable
[33m89f36a4d[m Removed random playback order
[33m5b7d3f77[m Update playlist UI when audio is removed from cache
[33m468abed7[m Removed redundant call
[33mf1ccd57e[m When making space in offline cache, preserve tracks in playlist
[33md5f755e9[m Cleanup
[33m3ca8dfd1[m Enforce offline cache size limit
[33medf37de5[m Added metadata about cached audio last usage
[33mfccd7fb9[m Fixed a bug where a corrupt file would prevent entire directories from displaying
[33m93db0271[m Offline cache no longer show directories that don't hold audio
[33m03aeafde[m Split OfflineCache.put into putAudio and putImage
[33mca30486d[m Gradle update
[33mb53dd967[m Cleanup
[33mc525f14f[m Gradle update
[33me9fb4424[m Added cache size option (not functional yet)
[33m64d30db5[m Moved I/O off UI thread
[33mcd771782[m Don't preload songs while offline
[33mc56c6e27[m Formatting
[33m9fb88ed4[m Fixed rare startup error
[33m51bfbaec[m Added missing null handling
[33md56b1cc0[m Avoid storing duplicate copies of images
[33m596e2a5a[m Added preference for how many songs should be preloaded
[33m90c309f4[m Download items in playlist ahead of time
[33mb109e931[m Ground work for downloading tracks ahead of playback
[33m6ce69a3b[m Allow flatten requests in offline mode
[33mdddd1fa7[m File move
[33me798a307[m Fixed a bug where a lot of artworks were missing in offline mode
[33mdbfebb0d[m Fixed a bug where seeking didn't work in offline mode
[33md642d65e[m Fixed a bug where caching internals appeared as directories
[33mbe383c32[m Added artworks to offline cache
[33mbcc8354e[m Cleanup
[33m3c44d669[m Removed unused method
[33m19c43f78[m Cleanup
[33me3e7e354[m Fixed a bug where /serve requests could be made before authenticating
[33m7049315a[m Allow seeking within streamed media
[33m12d65cf7[m Improved streaming error handling
[33m2b633fb6[m Fixed a bug where currently playing track didn't update
[33m049e8e9d[m Autoformat
[33mb2e8899c[m Disable random album list while offline
[33m6e53f381[m Removed settings menu from browse activities
[33ma22f77ad[m Added offline mode toggle
[33md7cfa0dd[m Added TODO
[33m08a5089b[m Browe and play audio from offline cache
[33m877b1501[m Cleanup
[33m9d0b5a99[m Better handling of playback completion
[33me3c0b378[m Added icon next to queue items that are cached locally
[33m36a0c041[m Autoformat
[33mcd936075[m Autoformat
[33m86c0e657[m Added cache icon
[33m344f66c1[m Cleanup
[33m25cd7744[m Moves and renames
[33m32956aa0[m Cleanup
[33md9666270[m Implemented audio offline cache
[33m271337e4[m Abstracted API over local/network, re-implemented network audio stream
[33mfca2db5a[m Removed junk
[33mc493e8cf[m Accept MediaDataSource on PolarisMediaPlayer
[33mdd16cbc1[m Autoformat
[33mf993d22b[m Handle browse failures
[33m9e34a5f3[m Cleanup
[33ma512dda1[m Renaming
[33m8c436366[m Cache album art locally
[33m94f17bc0[m Missing file from previous commit
[33me1bbcca6[m Indicate currently playing track in queue
[33mf5faf754[m Fixed a bug where album artist name wasn't used in album view
[33m34a73bcf[m Added swipe to refresh random album list
[33mc8d88724[m Autoformat
[33m8f2fe6ea[m Added landscape layout for player
[33m7a4529f1[m Added random album navigation
[33m6ba5b413[m Fixed a bug where queue ordering icon was inaccurate
[33mdf966130[m Skip to next track when playback completes
[33m7500d4e5[m Styled seek bar
[33mfedafb1c[m Fixed seekbar progress in release builds
[33m7ba15a84[m Autoformat
[33m6ac1491b[m Android Studio formatting options
[33m4cbbc3f4[m Implemented player seekbar
[33md5ec318f[m Fixed a bug where discography items didn't have a background
[33m941ae306[m Swapper palette to material design colors
[33m057e5a72[m Better handling of long album names
[33mb89c43aa[m Whitespace tweak
[33m888cfa4e[m Color tweak
[33mcf95643c[m Layout tweaks
[33m9222ddd0[m Removed redundant public access
[33mab16a82c[m Updated support libraries
[33mf7d5727c[m Removed redundant public exposure
[33maf6146a8[m Whitespace tweaks
[33m7dcd3dee[m Added white background on all screens
[33mab48cc3e[m Improved layout of playback queue
[33mab28e19c[m Format track numbers/names in album view
[33m5e2bcc7a[m Cleanup
[33m3a1bee9f[m Progress bar is now blue
[33m2b48774d[m Swipe icons are now white
[33mabaaea4f[m Adjusted color palette
[33med8af88f[m Added launcher icon
[33m6131e717[m Update task manager title bar color
[33m3596cb63[m Styled toolbar
[33m86e71cae[m Styled bottom navigation bar
[33m5cef139c[m Theming baby steps
[33m4758a940[m Fixed a bug where albums had inacurrate titles
[33m87e3fd31[m Added padding around explorer view
[33mee29936f[m Layout tweaks
[33m7aeafc74[m Class renames
[33m32113201[m Renamed enum entry
[33m7245253d[m Replaced ugly switch in BrowseAdapter by subclasses
[33m16b0d99a[m Separate album items from explorer items
[33m5df7cf86[m Mass renaming for consistency
[33me8a83506[m Added discography view
[33mea30c791[m Explorer variants are now custom views (were fragments)
[33mde491ed8[m Refactoring
[33m21702f5b[m Display album art, title and artist in explorer album view
[33m3b7ed1eb[m Auto-format
[33m66b2bd16[m Fixed overlap between queue and bottom navigation bar
[33m07067b42[m Added album view (distinct from folder view) in browse activity
[33m23058ee1[m Read album names
[33m58cb7626[m File renames and moves
[33m171c4955[m Moved browse activity content to a fragment
[33m1b836528[m Fixed a bug where event handlers were leaking
[33m7a9eda63[m Dynamically enable/disable player controls
[33m4ed2c413[m Refresh queue content when flatten operation completes
[33mefc2e839[m Cleanup
[33m9c44445f[m Renamed PMediaPlayer to PolarisMediaPlayer
[33m9535403e[m Implemented pause/resume toggle
[33m13d51619[m Gradle update to 2.2.3
[33m948a627f[m Removed dead code
[33mc6f4597d[m Restore explorer items after they are swiped out for queuing
[33mf61c5658[m Cosmetic changes
[33m32e52e16[m Handle errors while queuing directories
[33m3cb16600[m Improved look and feel of swipe to queue
[33m689d1a31[m Added queuing icons
[33m91a2787c[m Fixed a bug where queue was reset before appending a directory
[33m804508a8[m Refactoring to support prettier swipe to queue
[33m184f3a71[m Cleaner handling or API result lists
[33mf35fe4a9[m Allow swiping to queue songs/directories
[33m4c3a87e3[m Auto formatting
[33ma88f8d06[m Added menu options to set playback order
[33me1a73d1a[m Added action to shuffle palylist
[33m6bc478fd[m Added shuffle icon
[33m41382042[m Added Clear playlist action
[33m3d0c1db8[m Tweaks to settings menu
[33mcc3e0b51[m Added icons for playlist management
[33me288da39[m Added next/previous controls
[33mcc7fcef1[m More spacing between media controls
[33m1aea880a[m More robust layout for music player
[33m0c981a81[m Display album art in player
[33m9cbe82ed[m Keep API magic inside ServerAPI
[33mc36b6596[m Update player info when current track changes
[33md4a2985c[m Fixed warning spam
[33m077e220c[m Play tracks by tapping them in the queue
[33m1a35ae4d[m Playing audio 101
[33m98e84309[m Removed unecessary permission
[33m2ddd83f6[m Added stub player activity
[33m5068f67a[m Added player icons
[33m073669ba[m More accurate layout previews
[33m21529ead[m Allow dismissal and reordering of queue items
[33m32a0a5e7[m Art tweaks
[33m3ee17169[m Set fixed size in browse recycler view
[33ma0ccda4f[m Missing file from previous commit
[33m8a837c8e[m Basic setup for playback queue
[33me9706013[m Highlight correct tab while navigating
[33md24f9170[m Avoid re-creating activity when already on top
[33m8446058d[m More idiomatic handling of back button
[33m54271e52[m OCD Cleanup
[33m1f6a56d4[m Missing file from previous commit
[33mff590a02[m Allow navigation between views
[33m0c663fb3[m Removed unused imports
[33m8116ff3d[m Unify collection screen and browse screen base
[33m8a03fcd7[m Allow back navigation in explorer
[33ma9a32fb9[m Cleanup
[33m0a96e169[m Formatting
[33m2861afa4[m Don't try browsing within files
[33m001c8029[m Truncate long names
[33meea8a34e[m Don't dispaly full path to every item in file browser
[33mae4d141e[m Dynamic icons in file browser
[33m8cba626e[m Added music note icon
[33m00676cb6[m Updated folder icon
[33md149ffbe[m Basic file browsing
[33m95390840[m Import cleanup
[33m2f2df1c4[m Renamed layout package to fragment
[33ma613d397[m Added auth support
[33m5131c2ce[m Added string resources for preference keys
[33m3b272c92[m Fixed usage of deprecated API
[33m0ec8c8b8[m Moved activities to separate package
[33mba6b03ee[m Whitespace tweaks
[33m05b3973e[m Art tweaks
[33me1632f9e[m Ignore user dictionaries
[33m188205ea[m Art updates
[33m7a89b4fd[m More Android Studio junk
[33md43ebaab[m Fleshed out collection screen
[33mdc11552c[m Theme cleanup
[33me9853df4[m Added icons
[33md4713a86[m Added settings activity and action bar to reach it
[33ma46aec11[m Android project setup
[33m6cbcb026[m Removed readme
[33m68f790a8[m Added license
[33m5a597ca9[m Initial commit
