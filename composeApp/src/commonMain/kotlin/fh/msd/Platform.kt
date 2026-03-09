package fh.msd

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform