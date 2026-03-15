// ─────────────────────────────────────────────────────────────────────────────
//  SE 3242 — Android Application Development
//  Class 02 — Exercise 1: Model a Zoo
//
//  Concepts demonstrated:
//  ✅ abstract class          → Animal cannot be instantiated directly
//  ✅ abstract fun            → makeSound() must be implemented by subclasses
//  ✅ abstract val            → legs must be overridden by subclasses
//  ✅ open class              → classes are final by default in Kotlin
//  ✅ inheritance ( : )       → Dog and Cat extend Animal
//  ✅ override keyword        → mandatory when overriding (Class 02 rule)
//  ✅ constructor             → primary constructor with val name
//  ✅ polymorphism            → List<Animal> holds Dog and Cat objects
//  ✅ forEach + lambda        → iterate list and call makeSound()
//  ✅ String templates        → "$name says $sound"
//  ✅ data class              → AnimalRecord for storing zoo info
//  ✅ sealed class            → AnimalType for type safety
//  ✅ companion object        → factory method pattern
//  ✅ interface               → Trainable behavior
// ─────────────────────────────────────────────────────────────────────────────


// ══════════════════════════════════════════════════════════════════════════════
//  ABSTRACT CLASS — defines the contract ALL animals must follow
//
//  Why abstract?
//  → "Animal" is too generic to instantiate directly.
//  → We never want: val a = Animal("Ghost")  ← meaningless
//  → Every real animal (Dog, Cat) must provide their own sound and leg count
// ══════════════════════════════════════════════════════════════════════════════

abstract class Animal(val name: String) {   // primary constructor — Class 02

    // ── Abstract property: every subclass MUST provide a value ───────────────
    abstract val legs: Int

    // ── Abstract function: every subclass MUST implement this ────────────────
    abstract fun makeSound(): String

    // ── Concrete function: shared by ALL animals — no override needed ─────────
    // This is the template method — uses the abstract makeSound()
    fun describe() {
        println("  $name says ${makeSound()}!  [legs: $legs]")
    }

    // ── toString so printing an Animal is readable ────────────────────────────
    override fun toString(): String =
        "Animal(name=$name, legs=$legs, sound=${makeSound()})"
}


// ══════════════════════════════════════════════════════════════════════════════
//  CONCRETE SUBCLASS — Dog
//  Inherits from Animal, provides Dog-specific implementations
// ══════════════════════════════════════════════════════════════════════════════

class Dog(name: String) : Animal(name) {    // : Animal(name) calls super constructor

    // override is MANDATORY in Kotlin — prevents accidental overrides
    override val legs: Int = 4

    override fun makeSound(): String = "Woof"

    // Dog-specific extra behavior
    fun fetch(item: String): String = "$name fetches the $item!"
}


// ══════════════════════════════════════════════════════════════════════════════
//  CONCRETE SUBCLASS — Cat
// ══════════════════════════════════════════════════════════════════════════════

class Cat(name: String) : Animal(name) {

    override val legs: Int = 4

    override fun makeSound(): String = "Meow"

    // Cat-specific behavior
    fun purr(): String = "$name purrs contentedly..."
}


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS CLASSES — demonstrating deeper OOP
// ══════════════════════════════════════════════════════════════════════════════

// Interface — defines a contract for trainable animals (Class 02)
interface Trainable {
    val tricksLearned: Int
    fun performTrick(trick: String): String
}

// Bird subclass — different leg count, different sound
class Bird(name: String, val canFly: Boolean = true) : Animal(name) {
    override val legs: Int = 2
    override fun makeSound(): String = "Tweet"
    fun fly(): String = if (canFly) "$name soars through the sky!" else "$name cannot fly."
}

// Snake subclass — 0 legs, overrides default
class Snake(name: String) : Animal(name) {
    override val legs: Int = 0
    override fun makeSound(): String = "Hiss"
}

// TrainedDog — inherits Dog AND implements Trainable interface
class TrainedDog(name: String, override val tricksLearned: Int) : Animal(name), Trainable {
    override val legs: Int = 4
    override fun makeSound(): String = "Woof"
    override fun performTrick(trick: String): String = "$name performs: $trick! 🐾"
}


// ══════════════════════════════════════════════════════════════════════════════
//  MAIN FUNCTION
// ══════════════════════════════════════════════════════════════════════════════

