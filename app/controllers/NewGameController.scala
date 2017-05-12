package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, Controller}

@Singleton
class NewGameController @Inject() extends Controller {

  /**
    * The index action.
    */
  def index = Action { implicit request =>
    Ok(views.html.newGame())
  }
}
