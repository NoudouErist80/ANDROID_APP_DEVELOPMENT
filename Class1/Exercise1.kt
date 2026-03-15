// ─────────────────────────────────────────────────────────────────────────────
//  SE 3242 — Android Application Development
//  Class 01 Exercise 1: Null-Safe Data Processing
//  Concepts: data class, nullable types (?), safe call (?.), Elvis (?:),
//            let scope function, higher-order functions (filter, map, forEach)
// ─────────────────────────────────────────────────────────────────────────────

// ── Step 1: Define the data class with a nullable email ───────────────────────
// 'String?' means email CAN be null — Kotlin forces us to handle this safely
data class User(val name: String, val email: String?)

fun main() {

    // ── Step 2: Create the list of users ─────────────────────────────────────
    // Alex has an email, Blake has null, Casey has an email
    val users = listOf(
        User("Alex",  "alex@example.com"),
        User("Blake", null),               // null email — no email provided
        User("Casey", "casey@work.com")
    )

    println("════════════════════════════════════════")
    println("   SE 3242 — Exercise 1 Results")
    println("════════════════════════════════════════")
    println()

    // ── Requirement 1 & 2: Print emails or "has no email" ────────────────────
    println("📋 USER EMAIL REPORT:")
    println("────────────────────────────────────────")

    users.forEach { user ->

        // ?.let — only runs the block if email is NOT null (safe call + let)
        // ?: — Elvis operator: if email IS null, use the fallback string
        val message = user.email?.let { email ->
            // Inside this block, 'email' is guaranteed non-null
            "${user.name}: ${email.uppercase()}"   // Requirement 1: uppercase
        } ?: "${user.name} has no email"            // Requirement 2: fallback

        println("  $message")
    }

    println()

    // ── Requirement 3: Count users with valid emails ──────────────────────────
    // filter returns only users where email is not null
    // using higher-order function + null check
    val usersWithEmail = users.filter { user -> user.email != null }
    val emailCount     = usersWithEmail.size

    println("────────────────────────────────────────")
    println("📊 SUMMARY:")
    println("  Total users       : ${users.size}")
    println("  With valid email  : $emailCount")
    println("  Without email     : ${users.size - emailCount}")
    println("════════════════════════════════════════")

    // ── BONUS: Same count using filterNotNull on emails directly ──────────────
    val countAlternative = users.mapNotNull { it.email }.size
    println()
    println("✅ Verification (filterNotNull approach): $countAlternative users have emails")

    // ── BONUS 2: Show all emails in uppercase using mapNotNull ────────────────
    println()
    println("📧 All valid emails (uppercase):")
    users.mapNotNull { it.email }
         .map { it.uppercase() }
         .forEachIndexed { index, email ->
             println("  ${index + 1}. $email")
         }
}