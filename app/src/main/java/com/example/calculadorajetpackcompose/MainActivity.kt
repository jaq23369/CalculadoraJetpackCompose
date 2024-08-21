package com.example.calculadorajetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadorajetpackcompose.ui.theme.CalculadoraJetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraJetpackComposeTheme {
                CalculatorUI()
            }
        }
    }
}

@Composable
fun CalculatorUI() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        var displayValue by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = displayValue,
                onValueChange = {},
                readOnly = true,  // Hace el campo de texto solo de lectura
                textStyle = LocalTextStyle.current.copy(fontSize = 36.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                // Define los botones que quieres mostrar
                listOf("1", "2", "3", "+").forEach { label ->
                    Button(
                        onClick = { displayValue += label },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(text = label, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalculadoraJetpackComposeTheme {
        CalculatorUI()
    }
}