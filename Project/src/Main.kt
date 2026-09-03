// Concept Note: https://docs.google.com/document/d/1ZvSlpqKyDVVux8d41GMRsrZhY8nXpwsVDHzVy_g51DQ/edit?usp=sharing
// GROUP 5:
// 189722 - Brian Koech
// 179486 - Kamure Beyonce Kabura
// 191627 - Onserio Baraka Osumo
// 191636 - Bethuel Kilonzo
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

    // ==================================================
    // DATA TYPES & INPUT/OUTPUT: Register a Property
    // ==================================================
    // This section lets an agent register a new property listing.
    // It demonstrates several Kotlin data types (Int, Double, String,
    // Char, Boolean) and shows how to read user input from the
    // console (readLine) and convert it to the correct type before
    // printing a clear, formatted summary of what was entered.
    println()
    println("----------------------------------------")
    println("REGISTER A NEW PROPERTY LISTING")
    println("----------------------------------------")
    println("Please enter the property details below.")

    // Int: whole number used as a unique identifier
    print("Enter property ID number: ")
    val propertyId: Int = readLine()!!.trim().toInt()

    // String: text data type used for free-form details
    print("Enter property address: ")
    val address: String = readLine()!!.trim()

    // Char: a single character representing a category code
    print("Enter property type (H = House, A = Apartment, C = Commercial): ")
    val propertyType: Char = readLine()!!.trim().uppercase().first()

    // Double: decimal number needed for currency values
    print("Enter monthly rental price (KES): ")
    val price: Double = readLine()!!.trim().toDouble()

    // Int: whole number for a simple count
    print("Enter number of bedrooms: ")
    val bedrooms: Int = readLine()!!.trim().toInt()

    // Boolean: true/false value derived from a yes/no text answer
    print("Is the property currently available? (yes/no): ")
    val availabilityInput: String = readLine()!!.trim().lowercase()
    val isAvailable: Boolean = availabilityInput == "yes"

    println("Processing property listing...")
    println("----------------------------------------")
    println("PROPERTY LISTING SUMMARY")
    println("----------------------------------------")
    println("Property ID     : $propertyId")
    println("Address         : $address")
    println("Type            : $propertyType")
    println("Bedrooms        : $bedrooms")
    println("Monthly Price   : KES ${"%,.2f".format(price)}")
    println("Availability    : ${if (isAvailable) "Available" else "Not Available"}")
    println("----------------------------------------")
}
