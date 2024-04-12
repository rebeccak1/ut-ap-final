package edu.cs371m.reddit.api

import android.text.SpannableString
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import edu.cs371m.reddit.MainActivity

class RedditPostRepository(private val redditApi: RedditApi) {
    // NB: This is for our testing.
    private val gson : Gson = GsonBuilder().registerTypeAdapter(
            SpannableString::class.java, RedditApi.SpannableDeserializer()
        ).create()

    private fun unpackPosts(response: RedditApi.ListingResponse): List<RedditPost> {
        // XXX Write me.
        val posts = mutableListOf<RedditPost>()
        for (i in 0..<response.data.children.size){
            posts.add(response.data.children[i].data)
        }
        return posts.toList()
    }

    suspend fun getPosts(subreddit: String): List<RedditPost> {
        val response : RedditApi.ListingResponse?
        if (MainActivity.globalDebug) {
            response = gson.fromJson(
                MainActivity.jsonAww100,
                RedditApi.ListingResponse::class.java)
        } else {
            // XXX Write me.
            Log.d("XXX", "get posts")
            response = redditApi.getPosts(subreddit)
            Log.d("XXX", response.data.toString())

        }
        return unpackPosts(response!!)
    }

    suspend fun getSubreddits(): List<RedditPost> {
        val response : RedditApi.ListingResponse?
        if (MainActivity.globalDebug) {
            response = gson.fromJson(
                MainActivity.subreddit1,
                RedditApi.ListingResponse::class.java)
        } else {
            // XXX Write me.
            response = redditApi.getSubreddits()
        }
        return unpackPosts(response!!)
    }
}
