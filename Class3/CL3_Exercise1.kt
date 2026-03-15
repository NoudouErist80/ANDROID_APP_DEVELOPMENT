// ─────────────────────────────────────────────────────────────────────────────
//  SE 3242 — Android Application Development
//  Class 03 — Exercise 1: Generic Function with Constraints
//
//  What this exercise is trying to show:
//  → Generics let you write ONE function that works for MANY types
//  → Type constraints (<T : Comparable<T>>) restrict which types are allowed
//  → The compiler enforces type safety — no runtime surprises
//  → fold() is a powerful higher-order function for reducing a list to one value
//  → Nullable return (T?) handles the empty list case safely
//
//  Concepts demonstrated:
//  ✅ Generic functions        → <T> type parameter
//  ✅ Upper bound constraint   → <T : Comparable<T>>
//  ✅ Nullable return type     → T?  (null if list is empty)
//  ✅ fold()                   → accumulator pattern
//  ✅ Manual iteration         → for loop approach
//  ✅ reduce()                 → alternative aggregation
//  ✅ Elvis operator           → ?: for null safety (Class 01)
//  ✅ Smart cast               → null check before comparison
//  ✅ Extension function       → maxOf as extension on List<T>
//  ✅ reified inline           → bonus advanced approach
// ─────────────────────────────────────────────────────────────────────────────


// ══════════════════════════════════════════════════════════════════════════════
//  REQUIRED FUNCTION — maxOf using fold()
//
//  WHAT:  Returns the largest element from ANY list of comparable items.
//  WHY generic?  Without generics we'd need maxOfInt(), maxOfString() etc.
//                ONE function handles ALL Comparable types.
//
//  <T : Comparable<T>> means:
//  → T can be ANY type (Int, String, Double, Date…)
//  → BUT that type MUST implement Comparable<T>
//  → Comparable<T> means it has compareTo() — it knows how to compare itself
//
//  Returns T? (nullable) because if the list is empty, there's no maximum
// ══════════════════════════════════════════════════════════════════════════════

