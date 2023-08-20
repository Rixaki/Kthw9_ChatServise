import Chat.Companion.idInChat

class ChatService {
    private var chats = mutableListOf<Chat>()

    companion object { //static count
        @JvmField
        var idInService: Int = 1
    }

    init {
        idInService = 1
    }


    fun addMessage(
        idUserFrom: Int,
        idUserTo: Int,
        text: String
    ): Int {
        val pair = leftLowRightHigh(idUserFrom, idUserTo)
        val predicate = fun(chat: Chat) = ((chat.idUser1 == pair.first) &&
                (chat.idUser2 == pair.second))
        if (chats.find(predicate) == null) {
            val chat = Chat(pair.first, pair.second, idInService)
            idInService += 1
            chats += chat

            val message = Message(idUserFrom, idInChat, isDeleted = false, isReaded = false, text = text)
            chat.addMessage(message)
            idInChat += 1

            return 0
        } else {
            //val chat: Chat = chats.filter(predicate)[0]
            val chat: Chat = chats.find(predicate)!!
            val message = Message(idUserFrom, idInChat, isDeleted = false, isReaded = false, text = text)
            chat.addMessage(message)
            idInChat += 1

            return 1
        }
    }

    fun printChatByUsers( //without sequence-update due to only-value separator alg
        idUser1: Int,
        idUser2: Int,
    ): Int {
        val pair = leftLowRightHigh(idUser1, idUser2)
        val predicate = fun(chat: Chat) = ((chat.idUser1 == pair.first) &&
                (chat.idUser2 == pair.second))
        val sb: StringBuilder = StringBuilder()
        sb.append("Chat of id$idUser1 and id$idUser2")
        if (chats.none(predicate)) {
            sb.append(":" + "\n" + "<no chat>")
            println(sb)
            return -1
        } else {
            val chat: Chat = chats.find(predicate)!!
            sb.append(" (chat #${chat.chatId}):")
            println(sb)
            println(chat.printMessages())
            //println()
            return 0
        }
    }

    fun getChat(
        idChatSearch: Int
    ): Chat {
        val predicate = fun(chat: Chat) = (chat.chatId == idChatSearch)
        if (chats.none(predicate)) {
            throw RuntimeException("No search result with id#$idChatSearch, service have ${chats.size} chats with id#${chats[0].chatId} and users: ${chats[0].idUser1}, ${chats[0].idUser2}.")
        } else {
            return chats.find(predicate)!!
        }
    }

    fun printChatByFlags( //printMessages was updated
        idChatSearch: Int,
        idMsgSearch: Int = -1,
        countMsg: Int = -1
    ) {
        val predicate = fun(chat: Chat) = (chat.chatId == idChatSearch)
        if (chats.none(predicate)) {
            throw RuntimeException("No search result with id#$idChatSearch.")
        } else {
            val chat: Chat = chats.find(predicate)!!
            chat.printMessages(idMsgSearch, countMsg)
        }
    }

    fun getChatByFlags(
        idChatSearch: Int,
        idMsgSearch: Int = -1,
        countMsg: Int = -1
    ): MutableList<Message>? {
        val predicate = fun(chat: Chat) = (chat.chatId == idChatSearch)
        if (chats.none(predicate)) {
            throw RuntimeException("No search result with id#$idChatSearch, service have ${chats.size} chats with id#${chats[0].chatId} and users: ${chats[0].idUser1}, ${chats[0].idUser2}.")
        } else {
            val chat: Chat = chats.find(predicate)!!
            return chat.toReadedStatusMessages(idMsgSearch, countMsg)
        }
    }

    fun getChats(): MutableList<Chat> {
        val resultList: MutableList<Chat> = mutableListOf<Chat>()
        for (chat in chats) {
            resultList += chat
        }
        return resultList
    }

