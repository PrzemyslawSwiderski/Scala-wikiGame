package models

import play.api.libs.json.Json

case class Player(id: Long, nickName: String, score: Int)


object Player {

  implicit val playerFormat = Json.format[Player]
}