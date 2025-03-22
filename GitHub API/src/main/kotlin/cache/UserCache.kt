package cache

import models.GitHubRepo
import models.GitHubUser

object UserCache {
    private val userCache = mutableMapOf<String, GitHubUser>()
    private val repoCache = mutableMapOf<String, List<GitHubRepo>>()

    fun getUser(username: String): GitHubUser? = userCache[username]
    fun saveUser(user: GitHubUser) { userCache[user.username] = user }
    fun getAllUsers(): List<GitHubUser> = userCache.values.toList()

    fun getRepos(username: String): List<GitHubRepo>? = repoCache[username]
    fun saveRepos(username: String, repos: List<GitHubRepo>) { repoCache[username] = repos }
    fun getAllRepos(): List<GitHubRepo> = repoCache.values.flatten()
}