    /*
    fun printChats() {
        val sb: StringBuilder = StringBuilder()
        for (chat in chats) {
            sb.append("Chat #${chat.chatId}, users: id#${chat.idUser1} and id#${chat.idUser2}" + "\n")
        }
        print(sb)
    }
    */

    fun getChatsWithMsg(): MutableList<Chat> {
        val resultList: MutableList<Chat> = mutableListOf<Chat>()
        for (chat in chats) {
            resultList += chat
        }
        return resultList
    }

    /*
    fun printChatsWithMsg() {
        val sb: StringBuilder = StringBuilder()
        for (chat in chats) {
            sb.append("Chat #${chat.chatId}, users: id#${chat.idUser1} and id#${chat.idUser2}" + "\n")
            sb.append("last message (chat #${chat.chatId}): ")
            print(sb)
            sb.clear()
            sb.append(chat.lastMessageToString())
            sb.append("\n")
            sb.clear()
        }
        print(sb)
    }
    */

    fun printChatsWithMsg() { //updated
        println("Chat list:")
        val chatsDigest = chats
            .joinToString(separator = "\n") { "Chat #${it.chatId} (users #${it.idUser1} and " +
                    "#${it.idUser2}): " + it.lastMessageToString() }
            .ifEmpty {"No chats."}
        println(chatsDigest)
    }

    fun deleteMessage(
        idUser1: Int,
        idUser2: Int,
        idMessage: Int
    ): Int {
        val pair = leftLowRightHigh(idUser1, idUser2)
        val predicate = fun(chat: Chat) = ((chat.idUser1 == pair.first) &&
                (chat.idUser2 == pair.second))
        if (chats.find(predicate) == null) {
            return -1
        } else {
            //val chat: Chat = chats.filter(predicate)[0]
            val chat: Chat = chats.find(predicate)!!
            if (chat.getMessageById(idMessage) == null) {
                return 0
            } else {
                chat.deleteMessage(idMessage)
                return 1
            }
        }
    }


    fun deleteChat(
        idUser1: Int = 0,
        idUser2: Int = 0
    ): Int {
        val pair = leftLowRightHigh(idUser1, idUser2)
        val predicate = fun(chat: Chat) = ((chat.idUser1 == pair.first) &&
                (chat.idUser2 == pair.second))
        if (chats.find(predicate) == null) {
            return -1
        } else {
            val chat: Chat = chats.find(predicate)!!
            chats.remove(chat)
            return 1
        }
    }

    fun getUnreadChats(): MutableList<Chat>? {
        return chats.asSequence()
            .filter { it.isHaveUnread() }
            .ifEmpty { emptySequence() }
            .toMutableList()
    }

    fun getUnreadChatsPrint() { //updates with sequences
        println("Unreaded chats:")
        val unreadDigest = chats.asSequence()
            .filter { it.isHaveUnread() }
            .joinToString(separator = "\n" ) { "Chat #${it.chatId}: " + it.lastMessageToString() }
            .ifEmpty {"All chats read."}
        println(unreadDigest)
    }
}

