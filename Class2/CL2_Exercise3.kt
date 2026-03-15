// ─────────────────────────────────────────────────────────────────────────────
//  SE 3242 — Android Application Development
//  Class 02 — Exercise 3: Drawable Shapes with Interfaces
//
//  Concepts demonstrated:
//  ✅ interface               → Drawable defines the contract (what, not how)
//  ✅ interface implementation → Circle, Square, Triangle implement Drawable
//  ✅ override keyword        → mandatory when implementing interface methods
//  ✅ multiple interfaces     → Resizable also implemented by shapes
//  ✅ abstract class          → Shape base with shared properties
//  ✅ constructor             → primary constructor with shape properties
//  ✅ computed properties     → area(), perimeter() as val with get()
//  ✅ polymorphism            → List<Drawable> holds all shape types
//  ✅ forEach + lambda        → iterate and call draw() on each
//  ✅ String repeat           → "*".repeat(n) for ASCII art
//  ✅ when expression         → switch on shape type
//  ✅ data class              → ShapeInfo carries computed info
//  ✅ companion object        → factory methods for shapes
// ─────────────────────────────────────────────────────────────────────────────
import kotlin.math.*


// ══════════════════════════════════════════════════════════════════════════════
//  INTERFACE — Drawable
//
//  WHY interface and not abstract class?
//  → Drawable only defines BEHAVIOUR (what shapes can do)
//  → It carries NO STATE (no properties with values)
//  → Multiple unrelated classes can implement it freely
//  → In Kotlin, a class can implement MANY interfaces but extend only ONE class
// ══════════════════════════════════════════════════════════════════════════════

interface Drawable {
    fun draw()                        // every shape MUST implement this

