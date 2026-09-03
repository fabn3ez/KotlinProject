// Concept Note: https://docs.google.com/document/d/1ZvSlpqKyDVVux8d41GMRsrZhY8nXpwsVDHzVy_g51DQ/edit?usp=sharing
// GROUP 5:
// 189722 - Brian Koech
// 179486 - Kamure Beyonce Kabura
// 191627 - Onserio Baraka Osumo
// 191737 - Bethuel Kilonzo
// 176676 - Daniella Wanjohi
// 189808 - Ian Simiyu

// Compile-time constant
const val MAX_USERS = 100
fun main() {
    println("========================================")
    println(" REAL ESTATE PROPERTY LISTING SYSTEM")
    println("========================================")
    println("Welcome! This system helps agents manage property listings.")

    // val: cannot be changed
    val appName = "My Listing App"
    // appName = "New Name" // Error: Val cannot be reassigned

    // var: can be changed
    var activeUsers = 10
    activeUsers = 15 // Reassignment works perfectly

    val releaseYear: Int = 2026
    var isPublished: Boolean = false

    println("App: $appName ($releaseYear)")
    println("Users: $activeUsers / $MAX_USERS")
}