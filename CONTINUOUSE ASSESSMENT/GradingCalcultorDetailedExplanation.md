# Student Grade Calculator – How it Works

This document walks through what the app does behind the scenes, how the grading logic is implemented, and how the new bulk‑upload feature operates. A real‑world analogy is used to make the behaviour easy to understand.

---
## 1. Core grading functionality
The heart of the app is a Kotlin function that converts a numeric mark (0–100) into a letter grade, GPA points and a remark.

### Code snippet
```kotlin
private data class GradeInfo(val grade: String, val points: Double, val remark: String)

private fun calculateGradeInfo(mark: Double): GradeInfo {
    return when {
        mark >= 86.0 -> GradeInfo("A", 4.0, "Excellent")
        mark >= 75.0 -> GradeInfo("B+", 3.5, "Very Good")
        mark >= 65.0 -> GradeInfo("B", 3.0, "Good")
        mark >= 60.0 -> GradeInfo("C+", 2.7, "Above Average")
        mark >= 50.0 -> GradeInfo("C", 2.3, "Average")
        mark >= 45.0 -> GradeInfo("D+", 2.0, "Below Average")
        mark >= 35.0 -> GradeInfo("D", 1.5, "Poor")
        mark >= 25.0 -> GradeInfo("E", 1.0, "Very Poor")
        else          -> GradeInfo("F", 0.0, "Fail")
    }
}
```

- **Input**: one `Double` value representing a student’s score.
- **Output**: a `GradeInfo` object bundling three pieces of information.
- The ordered `when` expression ensures only one branch matches: higher thresholds are checked first.

### Real‑world analogy
Imagine a coffee‑shop loyalty program. Customers accumulate points, and the program assigns them a tier based on thresholds:
- 1 000 + points → Gold
- 500‑999 → Silver
- 0‑499 → Bronze

`calculateGradeInfo` works the same way. The student’s mark is the “points”, and the returned `GradeInfo` is the “tier” plus some extra data (e.g. perks). If Alice has 78 marks, the function returns `GradeInfo("B+", 3.5, "Very Good")`, just as 780 points would map to Gold in the loyalty example.

### Using the function in the app
The UI handler `calculateGrade()` (in `MainActivity`) reads the text fields, validates the mark, calls `calculateGradeInfo(mark)`, and then:
1. Builds a formatted result string shown in a `TextView`.
2. Calls `exportCsv(...)` to persist the record.

Validation ensures that marks outside 0–100 or non‑numbers are rejected with a toast message.

## 2. Persistent storage (CSV export)
Each time a grade is calculated, the app appends a line to a CSV file saved under the app’s private `filesDir`:

```kotlin
private fun exportCsv(name: String, ..., mark: Double, grade: String) {
    val file = File(filesDir, "grades_${System.currentTimeMillis()}.csv")
    if (!file.exists()) {
        file.writeText("Name,Matricule,Major,Course,Mark,Grade\n")
    }
    val line = "\"$name\",\"$matricule\",...\n"
    file.appendText(line)
}
```

> **Note:** there’s no database; the CSV files are plain text and are stored in internal storage. You can pull them using `adb` or add code to export them (see below).

## 3. Bulk upload feature (CSV/Excel)
To make grading large classes easier, the app now lets a teacher upload a spreadsheet containing many students’ data. The new UI elements are placed in a card above the manual form:

```xml
<!-- Upload / Template Card -->
<androidx.cardview.widget.CardView ...>
    <LinearLayout ...>
        <Button
            android:id="@+id/btnUploadFile" ...
            android:text="Upload CSV" />
        <Button
            android:id="@+id/btnDownloadTemplate" ...
            android:text="Download Template" />
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

### How it works
1. **Picking the file** – pressing **Upload CSV** opens the Android file chooser (`ACTION_GET_CONTENT`), filtering for CSV/XLS files.
2. **Processing** – after selection the URI is passed to `handleBulkFile(uri)`:
   - The input stream is read line‑by‑line.
   - The first line is treated as the header; subsequent lines are treated as rows.
   - Each row is split on commas, the mark parsed, and `calculateGradeInfo` called.
   - The output is a new CSV text where the original header has three extra columns (`Grade,Points,Remark`), and each row is extended accordingly.
3. **Saving and sharing** – `saveBulkCsv` writes the new CSV to `filesDir` and then uses a `FileProvider` to launch a share intent so the teacher can send the graded file to email or open it in a spreadsheet app.

```kotlin
private fun handleBulkFile(uri: Uri) { ... }
private fun saveBulkCsv(text: String) { ... }
```

### Template
Clicking **Download Template** writes a small CSV with just the header (`Name,Matricule,Major,Course,Mark`). The teacher can open this template, fill in many student records, and then re‑upload it.

### Example scenario
A teacher has 120 students and their marks stored in `class1.csv`:
```
Name,Matricule,Major,Course,Mark
Alice,FE21A001,Software,Math,78
Bob,FE21A002,Networking,OS,54
...
```
She taps **Upload CSV**, selects the file, and the app quickly produces `grades_bulk_1678452….csv` containing:
```
Name,Matricule,Major,Course,Mark,Grade,Points,Remark
Alice,FE21A001,Software,Math,78,B+,3.5,Very Good
Bob,FE21A002,Networking,OS,54,C,2.3,Average
...
```
The file is then offered via a share sheet so it can be saved or emailed.

## 4. UI/UX improvements
- A separate card for bulk operations makes the layout look more professional and separates one‑off uploads from manual entry.
- Buttons use Material components and are aligned horizontally with consistent padding.
- The entire form remains inside a scrollable `ScrollView` so it works on smaller screens.
- A `CardView` around the upload area mirrors the cards used elsewhere (title, form, result), giving a cohesive “dashboard” feel similar to modern Android apps.

## 5. Extensibility tips
- **Change grading thresholds** by editing `calculateGradeInfo`.
- **Support real Excel files** by adding Apache POI and expanding `handleBulkFile` to distinguish `.xlsx`/`.xls` URIs and parse with POI.
- **Persistent storage** can be upgraded to Room or SQLite if CSV files become unwieldy.
- **UI polish**: add icons (e.g. `android:drawableLeft`), use `ConstraintLayout` for more responsive forms, and show snackbar messages instead of toasts.

## 6. File provider configuration
To allow sharing the generated CSV, a `FileProvider` entry was added to `AndroidManifest.xml` and a simple `res/xml/file_paths.xml`:
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```
The paths file declares that all files in `filesDir` are shareable.

---
This document completes the explanation of how the grade calculator works and how bulk uploads are processed. You can keep it in the project root as `GradingCalcultorDetailedExplanation.md` for future reference.