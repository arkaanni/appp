import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf(arrayOf("Hello, World!", "-")) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            Button(onClick = {
                isLoading = true
                scope.launch {
                    val quote = withContext(Dispatchers.IO) { getQuotes() }
                    text = arrayOf("\"${quote[0]}\"", "- ${quote[1]}")
                    isLoading = false
                }
            }) {
                Text("Click me")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text[0], textAlign = TextAlign.Center, fontStyle = FontStyle.Italic)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text[1], fontStyle = FontStyle.Italic)
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

val objectMapper = ObjectMapper()
val httpClient: HttpClient = HttpClient.newHttpClient()
val request: HttpRequest = HttpRequest.newBuilder(URI.create("https://api.quotable.io/random"))
    .method("GET", BodyPublishers.noBody())
    .header("Accept", "application/json")
    .build()

fun getQuotes(): Array<String> {
    val request = httpClient.send(request, BodyHandlers.ofString())
    val json = objectMapper.readTree(request.body())
    return arrayOf(json["content"].asText(), json["author"].asText())
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
