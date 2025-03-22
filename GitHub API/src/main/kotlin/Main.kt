package org.example
import utils.GitHubService
import cache.UserCache
import models.GitHubUser

fun main() {
    while (true) {

        println("\n🔹 منو برنامه 🔹")
        println("1️⃣ دریافت اطلاعات کاربر از گیت‌هاب")
        println("2️⃣ نمایش لیست کاربران ذخیره‌شده")
        println("3️⃣ جستجو بر اساس نام کاربری")
        println("4️⃣ جستجو بر اساس نام ریپوزیتوری")
        println("5️⃣ خروج از برنامه")

        print("انتخاب کنید: ")
        when (readlnOrNull()?.trim()) {
            "1" -> {
                print("🔹 نام کاربری گیت‌هاب را وارد کنید: ")
                val username = readlnOrNull()?.trim()
                if (!username.isNullOrEmpty()) {
                    GitHubService.fetchGitHubUser(username)
                } else {
                    println("❌ نام کاربری معتبر وارد کنید!")
                }
            }

            "2" -> {
                println("📜 لیست کاربران ذخیره‌شده در حافظه:")
                val users = UserCache.getAllUsers()
                if (users.isEmpty()) {
                    println("ℹ️ هنوز هیچ کاربری ذخیره نشده است.")
                } else {
                    users.forEach { println("🔹 ${it.username}") }
                }
            }

            "3" -> {
                print("🔎 نام کاربری را وارد کنید: ")
                val username = readlnOrNull()?.trim()
                val user = username?.let { UserCache.getUser(it) }
                if (user != null) {
                    println("✅ اطلاعات کاربر پیدا شد:")
                    println(user)
                } else {
                    println("❌ کاربر در حافظه یافت نشد.")
                }
            }

            "4" -> {
                print("🔎 نام ریپوزیتوری را وارد کنید: ")
                val repoName = readlnOrNull()?.trim()
                val users = UserCache.getAllUsers()

                val matchingUsers = users.filter { user ->
                    user.repos.any { repo -> repo.name.contains(repoName ?: "", ignoreCase = true) }
                }

                if (matchingUsers.isNotEmpty()) {
                    println("✅ کاربران دارای این ریپوزیتوری:")
                    matchingUsers.forEach { println("🔹 ${it.username}") }
                } else {
                    println("❌ هیچ کاربری با این ریپوزیتوری پیدا نشد.")
                }
            }

            "5" -> {
                println("👋 خروج از برنامه...")
                return
            }

            else -> println("❌ گزینه نامعتبر است! لطفاً یکی از گزینه‌های 1 تا 5 را انتخاب کنید.")
        }
    }
}