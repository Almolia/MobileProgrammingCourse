package models

import com.google.gson.annotations.SerializedName

data class GitHubUser(
    @SerializedName("login") val username: String,  // GitHub username
    @SerializedName("name") val name: String?,     // Full name (nullable)
    @SerializedName("followers") val followers: Int,
    @SerializedName("following") val following: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("public_repos") val publicRepos: Int,
    val repos: List<GitHubRepo> = emptyList()  // List of repositories
) {
    override fun toString(): String {
        return """
            Username: $username
            Name: ${name ?: "N/A"}
            Followers: $followers
            Following: $following
            Created At: $createdAt
            Public Repos: $publicRepos
            Repositories: ${repos.joinToString { it.name }}
        """.trimIndent()
    }
}