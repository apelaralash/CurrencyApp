package ru.tatuna.mycurrency.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorView(onRetryClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Something went wrong. Please, retry.",
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(250.dp).padding(top = 256.dp)
        )
        Button(onClick = onRetryClick, modifier = Modifier.padding(top = 16.dp)) {
            Text("Retry")
        }
    }
}