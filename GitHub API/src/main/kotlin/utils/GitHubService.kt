package utils

import api.RetrofitInstance
import cache.UserCache
import models.GitHubRepo
import models.GitHubUser
import retrofit2.Call
import retrofit2.Response

object GitHubService {

    fun fetchGitHubUser(username: String) {
        UserCache.getUser(username)?.let {
            println("📌 اطلاعات از کش خوانده شد!")
            println(it)
            return
        }

        try {
            val userResponse: Response<GitHubUser> = RetrofitInstance.api.getUser(username).execute()
            if (userResponse.isSuccessful) {
                userResponse.body()?.let { user ->
                    fetchUserRepos(user)
                }
            } else {
                println("❌ خطا: ${userResponse.code()}")
            }
        } catch (e: Exception) {
            println("❌ خطا در اتصال به API: ${e.message}")
        }
    }

    private fun fetchUserRepos(user: GitHubUser) {
        try {
            val reposResponse: Response<List<GitHubRepo>> = RetrofitInstance.api.getUserRepos(user.username).execute()
            if (reposResponse.isSuccessful) {
                val repos = reposResponse.body().orEmpty()
                val updatedUser = user.copy(repos = repos)
                UserCache.saveUser(updatedUser)
                println("✅ اطلاعات کاربر و ریپوزیتوری‌ها دریافت شد:")
                println(updatedUser)
            } else {
                println("❌ خطا در دریافت ریپوزیتوری‌ها: ${reposResponse.code()}")
            }
        } catch (e: Exception) {
            println("❌ خطا در اتصال به API: ${e.message}")
        }
    }
}