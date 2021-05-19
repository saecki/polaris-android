package agersant.polaris.api

enum class ThumbnailSize {
    Small,
    Large,
    Native;

    override fun toString(): String {
        return when (this) {
            Small -> "small"
            Large -> "large"
            Native -> "native"
        }
    }
}
