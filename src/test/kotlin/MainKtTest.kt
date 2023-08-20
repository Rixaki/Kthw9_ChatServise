import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainKtTest {
    @Test
    fun addFirstMessageTest() {
        val service = ChatService()
        println("addFirstMessageTest")
        service.printChatsWithMsg()
        assertTrue(service.addMessage(1, 2, "Hello, 2") == 0)
    }

    @Test
    fun addAnotherMessageTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        println("addAnotherMessageTest")
        service.printChatsWithMsg()
        assertTrue(service.addMessage(1, 2, "Hello, again!") == 1)
    }

    @Test //idChat+1
    fun printValidChatTest() {
        val service = ChatService()
        service.addMessage(10, 11, "Hello, 11")
        service.addMessage(11, 10, "Hello, 10")
        println("printValidChatTest")
        service.printChatsWithMsg()
        assertTrue(service.printChatByUsers(10, 11) == 0)
    }

    @Test
    fun printInvalidChatTest() {
        val service = ChatService()
        service.addMessage(10, 11, "Hello, 11")
        service.addMessage(11, 10, "Hello, 10")
        println("printInvalidChatTest")
        service.printChatsWithMsg()
        assertTrue(service.printChatByUsers(10, 1) == -1)
    }

    @Test
    fun getValidChatTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        println("getValidChatTest")
        service.printChatsWithMsg()
        assertFalse(service.getChat(1).isEmpty())
    }

    @Test(expected = RuntimeException::class)
    fun getInvalidChatByChatIdTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        println("getInvalidChatByChatIdTest")
        service.printChatsWithMsg()
        service.getChat(2).isEmpty()
    }

    @Test
    fun getChatsWithMsgTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        service.addMessage(10, 11, "Hello, 11")
        service.addMessage(11, 10, "Hello, 10")
        println("getChatsWithMsgTest")
        service.printChatsWithMsg()
        assertFalse(service.getChatsWithMsg().isEmpty())
    }

    @Test
    fun deleteMsgTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        println("deleteMsgTest")
        service.printChatsWithMsg()
        assertTrue(
            ((service.deleteMessage(2, 1, 2) == 1) &&
                    (service.getChat(1).getMessageById(2)?.isDeleted == true))
        )
    }

    @Test //idChat+1
    fun invalidDeleteMsgByMsdIdTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        println("invalidDeleteMsgByMsdIdTest")
        service.printChatsWithMsg()
        assertTrue(service.deleteMessage(2, 1, 12) == 0)
    }

    @Test
    fun invalidDeleteMsgByChatIdTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        println(service.deleteMessage(2, 1, 12))
        println("invalidDeleteMsgByChatIdTest")
        service.printChatsWithMsg()
        assertTrue(service.deleteMessage(4, 5, 1) == -1)
    }

    @Test //idChat+3
    fun deleteChatTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        service.addMessage(10, 11, "Hello, 11")
        service.addMessage(11, 10, "Hello, 10")
        service.addMessage(10, 12, "12, who are you?")
        println("deleteChatTest")
        service.printChatsWithMsg()
        assertTrue((service.deleteChat(10, 11) == 1) &&
                (service.getChats().find { it.idUser1 == 10 && it.idUser2 == 11 } == null))
    }

    @Test
    fun invalidDeleteChatTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        service.addMessage(10, 11, "Hello, 11")
        service.addMessage(11, 10, "Hello, 10")
        service.addMessage(10, 12, "12, who are you?")
        println("invalidDeleteChatTest")
        service.printChatsWithMsg()
        assertTrue(
            (service.deleteChat(10, 4) == -1) &&
                    (service.getChats().size == 3)
        )
    }

    @Test
    fun unreadChatsCountTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        service.addMessage(10, 11, "Hello, 11")
        service.addMessage(11, 10, "Hello, 10")
        service.printChatByUsers(2, 1)
        service.printChatByUsers(10, 11)
        service.getChatsWithMsg()
        service.addMessage(1, 3, "unreaded")
        service.addMessage(10, 12, "12, who are you?")
        println("unreadChatsCountTest")
        service.printChatsWithMsg()
        assertTrue(service.getUnreadChats()!!.size == 2)
    }

    @Test
    fun noneUnreadChatsCountTest() {
        val service = ChatService()
        service.addMessage(1, 2, "Hello, 2")
        service.addMessage(1, 2, "Hello, again!")
        service.addMessage(2, 1, "Hello, 1!")
        service.addMessage(10, 11, "Hello, 11")
        service.addMessage(11, 10, "Hello, 10")
        service.printChatByUsers(2, 1)
        service.printChatByUsers(10, 11)
        println("noneUnreadChatsCountTest")
        service.printChatsWithMsg()
        assertTrue(service.getUnreadChats().isNullOrEmpty())
    }

}