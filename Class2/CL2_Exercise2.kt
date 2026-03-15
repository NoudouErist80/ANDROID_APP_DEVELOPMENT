// ─────────────────────────────────────────────────────────────────────────────
//  SE 3242 — Android Application Development
//  Class 02 — Exercise 2: Model Network Request State with Sealed Class
//
//  Concepts demonstrated:
//  ✅ sealed class          → all subclasses known at compile time
//  ✅ object subclass       → Loading (singleton, no data)
//  ✅ data class subclass   → Success(data), Error(message) carry data
//  ✅ when (exhaustive)     → no else needed — compiler checks all cases
//  ✅ is keyword            → type checking / smart cast
//  ✅ smart cast            → after 'is Success', compiler knows the type
//  ✅ forEach + lambda      → iterate the states list
//  ✅ companion object      → factory helpers
//  ✅ higher-order function → onSuccess, onError callbacks
//  ✅ nullable safe call    → state?.let { }
//  ✅ String templates      → "$data", "$message"
// ─────────────────────────────────────────────────────────────────────────────


// ══════════════════════════════════════════════════════════════════════════════
//  SEALED CLASS — NetworkState
//
//  WHY sealed?
//  → All possible states are defined HERE, in this file.
//  → No other file can create a new NetworkState subclass.
//  → The compiler KNOWS every possible state at compile time.
//  → This means 'when' on a sealed class is EXHAUSTIVE — no else needed!
//
//  Compare to a regular class:
//  → open class NetworkState  ← anyone can extend, compiler can't verify all cases
//  → sealed class NetworkState ← closed set, compiler guarantees exhaustiveness
// ══════════════════════════════════════════════════════════════════════════════

sealed class NetworkState {

    // ── object subclass — Loading has NO data, only one instance ever exists ──
    // Like a singleton: NetworkState.Loading is always the same object
    object Loading : NetworkState()

    // ── data class subclass — Success CARRIES data ────────────────────────────
    // data class auto-generates: equals(), hashCode(), toString(), copy()
    data class Success(val data: String) : NetworkState()

    // ── data class subclass — Error CARRIES a message ─────────────────────────
    data class Error(val message: String) : NetworkState()

    // ── Convenience property — available on ALL states ────────────────────────
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError:   Boolean get() = this is Error

