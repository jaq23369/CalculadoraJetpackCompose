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
                readOnly = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 36.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            // Añadir múltiples filas para cubrir todos los botones
            val rows = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("0", "^", "=", "+"),
                listOf("√", "(", ")", "e^"),
                listOf("RESET")
            )
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { label ->
                        Button(
                            onClick = { handleButtonClick(label, displayValue) { newValue -> displayValue = newValue } },
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
}

// Función para manejar los clics de los botones
fun handleButtonClick(label: String, currentValue: String, updateDisplay: (String) -> Unit) {
    when (label) {
        "=" -> {
            val result = try { eval(currentValue).toString() } catch (e: Exception) { "Error" }
            updateDisplay(result)
        }
        "RESET" -> updateDisplay("0")
        else -> {
            val newText = if (currentValue == "0" && label != ".") label else currentValue + label
            updateDisplay(newText)
        }
    }
}

fun eval(str: String): Double {
    return object {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < str.length) str[pos].toInt() else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.toInt()) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.toInt()) -> x += parseTerm()
                    eat('-'.toInt()) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.toInt()) -> x *= parseFactor()
                    eat('/'.toInt()) -> {
                        val denom = parseFactor()
                        if (denom == 0.0) throw RuntimeException("Division by zero")
                        x /= denom
                    }
                    else -> return x
                }
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.toInt())) return parseFactor()  // unary plus
            if (eat('-'.toInt())) return -parseFactor()  // unary minus

            var x: Double
            val startPos = pos

            if (eat('√'.toInt())) {  // square root
                x = Math.sqrt(parseFactor())
            } else if (eat('(' .toInt())) {  // parentheses
                x = parseExpression()
                if (!eat(')'.toInt())) throw RuntimeException("Closing parenthesis expected")
            } else if ((ch >= '0'.toInt() && ch <= '9'.toInt()) || ch == '.'.toInt()) {  // numbers
                while ((ch >= '0'.toInt() && ch <= '9'.toInt()) || ch == '.'.toInt()) nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else if (eat('e'.toInt())) {  // exponential function 'e^'
                if (eat('^'.toInt())) {
                    x = Math.exp(parseFactor())
                } else {
                    x = Math.E  // Euler's number
                }
            } else {
                throw RuntimeException("Unexpected: " + (ch.toChar()))
            }

            // Post-processing for exponentiation
            if (eat('^'.toInt())) {
                x = Math.pow(x, parseFactor())
            }

            return x
        }
        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected: " + (ch.toChar()))
            return x
        }

    }.parse()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalculadoraJetpackComposeTheme {
        CalculatorUI()
    }
}