package controllers

import javax.inject._

import dal.PlayerRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(repo: PlayerRepository, val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends Controller with I18nSupport {

  val playersForm: Form[CreatePlayerForm] = Form {
    mapping(
      "nickName1" -> nonEmptyText,
      "nickName2" -> nonEmptyText
    )(CreatePlayerForm.apply)(CreatePlayerForm.unapply)
  }


  /**
    * The index action.
    */
  def index = Action { implicit request =>
    Ok(views.html.index("Welcome in Home page of our game", playersForm))
  }

  /**
    * The add person action.
    *
    * This is asynchronous, since we're invoking the asynchronous methods on PlayerRepository.
    */
  def addPlayers() = Action { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    playersForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.index("Welcome in Home page of our game", errorForm)))
      },
      // There were no errors in the from, so create the person.
      playerOne => {
        repo.create(playerOne.nickName1, 0)
        repo.create(playerOne.nickName2, 0)
      }
    )

    Ok(views.html.newGame())
  }

  /**
    * A REST endpoint that gets all the people as JSON.
    */
  def getPlayers = Action.async { implicit request =>
    repo.list().map { players =>
      Ok(Json.toJson(players))
    }
  }
}

/**
  * The create person form.
  *
  * Generally for forms, you should define separate objects to your models, since forms very often need to present data
  * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
  * that is generated once it's created.
  */
case class CreatePlayerForm(nickName1: String, nickName2: String)