data class Chat(
    val idUser1: Int = 0,
    val idUser2: Int = 0,
    val chatId: Int = 0
) { //leftId <= rightId

    private var chatWall = mutableListOf<Message>()

    init {
        idInChat = 1
    }

    companion object { //static count
        @JvmField
        var idInChat: Int = 1
    }

    fun addMessage(msg: Message) {
        chatWall += msg
    }

    fun deleteMessage(idMessageSearch: Int): Int { //rewrote
        val predicate = fun(msg: Message): Boolean { return msg.idMessage == idMessageSearch }
        if (chatWall.find(predicate) == null) {
            println("Attention to delete unpredictable message")
            return -1
        } else {
            //val message: Message = chatWall.find(predicate)!!
            //message.isDeleted = true
            chatWall
                .find(predicate)!!
                .isDeleted = true
            return 1
        }
    }

    fun printMessages( //updated with sequences
        idMsgSearch: Int = -1,
        printCounterFlag: Int = -1
    ): String {
        //val sb: StringBuilder = StringBuilder()
        var filteredChat: MutableList<Message>? = null
        if (idMsgSearch == -1) {
            filteredChat = chatWall
        } else {
            val messageSearch: Message = chatWall.find { it.idMessage == idMsgSearch }
                ?: throw RuntimeException("message #$idMsgSearch was not founded")
            if (printCounterFlag == -1) {
                filteredChat = chatWall.asSequence()
                    .filter { it.idMessage >= idMsgSearch }
                    .toMutableList()
            } else {
                filteredChat = chatWall.asSequence()
                    .filter { it.idMessage >= idMsgSearch }
                    .take(printCounterFlag) //save filter without OutOfBounds
                    .toMutableList()
            }
        }
        /*
        for (message in filteredChat) {
            if (!message.isDeleted) {
                sb.append("idUser${message.idAuthor} (#${message.idMessage}) - ${message.text}" + "\n")
                message.isReaded = true
            } else {
                sb.append("<deleted>" + "\n")
                message.isReaded = true
            }
            println("Unreaded chats:")
            val chatText = chats.asSequence()
                .filter { it.isHaveUnread() }
                .joinToString(separator = "\n" ) { "Chat #${it.chatId}: " + it.lastMessageToString() }
                .ifEmpty {"All chats read."}
            println(unreadDigest)
        }
        */
        filteredChat.forEach { if (it.isDeleted) it.text = "<deleted>" else it.text}
        val resultMessage = filteredChat
            .fold(Message(text="")){
                acc :Message, message: Message -> acc.
                    copy(text = (acc.text + "user#${message.idAuthor} (msg#${message.idMessage}) - ${message.text}" + "\n"))
            }
        filteredChat.forEach { it.isReaded = true }
        return resultMessage.text
    }

    fun toReadedStatusMessages( //like printMessages, but without print and return filteredChat
        idMsgSearch: Int = -1,
        printCounterFlag: Int = -1
    ): MutableList<Message>? {
        var filteredChat: MutableList<Message>? = null
        if (idMsgSearch == -1) {
            filteredChat = chatWall
        } else {
            val messageSearch: Message = chatWall.find { it.idMessage == idMsgSearch }
                ?: throw RuntimeException("message #$idMsgSearch was not founded")
            if (printCounterFlag == -1) {
                filteredChat = chatWall.asSequence()
                    .filter { it.idMessage >= idMsgSearch }
                    .toMutableList()
            } else {
                filteredChat = chatWall.asSequence()
                    .filter { it.idMessage >= idMsgSearch }
                    .take(printCounterFlag) //save filter without OutOfBounds
                    .toMutableList()
            }
        }
        filteredChat.forEach { it.isReaded = true }
        return filteredChat
    }

    fun getLastMessage(): Message {
        return chatWall.last()
    }

    fun lastMessageToString(): String {
        val lastMsg = chatWall.last()
        return ("idUser${lastMsg.idAuthor} (msg #${lastMsg.idMessage}) - ${lastMsg.text}")
    }

    fun getMessageById(id: Int): Message? {
        return chatWall.find { it.idMessage == id }
    }

    fun isHaveUnread(): Boolean {
        return (chatWall.find { !it.isReaded } != null)
    }

    fun isEmpty(): Boolean {
        return chatWall.isEmpty()
    }
}

data class Message(
    val idAuthor: Int = 0,
    val idMessage: Int = 0,
    var isDeleted: Boolean = false,
    var isReaded: Boolean = false,
    var text: String = "none text"
) {
    fun printMessage() {
        println("idUser$idAuthor (msg #$idMessage) - $text")
    }
}

fun leftLowRightHigh(arg1: Int, arg2: Int): Pair<Int, Int> {
    var idLeft: Int = 0
    var idRight: Int = 0
    if (arg1 <= arg2) {
        return Pair(arg1, arg2)
    } else {
        return Pair(arg2, arg1)
    }
}
