package CS2API

data class ChatMessage(
    var message: String = "",
    var sender: String? = "",
    var timestamp: Long = System.currentTimeMillis()
)
