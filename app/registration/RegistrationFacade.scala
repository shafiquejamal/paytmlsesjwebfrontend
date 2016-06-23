package registration

import java.util.UUID

import com.google.inject.Inject
import entity.User
import user.{UserDAO, UserMessage}
import utils.TimeProvider

import scala.util.Try

class RegistrationFacade @Inject() (userDAO:UserDAO, user:User, timeProvider:TimeProvider) extends RegistrationAPI {

  override def signUp(userMessage:UserMessage, hashedPassword:String):Try[User] =
      user
      .create(None, userMessage.maybeUsername.getOrElse(UUID.randomUUID().toString), userMessage.email, hashedPassword,
        isActive = true, Some(timeProvider.now), None)
      .add(userDAO)

  override def isUsernameIsAvailable(username:String): Boolean = userDAO.byUsername(username).isEmpty

  override def isEmailIsAvailable(email:String): Boolean = userDAO.byEmail(email).isEmpty

}
