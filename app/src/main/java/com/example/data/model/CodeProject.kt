package com.example.data.model

import java.util.UUID

data class CodeProject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val files: Map<String, String>, // e.g. "index.html" -> content, "style.css" -> content, "script.js" -> content
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getCombinedHtml(): String {
        val html = files["index.html"] ?: "<html><body><h1>No HTML content</h1></body></html>"
        val css = files["style.css"] ?: ""
        val js = files["script.js"] ?: ""

        return buildString {
            if (html.contains("</head>", ignoreCase = true)) {
                val headInsert = "<style>\n$css\n</style>\n</head>"
                val withCss = html.replaceFirst("</head>", headInsert, ignoreCase = true)
                if (withCss.contains("</body>", ignoreCase = true)) {
                    append(withCss.replaceFirst("</body>", "<script>\n$js\n</script>\n</body>", ignoreCase = true))
                } else {
                    append(withCss)
                    append("<script>\n$js\n</script>")
                }
            } else {
                append("<!DOCTYPE html><html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><style>\n")
                append(css)
                append("\n</style></head><body>\n")
                append(html)
                append("\n<script>\n")
                append(js)
                append("\n</script></body></html>")
            }
        }
    }
}
