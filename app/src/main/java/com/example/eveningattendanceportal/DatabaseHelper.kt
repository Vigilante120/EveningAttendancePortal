import android.content.ContentValues
import android.content.Context
import com.example.eveningattendanceportal.models.db.Student
import com.example.eveningattendanceportal.models.db.Teacher
import com.example.eveningattendanceportal.Attendance
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "attendance_portal.db"
        private const val DATABASE_VERSION = 1

        // Table Names
        private const val TABLE_STUDENTS = "Students"
        private const val TABLE_TEACHERS = "Teachers"
        private const val TABLE_ATTENDANCE = "Attendance"

        // Students Table Columns
        private const val KEY_ROLL_NUMBER = "RollNumber"
        private const val KEY_STUDENT_NAME = "Name"
        private const val KEY_STUDENT_CLASS = "Class"

        // Teachers Table Columns
        private const val KEY_TEACHER_ID = "TeacherId"
        private const val KEY_TEACHER_NAME = "Name"
        private const val KEY_TEACHER_SUBJECT = "Subject"

        // Attendance Table Columns
        private const val KEY_ATTENDANCE_DATE = "Date"
        private const val KEY_ATTENDANCE_STATUS = "Status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_STUDENTS_TABLE = """
            CREATE TABLE $TABLE_STUDENTS (
                $KEY_ROLL_NUMBER TEXT PRIMARY KEY,
                $KEY_STUDENT_NAME TEXT NOT NULL,
                $KEY_STUDENT_CLASS TEXT NOT NULL
            )""".trimIndent()

        val CREATE_TEACHERS_TABLE = """
            CREATE TABLE $TABLE_TEACHERS (
                $KEY_TEACHER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_TEACHER_NAME TEXT NOT NULL,
                $KEY_TEACHER_SUBJECT TEXT NOT NULL
            )""".trimIndent()

        val CREATE_ATTENDANCE_TABLE = """
            CREATE TABLE $TABLE_ATTENDANCE (
                $KEY_ROLL_NUMBER TEXT,
                $KEY_ATTENDANCE_DATE TEXT,
                $KEY_ATTENDANCE_STATUS TEXT NOT NULL,
                PRIMARY KEY ($KEY_ROLL_NUMBER, $KEY_ATTENDANCE_DATE),
                FOREIGN KEY ($KEY_ROLL_NUMBER) REFERENCES $TABLE_STUDENTS($KEY_ROLL_NUMBER)
            )""".trimIndent()

        db.execSQL(CREATE_STUDENTS_TABLE)
        db.execSQL(CREATE_TEACHERS_TABLE)
        db.execSQL(CREATE_ATTENDANCE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ATTENDANCE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TEACHERS")
        onCreate(db)
    }

    // CRUD Operations for Students
    fun addStudent(rollNumber: String, name: String, className: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_ROLL_NUMBER, rollNumber)
            put(KEY_STUDENT_NAME, name)
            put(KEY_STUDENT_CLASS, className)
        }
        return db.insert(TABLE_STUDENTS, null, values)
    }

    fun getAllStudents(): List<Student> {
        val students = mutableListOf<Student>()
        val query = "SELECT * FROM $TABLE_STUDENTS ORDER BY $KEY_STUDENT_NAME"
        val cursor = readableDatabase.rawQuery(query, null)

        cursor?.use {
            while (it.moveToNext()) {
                students.add(
                    Student(
                        rollNumber = it.getString(it.getColumnIndexOrThrow(KEY_ROLL_NUMBER)),
                        name = it.getString(it.getColumnIndexOrThrow(KEY_STUDENT_NAME)),
                        className = it.getString(it.getColumnIndexOrThrow(KEY_STUDENT_CLASS))
                    )
                )
            }
        }
        return students
    }

    // CRUD Operations for Teachers
    fun addTeacher(name: String, subject: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_TEACHER_NAME, name)
            put(KEY_TEACHER_SUBJECT, subject)
        }
        return db.insert(TABLE_TEACHERS, null, values)
    }

    fun getAllTeachers(): List<Teacher> {
        val teachers = mutableListOf<Teacher>()
        val query = "SELECT * FROM $TABLE_TEACHERS ORDER BY $KEY_TEACHER_NAME"
        val cursor = readableDatabase.rawQuery(query, null)

        cursor?.use {
            while (it.moveToNext()) {
                teachers.add(
                    Teacher(
                        id = it.getInt(it.getColumnIndexOrThrow(KEY_TEACHER_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(KEY_TEACHER_NAME)),
                        subject = it.getString(it.getColumnIndexOrThrow(KEY_TEACHER_SUBJECT))
                    )
                )
            }
        }
        return teachers
    }

    // Attendance Operations
    fun markAttendance(rollNumber: String, date: String, status: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_ROLL_NUMBER, rollNumber)
            put(KEY_ATTENDANCE_DATE, date)
            put(KEY_ATTENDANCE_STATUS, status)
        }

        return try {
            db.insertWithOnConflict(
                TABLE_ATTENDANCE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        } finally {
            db.close()
        }
    }

    fun getAttendanceDates(): List<String> {
        val dates = mutableListOf<String>()
        val query = "SELECT DISTINCT $KEY_ATTENDANCE_DATE FROM $TABLE_ATTENDANCE ORDER BY $KEY_ATTENDANCE_DATE DESC"
        val cursor = readableDatabase.rawQuery(query, null)

        cursor?.use {
            while (it.moveToNext()) {
                dates.add(it.getString(it.getColumnIndexOrThrow(KEY_ATTENDANCE_DATE)))
            }
        }
        return dates
    }

    fun getAttendanceByDate(date: String): List<Attendance> {
        val attendanceList = mutableListOf<Attendance>()
        val query = """
            SELECT a.$KEY_ROLL_NUMBER, s.$KEY_STUDENT_NAME, a.$KEY_ATTENDANCE_STATUS 
            FROM $TABLE_ATTENDANCE a 
            INNER JOIN $TABLE_STUDENTS s ON a.$KEY_ROLL_NUMBER = s.$KEY_ROLL_NUMBER 
            WHERE a.$KEY_ATTENDANCE_DATE = ?
            ORDER BY s.$KEY_STUDENT_NAME
        """.trimIndent()

        val cursor = readableDatabase.rawQuery(query, arrayOf(date))

        cursor?.use {
            while (it.moveToNext()) {
                attendanceList.add(
                    Attendance(
                        rollNumber = it.getString(it.getColumnIndexOrThrow(KEY_ROLL_NUMBER)),
                        date = date,
                        status = it.getString(it.getColumnIndexOrThrow(KEY_ATTENDANCE_STATUS)),
                        studentName = it.getString(it.getColumnIndexOrThrow(KEY_STUDENT_NAME))
                    )
                )
            }
        }
        return attendanceList
    }

    // Validation methods
    fun studentExists(rollNumber: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_STUDENTS,
            arrayOf(KEY_ROLL_NUMBER),
            "$KEY_ROLL_NUMBER = ?",
            arrayOf(rollNumber),
            null, null, null
        )
        return cursor.use { it.count > 0 }
    }

    fun teacherExists(name: String, subject: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TEACHERS,
            arrayOf(KEY_TEACHER_ID),
            "$KEY_TEACHER_NAME = ? AND $KEY_TEACHER_SUBJECT = ?",
            arrayOf(name, subject),
            null, null, null
        )
        return cursor.use { it.count > 0 }
    }
}