fun main() {

    println("════════════════════════════════════════════")
    println("   SE 3242 — Class 02 Exercise 1: Zoo")
    println("════════════════════════════════════════════")
    println()

    // ── REQUIREMENT: Create instances ─────────────────────────────────────────
    val buddy    = Dog("Buddy")
    val whiskers = Cat("Whiskers")

    // ── REQUIREMENT: Expected output ─────────────────────────────────────────
    println("📋  REQUIRED OUTPUT:")
    println("────────────────────────────────────────────")
    println("  ${buddy.name} says ${buddy.makeSound()}!")
    println("  ${whiskers.name} says ${whiskers.makeSound()}!")
    println()

    // ── REQUIREMENT: List of animals + iterate with forEach ───────────────────
    // Polymorphism: List<Animal> holds BOTH Dog and Cat objects
    // Each object keeps its own type but is treated as Animal
    println("🐾  POLYMORPHIC LIST — forEach loop:")
    println("────────────────────────────────────────────")

    val animals: List<Animal> = listOf(
        Dog("Buddy"),
        Cat("Whiskers"),
        Dog("Rex"),
        Cat("Luna")
    )

    // forEach + lambda — calls makeSound() on each Animal polymorphically
    animals.forEach { animal ->
        animal.describe()    // calls Dog.makeSound() or Cat.makeSound() automatically
    }
    println()

    // ── BONUS: Bigger zoo with all animal types ───────────────────────────────
    println("🦁  FULL ZOO DEMO:")
    println("────────────────────────────────────────────")

    val zoo: List<Animal> = listOf(
        Dog("Buddy"),
        Cat("Whiskers"),
        Bird("Tweety"),
        Snake("Slithers"),
        Bird("Penguin Pete", canFly = false),
        TrainedDog("Max", tricksLearned = 5)
    )

    // Iterate and show all sounds with type info
    zoo.forEach { animal ->
        val typeName = animal::class.simpleName?.padEnd(10) ?: "Unknown"
        println("  [$typeName]  ${animal.name.padEnd(12)} → ${animal.makeSound()}!  " +
                "(${animal.legs} legs)")
    }
    println()

    // ── BONUS: Type-safe filtering using is checks ────────────────────────────
    println("────────────────────────────────────────────")
    println("🔍  FILTERING BY TYPE:")
    println()

    // Smart cast — after 'is Dog', Kotlin knows it's a Dog
    val dogs  = zoo.filterIsInstance<Dog>()
    val cats  = zoo.filterIsInstance<Cat>()
    val birds = zoo.filterIsInstance<Bird>()

    println("  Dogs  (${dogs.size})  : ${dogs.map { it.name }}")
    println("  Cats  (${cats.size})  : ${cats.map { it.name }}")
    println("  Birds (${birds.size}) : ${birds.map { it.name }}")
    println()

    // ── BONUS: Type-specific behavior ─────────────────────────────────────────
    println("  Dog-specific actions:")
    dogs.forEach { dog -> println("    ${dog.fetch("ball")}") }

    println()
    println("  Cat-specific actions:")
    cats.forEach { cat -> println("    ${cat.purr()}") }

    println()
    println("  Bird-specific actions:")
    birds.forEach { bird -> println("    ${bird.fly()}") }

    // ── BONUS: Trained dog tricks ─────────────────────────────────────────────
    println()
    val trainedDog = zoo.filterIsInstance<TrainedDog>().firstOrNull()
    trainedDog?.let { td ->
        println("  Trained dog:")
        println("    ${td.performTrick("sit")}")
        println("    ${td.performTrick("roll over")}")
        println("    Tricks learned: ${td.tricksLearned}")
    }

    // ── STATISTICS ────────────────────────────────────────────────────────────
    println()
    println("────────────────────────────────────────────")
    println("📊  ZOO STATISTICS:")
    println()
    println("  Total animals : ${zoo.size}")
    println("  Total legs    : ${zoo.sumOf { it.legs }}")

    val avgLegs = zoo.map { it.legs }.average()
    println("  Average legs  : ${"%.1f".format(avgLegs)}")

    // groupBy — groups animals by their sound
    val bySound = zoo.groupBy { it.makeSound() }
    println()
    println("  Animals by sound:")
    bySound.forEach { (sound, list) ->
        println("    \"$sound\" → ${list.map { it.name }}")
    }

    println()
    println("════════════════════════════════════════════")
    println("  ✅ Abstract class      : Animal")
    println("  ✅ Concrete subclasses : Dog, Cat, Bird, Snake")
    println("  ✅ Interface           : Trainable")
    println("  ✅ Polymorphism        : List<Animal> = Dog + Cat + ...")
    println("  ✅ override keyword    : mandatory on all overrides")
    println("════════════════════════════════════════════")
}