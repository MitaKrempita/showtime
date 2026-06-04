package com.example.showtime.presentation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun cachedImageRequest(url: String?): ImageRequest? {
    if (url == null) return null
    return ImageRequest.Builder(LocalPlatformContext.current)
        .data(url)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .build()
}

@Composable
fun PrefetchImages(urls: List<String?>) {
    val context = LocalPlatformContext.current
    LaunchedEffect(urls) {
        val imageLoader = SingletonImageLoader.get(context)
        withContext(Dispatchers.Default) {
            urls.filterNotNull().distinct().forEach { url ->
                runCatching {
                    imageLoader.execute(
                        ImageRequest.Builder(context)
                            .data(url)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .networkCachePolicy(CachePolicy.ENABLED)
                            .build()
                    )
                }
            }
        }
    }
}