fun <T : Comparable<T>> maxOf(list: List<T>): T? {

    // Guard: empty list → no maximum exists → return null (Class 01 null safety)
    if (list.isEmpty()) return null

    // fold() — the accumulator pattern:
    // Start with the first element as the "current maximum"
    // For each remaining element, keep whichever is larger
    //
    // fold(initial) { accumulator, currentElement -> result }
    //   acc       = the running maximum so far
    //   element   = the next item in the list
    //   compareTo = returns >0 if element > acc
    return list.fold(list.first()) { acc, element ->
        if (element > acc) element else acc
        // element > acc uses Comparable's compareTo() under the hood
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  ALTERNATIVE 1 — Manual iteration (for loop)
//  Clearest to read — shows the logic step by step
// ══════════════════════════════════════════════════════════════════════════════

fun <T : Comparable<T>> maxOfManual(list: List<T>): T? {
    if (list.isEmpty()) return null

    var currentMax = list.first()             // start with first element
    for (element in list.drop(1)) {           // iterate remaining elements
        if (element > currentMax) {           // compareTo() called by >
            currentMax = element              // found a new maximum
        }
    }
    return currentMax
}


// ══════════════════════════════════════════════════════════════════════════════
//  ALTERNATIVE 2 — Using reduce()
//  reduce() is like fold() but uses the FIRST element as the initial value
//  Shorter but throws on empty list → needs null guard
// ══════════════════════════════════════════════════════════════════════════════

fun <T : Comparable<T>> maxOfReduce(list: List<T>): T? {
    if (list.isEmpty()) return null
    // reduce: start with first element, compare with each next element
    return list.reduce { acc, element -> if (element > acc) element else acc }
}


// ══════════════════════════════════════════════════════════════════════════════
//  ALTERNATIVE 3 — Recursive approach
//  Shows how to think about the problem recursively
// ══════════════════════════════════════════════════════════════════════════════

fun <T : Comparable<T>> maxOfRecursive(list: List<T>): T? {
    return when (list.size) {
        0    -> null                     // base case: empty
        1    -> list.first()             // base case: one element
        else -> {
            val restMax = maxOfRecursive(list.drop(1))  // recurse on tail
            val head    = list.first()
            if (restMax == null || head > restMax) head else restMax
        }
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS — Extension function version
//  Makes it callable as list.maxOf() instead of maxOf(list)
//  This is how Kotlin's standard library is designed
// ══════════════════════════════════════════════════════════════════════════════

fun <T : Comparable<T>> List<T>.myMax(): T? =
    if (isEmpty()) null
    else fold(first()) { acc, e -> if (e > acc) e else acc }


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS — minOf using the same pattern
//  Shows generics are reusable — just flip the comparison
// ══════════════════════════════════════════════════════════════════════════════

fun <T : Comparable<T>> minOf(list: List<T>): T? =
    if (list.isEmpty()) null
    else list.fold(list.first()) { acc, e -> if (e < acc) e else acc }


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS — Generic data class to show generics in classes too (Class 03)
// ══════════════════════════════════════════════════════════════════════════════

data class Box<T>(val value: T) {         // generic class — T is the type inside
    fun get(): T = value
    override fun toString() = "Box($value)"
}


// ══════════════════════════════════════════════════════════════════════════════
//  MAIN FUNCTION
// ══════════════════════════════════════════════════════════════════════════════

fun main() {

    println("════════════════════════════════════════════")
    println("   SE 3242 — Class 03 Exercise 1: Generics")
    println("════════════════════════════════════════════")
    println()

    // ── REQUIRED OUTPUT ───────────────────────────────────────────────────────
    println("📋  REQUIRED OUTPUT:")
    println("────────────────────────────────────────────")

    println("  maxOf(listOf(3, 7, 2, 9))              = ${maxOf(listOf(3, 7, 2, 9))}")
    println("  maxOf(listOf(\"apple\",\"banana\",\"kiwi\")) = ${maxOf(listOf("apple", "banana", "kiwi"))}")
    println("  maxOf(emptyList())                     = ${maxOf(emptyList<Int>())}")

    println()

    // ── HOW fold() WORKS STEP BY STEP ────────────────────────────────────────
    println("🔄  HOW fold() WORKS — step by step with [3, 7, 2, 9]:")
    println("────────────────────────────────────────────")

    val numbers = listOf(3, 7, 2, 9)
    var acc = numbers.first()
    println("  Start: acc = $acc")
    numbers.drop(1).forEachIndexed { idx, element ->
        val before = acc
        if (element > acc) acc = element
        val arrow = if (element > before) "← new max!" else "← kept old"
        println("  Step ${idx + 1}: compare $before vs $element → acc = $acc  $arrow")
    }
    println("  Result: $acc")
    println()

    // ── ALL THREE ALTERNATIVES GIVE THE SAME RESULT ──────────────────────────
    println("🔍  ALL APPROACHES — same result:")
    println("────────────────────────────────────────────")

    val ints    = listOf(3, 7, 2, 9, 1, 8, 4)
    val strings = listOf("apple", "banana", "kiwi", "date")

    println("  List: $ints")
    println("  fold()     : ${maxOf(ints)}")
    println("  manual     : ${maxOfManual(ints)}")
    println("  reduce()   : ${maxOfReduce(ints)}")
    println("  recursive  : ${maxOfRecursive(ints)}")
    println("  extension  : ${ints.myMax()}")
    println()

    // ── THE GENERIC POWER — same function, different types ───────────────────
    println("⚡  GENERIC POWER — one function, many types:")
    println("────────────────────────────────────────────")

    // Integers
    val intList    = listOf(42, 17, 88, 3, 55)
    println("  Integers     $intList")
    println("  max = ${maxOf(intList)}")
    println()

    // Doubles
    val doubleList = listOf(3.14, 2.71, 1.41, 9.99, 0.57)
    println("  Doubles      $doubleList")
    println("  max = ${maxOf(doubleList)}")
    println()

    // Strings — alphabetical / lexicographic order
    val wordList   = listOf("apple", "banana", "kiwi", "date", "zucchini")
    println("  Strings      $wordList")
    println("  max = ${maxOf(wordList)}  ← lexicographic: 'z' > 'k' > 'b' > 'a'")
    println()

    // Chars
    val charList   = listOf('m', 'z', 'a', 'k', 'f')
    println("  Chars        $charList")
    println("  max = ${maxOf(charList)}")
    println()

    // Long
    val longList   = listOf(100L, 999L, 50L, 777L)
    println("  Longs        $longList")
    println("  max = ${maxOf(longList)}")
    println()

    // ── MIN + MAX together ────────────────────────────────────────────────────
    println("────────────────────────────────────────────")
    println("📊  MIN + MAX together:")
    println()

    val scores = listOf(78, 92, 45, 88, 100, 55, 73)
    println("  Scores: $scores")
    println("  Min    : ${minOf(scores)}")
    println("  Max    : ${maxOf(scores)}")
    println("  Range  : ${minOf(scores)} → ${maxOf(scores)}")

    val avg = scores.average()
    println("  Average: ${"%.1f".format(avg)}")
    println()

    // ── EMPTY LIST SAFETY ─────────────────────────────────────────────────────
    println("────────────────────────────────────────────")
    println("🛡️  NULL SAFETY — empty list handling:")
    println()

    val emptyInts:    List<Int>    = emptyList()
    val emptyStrings: List<String> = emptyList()

    // Elvis operator — provide default if result is null (Class 01)
    val maxInt    = maxOf(emptyInts)    ?: "no value"
    val maxString = maxOf(emptyStrings) ?: "no value"

    println("  maxOf(emptyList<Int>())    = $maxInt")
    println("  maxOf(emptyList<String>()) = $maxString")
    println()

    // ── GENERIC BOX CLASS ─────────────────────────────────────────────────────
    println("────────────────────────────────────────────")
    println("📦  BONUS — Generic class Box<T>:")
    println()

    val intBox    = Box(42)
    val stringBox = Box("Hello Kotlin")
    val doubleBox = Box(3.14)

    println("  $intBox    → get() = ${intBox.get()}")
    println("  $stringBox → get() = ${stringBox.get()}")
    println("  $doubleBox  → get() = ${doubleBox.get()}")
    println()

    // ── WHAT TYPES ARE NOT ALLOWED ────────────────────────────────────────────
    println("────────────────────────────────────────────")
    println("🚫  TYPE CONSTRAINT — only Comparable<T> types work:")
    println()
    println("  ✅  maxOf(listOf(1, 2, 3))          → Int implements Comparable")
    println("  ✅  maxOf(listOf(\"a\", \"b\"))        → String implements Comparable")
    println("  ✅  maxOf(listOf(1.0, 2.0))          → Double implements Comparable")
    println("  ❌  maxOf(listOf(myCustomObject))    → compile error if no Comparable")
    println()
    println("  The constraint <T : Comparable<T>> is checked at COMPILE TIME.")
    println("  No runtime crash — the compiler rejects invalid types immediately.")

    println()
    println("════════════════════════════════════════════")
    println("  ✅ Generic function    : <T : Comparable<T>>")
    println("  ✅ fold()              : accumulator pattern")
    println("  ✅ Nullable return     : T? handles empty list")
    println("  ✅ Type safety         : compiler-enforced")
    println("  ✅ Works for           : Int, Double, String, Char, Long…")
    println("════════════════════════════════════════════")
}