    // Interface DEFAULT implementation (Class 02 — default methods)
    // Subclasses can override this, but don't have to
    fun describe() {
        println("  This shape is drawable.")
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS INTERFACE — Resizable
//  A shape can implement BOTH Drawable AND Resizable
// ══════════════════════════════════════════════════════════════════════════════

interface Resizable {
    fun resize(factor: Double): Drawable   // returns a new scaled shape
}


// ══════════════════════════════════════════════════════════════════════════════
//  ABSTRACT BASE CLASS — Shape
//  Provides shared state (color, name) and concrete helpers
//  Implements Drawable — so all subclasses are also Drawable
// ══════════════════════════════════════════════════════════════════════════════

abstract class Shape(
    val name:  String,
    val color: String = "white"
) : Drawable {

    // Abstract computed properties — each shape calculates its own
    abstract val area:      Double
    abstract val perimeter: Double

    // Concrete shared method — prints info about any shape
    fun printInfo() {
        println("  ┌─────────────────────────────────")
        println("  │ Shape     : $name")
        println("  │ Color     : $color")
        println("  │ Area      : ${"%.2f".format(area)}")
        println("  │ Perimeter : ${"%.2f".format(perimeter)}")
        println("  └─────────────────────────────────")
    }

    // Override the default describe() from Drawable
    override fun describe() {
        println("  $name ($color) — area: ${"%.1f".format(area)}")
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  CLASS — Circle
//  Extends Shape (gets name/color + printInfo)
//  Implements Drawable (must override draw)
//  Implements Resizable (must override resize)
// ══════════════════════════════════════════════════════════════════════════════

class Circle(
    val radius: Int,
    color: String = "white"
) : Shape("Circle", color), Resizable {

    // Computed properties — calculated from radius
    override val area:      Double get() = PI * radius * radius
    override val perimeter: Double get() = 2 * PI * radius

    // ASCII art draw — builds circle row by row using distance formula
    override fun draw() {
        println("  ── Circle (radius = $radius) ──")
        val size = radius * 2 + 1
        for (row in 0 until size) {
            val sb = StringBuilder("  ")
            for (col in 0 until size) {
                val dy = row - radius
                val dx = col - radius
                // A point is on the circle if its distance from center ≈ radius
                val dist = sqrt((dx * dx + dy * dy).toDouble())
                sb.append(if (abs(dist - radius) < 0.6) "* " else "  ")
            }
            println(sb)
        }
    }

    override fun resize(factor: Double): Drawable =
        Circle((radius * factor).toInt(), color)

    companion object {
        fun small()  = Circle(2)
        fun medium() = Circle(4)
        fun large()  = Circle(6)
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  CLASS — Square
//  Extends Shape, implements Drawable and Resizable
// ══════════════════════════════════════════════════════════════════════════════

class Square(
    val side: Int,
    color: String = "white"
) : Shape("Square", color), Resizable {

    override val area:      Double get() = (side * side).toDouble()
    override val perimeter: Double get() = (4 * side).toDouble()

    // ASCII art — hollow square: top/bottom = full row, middle = just edges
    override fun draw() {
        println("  ── Square (side = $side) ──")
        val row = ("* ".repeat(side)).trimEnd()
        println("  $row")                            // top edge
        repeat(side - 2) {
            val middle = "*" + "  ".repeat(side - 2) + "*"
            println("  $middle")                     // left + right walls
        }
        if (side > 1) println("  $row")              // bottom edge
    }

    override fun resize(factor: Double): Drawable =
        Square((side * factor).toInt(), color)

    companion object {
        fun small()  = Square(3)
        fun medium() = Square(5)
        fun large()  = Square(8)
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS CLASS — Triangle
//  Shows that ANY class can implement Drawable — not just shapes
// ══════════════════════════════════════════════════════════════════════════════

class Triangle(
    val base: Int,
    color: String = "white"
) : Shape("Triangle", color), Resizable {

    override val area:      Double get() = 0.5 * base * (base * sqrt(3.0) / 2.0)
    override val perimeter: Double get() = (3 * base).toDouble()

    // ASCII art — builds triangle top-down
    override fun draw() {
        println("  ── Triangle (base = $base) ──")
        for (i in 1..base) {
            val spaces = " ".repeat(base - i)
            val stars  = "* ".repeat(i).trimEnd()
            println("  $spaces$stars")
        }
    }

    override fun resize(factor: Double): Drawable =
        Triangle((base * factor).toInt(), color)
}


// ══════════════════════════════════════════════════════════════════════════════
//  BONUS CLASS — Rectangle (implements Drawable directly, no Shape base)
//  Demonstrates: a class can implement interface WITHOUT inheriting Shape
// ══════════════════════════════════════════════════════════════════════════════

class Rectangle(
    val width: Int,
    val height: Int
) : Drawable {

    override fun draw() {
        println("  ── Rectangle (${width}×${height}) ──")
        val topBottom = ("* ".repeat(width)).trimEnd()
        println("  $topBottom")
        repeat(height - 2) {
            val mid = "*" + "  ".repeat(width - 2) + "*"
            println("  $mid")
        }
        if (height > 1) println("  $topBottom")
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  MAIN FUNCTION
// ══════════════════════════════════════════════════════════════════════════════

fun main() {

    println("════════════════════════════════════════════")
    println("   SE 3242 — Class 02 Exercise 3: Shapes")
    println("════════════════════════════════════════════")
    println()

    // ── REQUIREMENT: Basic Circle and Square ──────────────────────────────────
    println("📋  REQUIRED OUTPUT:")
    println("────────────────────────────────────────────")

    val circle = Circle(radius = 3)
    val square = Square(side = 5)

    circle.draw()
    println()
    square.draw()
    println()

    // ── POLYMORPHISM: List<Drawable> holds all shapes ─────────────────────────
    println("────────────────────────────────────────────")
    println("🔷  POLYMORPHIC LIST — List<Drawable>:")
    println()

    // Polymorphism: List<Drawable> stores Circle, Square, Triangle, Rectangle
    val shapes: List<Drawable> = listOf(
        Circle(2),
        Square(4),
        Triangle(5),
        Rectangle(6, 3)
    )

    // forEach calls draw() on each — Kotlin picks the right implementation
    shapes.forEach { shape ->
        shape.draw()
        println()
    }

    // ── Shape info (only for Shape subclasses) ────────────────────────────────
    println("────────────────────────────────────────────")
    println("📐  SHAPE PROPERTIES:")
    println()

    val shapeObjects: List<Shape> = listOf(
        Circle(radius = 4, color = "red"),
        Square(side   = 5, color = "blue"),
        Triangle(base = 6, color = "green")
    )

    shapeObjects.forEach { shape ->
        shape.printInfo()
        println()
    }

    // ── describe() — interface default method ─────────────────────────────────
    println("────────────────────────────────────────────")
    println("💬  describe() — interface default method overridden:")
    println()
    shapes.forEach { it.describe() }
    println()

    // ── Resize demo (Resizable interface) ─────────────────────────────────────
    println("────────────────────────────────────────────")
    println("🔄  RESIZABLE INTERFACE DEMO:")
    println()

    val original = Circle(3)
    val scaled   = (original as Resizable).resize(2.0) as Circle

    println("  Original circle (radius ${original.radius}):")
    original.draw()
    println()
    println("  Scaled × 2.0 (radius ${scaled.radius}):")
    scaled.draw()
    println()

    // ── Statistics using higher-order functions ────────────────────────────────
    println("────────────────────────────────────────────")
    println("📊  SHAPE STATISTICS:")
    println()

    println("  %-14s  %6s  %10s  %10s"
        .format("Shape", "Size", "Area", "Perimeter"))
    println("  " + "-".repeat(46))

    shapeObjects.forEach { shape ->
        val sizeStr = when (shape) {
            is Circle   -> "r=${shape.radius}"
            is Square   -> "s=${shape.side}"
            is Triangle -> "b=${shape.base}"
            else        -> "?"
        }
        println("  %-14s  %6s  %10.2f  %10.2f"
            .format(shape.name, sizeStr, shape.area, shape.perimeter))
    }

    // Largest area using maxByOrNull (higher-order function)
    val largest = shapeObjects.maxByOrNull { it.area }
    println()
    println("  Largest area   : ${largest?.name} (${"%.2f".format(largest?.area)})")
    println("  Total area     : ${"%.2f".format(shapeObjects.sumOf { it.area })}")
    println("  Avg perimeter  : ${"%.2f".format(shapeObjects.map { it.perimeter }.average())}")

    // Sort by area using sortedBy
    println()
    println("  Sorted by area (ascending):")
    shapeObjects.sortedBy { it.area }
        .forEach { println("    ${it.name.padEnd(10)} area = ${"%.2f".format(it.area)}") }

    println()
    println("════════════════════════════════════════════")
    println("  ✅ interface Drawable    : draw()")
    println("  ✅ interface Resizable   : resize(factor)")
    println("  ✅ abstract class Shape  : shared state")
    println("  ✅ Circle, Square,")
    println("     Triangle, Rectangle  : implementations")
    println("  ✅ polymorphism          : List<Drawable>")
    println("  ✅ override              : mandatory keyword")
    println("  ✅ default interface fn  : describe()")
    println("════════════════════════════════════════════")
}