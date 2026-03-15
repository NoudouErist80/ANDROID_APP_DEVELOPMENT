// ─────────────────────────────────────────────────────────────────────────────
//  SE 3242 — Android Application Development
//  Class 01 — Exercise 2: Temperature Descriptions
//
//  Concepts demonstrated:
//  ✅ Nullable types          → Int?  (temp can be null)
//  ✅ Null safety             → ?: Elvis operator
//  ✅ when as expression      → returns a value directly
//  ✅ when without argument   → handles ranges like if-else chain
//  ✅ Range checks            → in 1..15, in 16..25 etc.
//  ✅ Functions               → fun describeTemperature(temp: Int?): String
//  ✅ Higher-order functions  → forEach, mapIndexed
//  ✅ String templates        → "$temp°C → $desc"
//  ✅ val vs var              → immutable list with val
// ─────────────────────────────────────────────────────────────────────────────


// ══════════════════════════════════════════════════════════════════════════════
//  CORE FUNCTION
//  - Takes a nullable Int (Int?) as parameter
//  - Returns a String description
//  - Uses 'when' WITHOUT an argument (when as boolean expression chain)
//  - Uses Elvis operator ?: to handle null before entering when
// ══════════════════════════════════════════════════════════════════════════════

fun describeTemperature(temp: Int?): String {

    // Step 1: Handle null with Elvis operator
    // If temp is null → return "No data" immediately, no need to check ranges
    // If temp is not null → 'safeTemp' is a guaranteed non-null Int
    val safeTemp = temp ?: return "No data"   // Class 01: Elvis operator ?:

    // Step 2: Use 'when' WITHOUT an argument
    // This is like a series of if-else if checks
    // Each branch is a boolean condition — first match wins
    // 'when' here is an EXPRESSION — it returns a value directly
    return when {                              // Class 01: when as expression
        safeTemp <= 0       -> "Freezing"      // 0°C and below
        safeTemp in 1..15   -> "Cold"          // 1°C to 15°C  (using range)
        safeTemp in 16..25  -> "Mild"          // 16°C to 25°C
        safeTemp in 26..35  -> "Warm"          // 26°C to 35°C
        safeTemp in 36..45  -> "Hot"           // 36°C to 45°C
        else                -> "Extreme"       // above 45°C
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS HELPER — returns the emoji icon for each description
// ══════════════════════════════════════════════════════════════════════════════

fun tempIcon(description: String): String = when (description) {
    "Freezing" -> "🥶"
    "Cold"     -> "🧥"
    "Mild"     -> "😊"
    "Warm"     -> "☀️"
    "Hot"      -> "🔥"
    "Extreme"  -> "🌋"
    "No data"  -> "❓"
    else       -> "🌡️"
}


// ══════════════════════════════════════════════════════════════════════════════
//  MAIN FUNCTION
// ══════════════════════════════════════════════════════════════════════════════

fun main() {

    println("════════════════════════════════════════════")
    println("   SE 3242 — Exercise 2: Temperature App")
    println("════════════════════════════════════════════")
    println()

    // ── PART 1: Test individual temperatures ──────────────────────────────────
    println("🌡️  PART 1 — Individual Temperature Tests:")
    println("────────────────────────────────────────────")

    val testTemps = listOf(-10, 0, 8, 20, 30, 40, 50, null)

    testTemps.forEach { temp ->
        val desc  = describeTemperature(temp)
        val icon  = tempIcon(desc)
        val label = if (temp != null) "${temp}°C" else "null"

        // String template — Class 01 Slide 4
        println("  %-8s  →  $icon  $desc".format(label))
    }

    println()

    // ── PART 2: BONUS — Real city temperatures (some null) ───────────────────
    println("🌍  PART 2 — City Temperature Report (Bonus):")
    println("────────────────────────────────────────────")

    // List of Pair(city, temperature?) — some cities have null (sensor offline)
    val cityTemps: List<Pair<String, Int?>> = listOf(
        Pair("Yaoundé",      32),
        Pair("Douala",       35),
        Pair("Paris",        12),
        Pair("Moscow",       -8),
        Pair("Dubai",        48),
        Pair("London",       null),   // sensor offline
        Pair("Nairobi",      22),
        Pair("Bafoussam",    null),   // no data
        Pair("Tokyo",        18),
        Pair("Sahara Desert",52)
    )

    cityTemps.forEach { (city, temp) ->
        val desc  = describeTemperature(temp)
        val icon  = tempIcon(desc)
        val tempStr = if (temp != null) "${temp}°C" else "N/A"

        // Formatted output — aligned columns
        println("  %-18s  %5s  →  $icon  $desc".format(city, tempStr))
    }

    println()

    // ── PART 3: Statistics ────────────────────────────────────────────────────
    println("────────────────────────────────────────────")
    println("📊  SUMMARY STATISTICS:")
    println()

    // Higher-order functions — filter, count, groupBy (Class 01)
    val validTemps   = cityTemps.filter { it.second != null }
    val nullTemps    = cityTemps.filter { it.second == null }

    println("  Total cities      : ${cityTemps.size}")
    println("  With data         : ${validTemps.size}")
    println("  No data (null)    : ${nullTemps.size}")

    // Group cities by their temperature description
    val grouped = cityTemps
        .groupBy { (_, temp) -> describeTemperature(temp) }

    println()
    println("  Cities by category:")
    grouped.forEach { (category, cities) ->
        val icon      = tempIcon(category)
        val cityNames = cities.map { it.first }.joinToString(", ")
        println("    $icon  %-10s : $cityNames".format(category))
    }

    println()

    // Average of non-null temperatures
    val avgTemp = validTemps
        .mapNotNull { it.second }           // extract non-null temps
        .average()                          // built-in average function

    println("  Average temp (valid cities): ${"%.1f".format(avgTemp)}°C")
    println("  → ${describeTemperature(avgTemp.toInt())} on average")

    println()
    println("════════════════════════════════════════════")
    println("  ✅ All requirements complete!")
    println("  ✅ Bonus: loop + groupBy + statistics")
    println("════════════════════════════════════════════")
}