package chat

import chat.ChatMessageVisibility._
import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import user.UserFixture
import util.TestTimeProviderImpl

import scala.util.Success

class ChatMessageDAOUTest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  val timeProvider = new TestTimeProviderImpl()

  "Adding a message" should "succeed" in { session =>
    val dAO = makeDAO(session)
    val chatMessageWithVisibility = OutgoingChatMessageWithVisibility(
      ToClientChatMessage(
        uUIDProvider.randomUUID(), "alice", "bob", "some message", timeProvider.now().minusMillis(1).getMillis),
      Visible, Visible,
      id1,
      id3
    )
    val result = dAO.add(chatMessageWithVisibility, uUIDProvider.randomUUID(), timeProvider.now(), uUIDProvider.randomUUID())
    result shouldBe a[Success[_]]
  }

  "Adding sender visibility" should "succeed" in { session =>
    val dAO = makeDAO(session)
    val resultSender =
      dAO.addSenderVisibility(idMsgAliceBob3, timeProvider.now(), NotVisible, uUIDProvider.randomUUID())

    val resultReceiver =
      dAO.addReceiverVisibility(idMsgAliceBob3, timeProvider.now(), NotVisible, uUIDProvider.randomUUID())

    resultSender shouldBe a[Success[_]]
    resultReceiver shouldBe a[Success[_]]
  }

  "Retrieving messages for a user" should "return all messages that are visible to that user" in { session =>
    val dAO = makeDAO(session)

    val expectedMessages = Seq(
      OutgoingChatMessageWithVisibility(
        ToClientChatMessage(
          idMsgAliceBob1, "alice", "bob", "alice to bob one", dayBeforeYesterday.getMillis), Visible, Visible, id1, id3),
      OutgoingChatMessageWithVisibility(
        ToClientChatMessage(
          idMsgAliceBob2, "alice", "bob", "alice to bob two", dayBeforeYesterday.getMillis), Visible, NotVisible, id1, id3),
      OutgoingChatMessageWithVisibility(
          ToClientChatMessage(
         idMsgBobAlice3, "bob", "alice", "bob to alice three", dayBeforeYesterday.getMillis), NotVisible, Visible, id3, id1)
    )

    dAO.visibleMessages(id1) should contain theSameElementsAs expectedMessages
  }

  private def makeDAO(session:DBSession) =
    new ChatMessageDAOImpl(TestScalikeJDBCSessionProvider(session), dBConfig)

}
