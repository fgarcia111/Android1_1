package com.example.a11

import android.os.Build
import android.os.Bundle
import android.os.TestLooperManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a11.ui.theme._11Theme
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _11Theme {
                    MainView()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainView(modifier: Modifier = Modifier) {
    var dia by remember { mutableStateOf(TextFieldValue("")) }
    var mes by remember { mutableStateOf(TextFieldValue("")) }
    var any by remember { mutableStateOf(TextFieldValue("")) }
    var errorNum = stringResource(id = R.string.error_num)
    var resultText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    val futur = stringResource(id = R.string.futur)
    val passat = stringResource(R.string.passat)
    val dies = stringResource(R.string.dies)
    val anys = stringResource(R.string.anys)
    val mesos = stringResource(R.string.mesosy)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > 600.dp // Umbral para detectar orientación horizontal

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.Title),
                modifier = Modifier.padding(bottom = if (isLandscape) 16.dp else 100.dp),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )

            Text(
                text = stringResource(id = R.string.introduceData),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            if (isLandscape) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InputField(label = stringResource(R.string.Dia), value = dia, onValueChange = { dia = it })
                    InputField(label = stringResource(R.string.Mes), value = mes, onValueChange = { mes = it })
                    InputField(label = stringResource(R.string.Any), value = any, onValueChange = { any = it })
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InputField(label = stringResource(R.string.Dia), value = dia, onValueChange = { dia = it })
                    InputField(label = stringResource(R.string.Mes), value = mes, onValueChange = { mes = it })
                    InputField(label = stringResource(R.string.Any), value = any, onValueChange = { any = it })
                }
            }
            if (errorText.isNotEmpty()) {
                Text(text = errorText, color = Color.Red, modifier = Modifier.padding(top = 20.dp))
            } else if (resultText.isNotEmpty()) {
                Text(text = resultText, modifier = Modifier.padding(top = 20.dp))
            }
            Button(
                onClick = {
                    try {
                        val day = dia.text.toInt()
                        val month = mes.text.toInt()
                        val year = any.text.toInt()

                        val result = calculateElapsedTime(day, month, year, futur, passat, dies, anys, mesos)
                        resultText = result
                        errorText = ""
                    } catch (e: NumberFormatException) {
                        errorText = errorNum
                        resultText = ""
                    } catch (e: Exception) {
                        errorText = "Error: ${e.message}"
                        resultText = ""
                    }
                },
                modifier = Modifier
                    .padding(top = 30.dp)
                    .width(150.dp)
                    .height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.Calcular))
                Image(
                    painter = painterResource(id = R.drawable.conversor),
                    contentDescription = "Imatge Logo",
                    modifier = Modifier.size(50.dp)
                )
            }
            Text(text = stringResource(id = R.string.creator), modifier = Modifier.padding(top = 20.dp))
        }
    }
}

//Així puc ficar label als text fields
@Composable
fun InputField(label: String, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.width(100.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateElapsedTime(day: Int, month: Int, year: Int, futur: String, passat: String, dies: String, anys: String, mesos: String): String {
    val inputDate = LocalDate.of(year, month, day)
    val currentDate = LocalDate.now()

    if (inputDate.isAfter(currentDate)) {
        throw IllegalArgumentException(futur)
    }

    val years = ChronoUnit.YEARS.between(inputDate, currentDate)
    val tempDate = inputDate.plusYears(years)

    val months = ChronoUnit.MONTHS.between(tempDate, currentDate)
    val tempDateWithMonths = tempDate.plusMonths(months)

    val days = ChronoUnit.DAYS.between(tempDateWithMonths, currentDate)

    return "$passat $years $anys, $months $mesos $days $dies"
}
