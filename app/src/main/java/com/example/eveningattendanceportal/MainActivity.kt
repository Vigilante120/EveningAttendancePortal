package com.example.eveningattendanceportal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eveningattendanceportal.ui.theme.EveningAttendancePortalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EveningAttendancePortalTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to Evening Attendance Portal!", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* TODO: Navigate to Take Attendance */ }) {
                Text("Take Attendance")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* TODO: Navigate to Add Teacher */ }) {
                Text("Add Teacher")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* TODO: Navigate to Add Student */ }) {
                Text("Add Student")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* TODO: Navigate to View Students */ }) {
                Text("View Students")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* TODO: Navigate to View Teachers */ }) {
                Text("View Teachers")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* TODO: Navigate to View Attendance */ }) {
                Text("View Attendance")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    EveningAttendancePortalTheme {
        HomeScreen()
    }
}
