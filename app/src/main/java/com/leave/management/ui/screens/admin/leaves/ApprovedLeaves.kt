package com.leave.management.ui.screens.admin.leaves

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.leave.management.ui.screens.admin.LeaveApplication
import kotlinx.coroutines.launch


@Composable
fun ApprovedLeaveScreen(navController: NavHostController) {
    var approvedLeaves by remember { mutableStateOf<List<LeaveApplication>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") } // State to hold the search query
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchApprovedLeaves { leaves, error ->
                if (error == null) {
                    approvedLeaves = leaves ?: emptyList()
                } else {
                    Toast.makeText(context, "Error fetching approved leaves: ${error.message}", Toast.LENGTH_LONG).show()
                }
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 90.dp)
    ) {
        // Styled Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(24.dp)) // Background and corner shape
                .shadow(4.dp, RoundedCornerShape(24.dp)), // Add shadow for elevation
            placeholder = { Text(text = "Search by name or email", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray // Tint the search icon
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White, // Transparent background to show custom background
                focusedIndicatorColor = Color(0xFF6650a4), // Remove underline
                unfocusedIndicatorColor = Color(0xFF6650a4), // Remove underline
                cursorColor = Color(0xff6f2dc2), // Custom cursor color
                textColor = Color.Black // Custom text color
            )
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        } else {
            val filteredLeaves = approvedLeaves.filter { leave ->
                leave.applied_by.contains(searchQuery, ignoreCase = true) ||
                        leave.email.contains(searchQuery, ignoreCase = true)
            }

            if (filteredLeaves.isEmpty()) {
                Text(
                    text = "No approved leaves",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(filteredLeaves) { leave ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .shadow(4.dp, RoundedCornerShape(16.dp))
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        AsyncImage(
                                            model = leave.proofUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.Gray)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "By: ${leave.applied_by}, ${leave.email}",
                                            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Leave Type: ${leave.leaveType}",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "From Date: ${leave.fromDate}")
                                        Text(text = "To Date: ${leave.toDate}")
                                        Text(text = "Number of Days: ${leave.numberOfDays}")
                                        Text(text = "Reason: ${leave.reason}")
                                        Text(text = "Comment: ${leave.comment}")
                                        Text(text = "Served By: ${leave.handled_by}")
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .background(Color.Green, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Approved",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

private fun fetchApprovedLeaves(onResult: (List<LeaveApplication>?, Exception?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("leave_applications")
        .whereEqualTo("leaveStatus", "approved")
        .get()
        .addOnSuccessListener { result ->
            val leaves = result.documents.mapNotNull { document ->
                document.toObject(LeaveApplication::class.java)?.copy(id = document.id)
            }
            onResult(leaves, null)
        }
        .addOnFailureListener { exception ->
            onResult(null, exception)
        }
}