    // ── companion object — factory helpers (Class 02) ─────────────────────────
    companion object {
        fun success(data: String)    = Success(data)
        fun error(message: String)   = Error(message)
        fun loading()                = Loading
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  REQUIRED FUNCTION — handleState
//
//  WHY when without else?
//  → sealed class = compiler knows ALL subclasses
//  → when covers Loading, Success, Error → that is EVERY case
//  → no else branch needed — compiler ENFORCES this!
//  → If you add a new subclass later, compiler will ERROR until you handle it
// ══════════════════════════════════════════════════════════════════════════════

fun handleState(state: NetworkState) {
    when (state) {                              // exhaustive — no else needed!
        is NetworkState.Loading         -> {
            println("  ⏳  Loading... please wait")
        }
        is NetworkState.Success         -> {    // smart cast: state is now Success
            println("  ✅  Success! Data received: ${state.data}")
        }
        is NetworkState.Error           -> {    // smart cast: state is now Error
            println("  ❌  Error: ${state.message}")
        }
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS — handleStateAdvanced
//  Shows how to extract values and use smart casts more deeply
// ══════════════════════════════════════════════════════════════════════════════

fun handleStateAdvanced(state: NetworkState) {
    // 'when' as EXPRESSION — returns a String (Class 01 + 02 concept)
    val message = when (state) {
        is NetworkState.Loading  -> "Fetching data from server…"
        is NetworkState.Success  -> "Loaded: ${state.data}"   // smart cast
        is NetworkState.Error    -> "Failed: ${state.message} — please retry"
    }
    println("  → $message")
}


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS — Higher-order function with callbacks
//  Simulates how Android apps typically use sealed states
// ══════════════════════════════════════════════════════════════════════════════

fun handleWithCallbacks(
    state: NetworkState,
    onLoading: () -> Unit,
    onSuccess: (String) -> Unit,      // lambda: receives data String
    onError:   (String) -> Unit       // lambda: receives error String
) {
    when (state) {
        is NetworkState.Loading  -> onLoading()
        is NetworkState.Success  -> onSuccess(state.data)
        is NetworkState.Error    -> onError(state.message)
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  MAIN FUNCTION
// ══════════════════════════════════════════════════════════════════════════════

fun main() {

    println("════════════════════════════════════════════")
    println("   SE 3242 — Class 02 Exercise 2: Sealed")
    println("════════════════════════════════════════════")
    println()

    // ── REQUIRED: Create the states list ─────────────────────────────────────
    val states = listOf(
        NetworkState.Loading,
        NetworkState.Success("User data loaded"),
        NetworkState.Error("Network timeout")
    )

    // ── REQUIRED: forEach + handleState ──────────────────────────────────────
    println("📋  REQUIRED OUTPUT:")
    println("────────────────────────────────────────────")
    states.forEach { handleState(it) }
    println()

    // ── BONUS: when as expression ─────────────────────────────────────────────
    println("📋  BONUS — when as expression:")
    println("────────────────────────────────────────────")
    states.forEach { handleStateAdvanced(it) }
    println()

    // ── BONUS: Checking state type ────────────────────────────────────────────
    println("────────────────────────────────────────────")
    println("🔍  STATE TYPE CHECKS:")
    println()
    states.forEach { state ->
        println("  $state")
        println("    isLoading : ${state.isLoading}")
        println("    isSuccess : ${state.isSuccess}")
        println("    isError   : ${state.isError}")
        println()
    }

    // ── BONUS: Higher-order function usage ────────────────────────────────────
    println("────────────────────────────────────────────")
    println("⚡  BONUS — Higher-order callbacks:")
    println()
    states.forEach { state ->
        handleWithCallbacks(
            state,
            onLoading = { println("  [LOADING]  Showing spinner in UI…") },
            onSuccess = { data    -> println("  [SUCCESS]  Displaying: $data") },
            onError   = { message -> println("  [ERROR]    Showing alert: $message") }
        )
    }
    println()

    // ── BONUS: Real-world simulation — multiple API calls ─────────────────────
    println("────────────────────────────────────────────")
    println("🌐  REAL-WORLD SIMULATION — API calls:")
    println()

    // Simulate a sequence of states for a login request
    val loginFlow = listOf(
        NetworkState.Loading,
        NetworkState.Success("{ \"user\": \"Alice\", \"token\": \"abc123\" }"),
    )

    // Simulate a sequence of states for a profile fetch that fails
    val profileFlow = listOf(
        NetworkState.Loading,
        NetworkState.Error("401 Unauthorized — token expired")
    )

    println("  Login request:")
    loginFlow.forEach { state ->
        print("    ")
        handleState(state)
    }

    println()
    println("  Profile fetch:")
    profileFlow.forEach { state ->
        print("    ")
        handleState(state)
    }

    // ── BONUS: Statistics on a list of states ─────────────────────────────────
    println()
    println("────────────────────────────────────────────")
    println("📊  STATE STATISTICS:")
    println()

    val allStates = listOf(
        NetworkState.Loading,
        NetworkState.Success("Data 1"),
        NetworkState.Success("Data 2"),
        NetworkState.Error("Timeout"),
        NetworkState.Loading,
        NetworkState.Error("404 Not Found"),
        NetworkState.Success("Data 3")
    )

    val loadingCount = allStates.count { it is NetworkState.Loading }
    val successCount = allStates.count { it is NetworkState.Success }
    val errorCount   = allStates.count { it is NetworkState.Error }

    println("  Total states : ${allStates.size}")
    println("  Loading      : $loadingCount")
    println("  Success      : $successCount")
    println("  Error        : $errorCount")

    // Extract all successful data values using filterIsInstance + map
    val allData = allStates
        .filterIsInstance<NetworkState.Success>()
        .map { it.data }
    println()
    println("  Successful data values:")
    allData.forEach { println("    → $it") }

    // Extract all error messages
    val allErrors = allStates
        .filterIsInstance<NetworkState.Error>()
        .map { it.message }
    println()
    println("  Error messages:")
    allErrors.forEach { println("    ⚠ $it") }

    println()
    println("════════════════════════════════════════════")
    println("  ✅ sealed class  : NetworkState")
    println("  ✅ object        : Loading (singleton)")
    println("  ✅ data class    : Success, Error (carry data)")
    println("  ✅ when          : exhaustive, no else needed")
    println("  ✅ smart cast    : is Success → access .data")
    println("  ✅ polymorphism  : List<NetworkState> mixed types")
    println("════════════════════════════════════════════")
}