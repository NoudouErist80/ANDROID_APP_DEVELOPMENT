// ─────────────────────────────────────────────────────────────────────────────
//  SE 3242 — Android Application Development
//  Class 01 — Exercise 3: Filtering and Transforming Collections
//
//  Concepts demonstrated:
//  ✅ Nullable types in collections  → List<Int?>
//  ✅ filterNotNull()                → removes all null values
//  ✅ filter { it != null }          → alternative approach
//  ✅ map()                          → transforms each element
//  ✅ sum()                          → aggregates to single value
//  ✅ Method chaining                → pipeline of transformations
//  ✅ One-liner challenge            → single expression chain
//  ✅ Higher-order functions         → lambdas in map, filter
//  ✅ val vs var                     → immutable collection with val
//  ✅ String templates               → "$result" interpolation
//  ✅ fold()                         → alternative to sum()
//  ✅ reduce()                       → another aggregation option
// ─────────────────────────────────────────────────────────────────────────────

fun main() {

    // ── The starting list — contains nullable integers ────────────────────────
    // List<Int?> means each element is either an Int OR null
    val numbers: List<Int?> = listOf(1, null, 3, null, 5, 6, null, 8)

    println("════════════════════════════════════════════")
    println("   SE 3242 — Exercise 3: Collections")
    println("════════════════════════════════════════════")
    println()
    println("📋 Original list : $numbers")
    println("   Size          : ${numbers.size} elements")
    println("   Nulls         : ${numbers.count { it == null }}")
    println("   Non-nulls     : ${numbers.count { it != null }}")
    println()

    // ══════════════════════════════════════════════════════════════════════════
    //  STEP-BY-STEP APPROACH  (Requirements 1, 2, 3, 4 separately)
    //  This shows clearly what each function does
    // ══════════════════════════════════════════════════════════════════════════

    println("🔢  STEP-BY-STEP BREAKDOWN:")
    println("────────────────────────────────────────────")

    // STEP 1 — filterNotNull()
    // Removes all null values from the list
    // Returns List<Int> (no longer nullable — the ? is gone!)
    val nonNullNumbers: List<Int> = numbers.filterNotNull()
    println("  Step 1 — filterNotNull()  : $nonNullNumbers")
    println("           Type after       : List<Int>  (nulls removed)")

    // STEP 2 — map { it * 2 }
    // Transforms each element — doubles every number
    // { it * 2 } is a lambda — 'it' refers to the current element
    val doubled: List<Int> = nonNullNumbers.map { it * 2 }
    println()
    println("  Step 2 — map { it * 2 }   : $doubled")
    println("           Each element doubled")

    // STEP 3 + 4 — sum()
    // Adds all elements together → returns a single Int
    val total: Int = doubled.sum()
    println()
    println("  Step 3 — sum()            : $total")
    println("           All doubled values added")

    println()
    println("  ✅ Final answer: $total")
    println()

    // ══════════════════════════════════════════════════════════════════════════
    //  ONE-LINER CHALLENGE
    //  All 3 steps in a single method chain — same result!
    // ══════════════════════════════════════════════════════════════════════════

    println("────────────────────────────────────────────")
    println("⚡  ONE-LINER CHALLENGE:")
    println()

    // The entire operation in ONE expression:
    // numbers → filterNotNull → map(double) → sum
    val oneLinerResult = numbers
        .filterNotNull()     // Step 1: remove nulls    → [1, 3, 5, 6, 8]
        .map { it * 2 }      // Step 2: double each     → [2, 6, 10, 12, 16]
        .sum()               // Step 3: sum all         → 46

    println("  numbers.filterNotNull().map { it * 2 }.sum()")
    println()
    println("  Result: $oneLinerResult")
    println()
    println("  ✅ Same answer as step-by-step: ${oneLinerResult == total}")

    // ══════════════════════════════════════════════════════════════════════════
    //  BONUS — Alternative approaches to show more Kotlin concepts
    // ══════════════════════════════════════════════════════════════════════════

    println()
    println("────────────────────────────────────────────")
    println("🎯  BONUS — Alternative Approaches:")
    println()

    // ALTERNATIVE 1: Using mapNotNull() — filter + map in ONE step
    // mapNotNull applies a transform and automatically removes nulls
    val withMapNotNull = numbers
        .mapNotNull { it?.times(2) }  // ?.times(2) = safe call to multiply
        .sum()
    println("  mapNotNull { it?.times(2) }.sum()  → $withMapNotNull")
    println("  (mapNotNull = filter + map combined)")

    // ALTERNATIVE 2: Using filter { it != null } instead of filterNotNull()
    val withFilter = numbers
        .filter { it != null }        // keeps non-null only
        .map { it!! * 2 }             // !! = we know it's safe here
        .sum()
    println()
    println("  filter { it != null }.map { it!! * 2 }.sum() → $withFilter")

    // ALTERNATIVE 3: Using fold() — manual accumulation
    // fold starts with 0, adds each doubled non-null value to the accumulator
    val withFold = numbers.fold(0) { acc, n ->
        acc + (n?.times(2) ?: 0)      // if n is null, add 0; else add n*2
    }
    println()
    println("  fold(0) { acc, n -> acc + (n?.times(2) ?: 0) } → $withFold")
    println("  (fold = manual accumulator, handles null inline)")

    // ALTERNATIVE 4: Using sumOf() — most concise for sum with transform
    val withSumOf = numbers.sumOf { it?.times(2) ?: 0 }
    println()
    println("  sumOf { it?.times(2) ?: 0 } → $withSumOf")
    println("  (sumOf = map + sum in one step)")

    // ══════════════════════════════════════════════════════════════════════════
    //  VISUAL PIPELINE
    // ══════════════════════════════════════════════════════════════════════════

    println()
    println("────────────────────────────────────────────")
    println("🔄  VISUAL PIPELINE (step by step):")
    println()

    println("  Input    :  [1, null, 3, null, 5, 6, null, 8]")
    println("                  ↓  filterNotNull()")
    println("  Step 1   :  [1, 3, 5, 6, 8]            (3 nulls removed)")
    println("                  ↓  map { it * 2 }")
    println("  Step 2   :  [2, 6, 10, 12, 16]          (each doubled)")
    println("                  ↓  sum()")
    println("  Step 3   :  2 + 6 + 10 + 12 + 16 = $total")

    // ══════════════════════════════════════════════════════════════════════════
    //  RESULTS TABLE — all methods produce same answer
    // ══════════════════════════════════════════════════════════════════════════

    println()
    println("────────────────────────────────────────────")
    println("📊  RESULTS TABLE:")
    println()
    println("  %-45s  %s".format("Method", "Result"))
    println("  %-45s  %s".format("-".repeat(44), "------"))
    println("  %-45s  %d".format("Step-by-step (filterNotNull + map + sum)", total))
    println("  %-45s  %d".format("One-liner chain", oneLinerResult))
    println("  %-45s  %d".format("mapNotNull { it?.times(2) }.sum()", withMapNotNull))
    println("  %-45s  %d".format("filter + map + sum", withFilter))
    println("  %-45s  %d".format("fold(0) with Elvis", withFold))
    println("  %-45s  %d".format("sumOf { it?.times(2) ?: 0 }", withSumOf))
    println()

    val allMatch = listOf(total, oneLinerResult, withMapNotNull, withFilter, withFold, withSumOf)
        .all { it == total }
    println("  ✅ All methods return same result: $allMatch")

    println()
    println("════════════════════════════════════════════")
    println("  Answer: The sum of doubled non-null values")
    println("  = $total")
    println("════════════════════════════════════════════")
}