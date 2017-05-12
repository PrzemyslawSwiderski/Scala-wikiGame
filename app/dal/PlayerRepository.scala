package dal

import javax.inject.{Inject, Singleton}

import models.Player
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


/**
  * A repository for players.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class PlayerRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import driver.api._

  /**
    * Here we define the table. It will have a name of players
    */
  private class PlayerTable(tag: Tag) extends Table[Player](tag, "players") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The nickName column */
    def nickName = column[String]("nickName")

    /** The score column */
    def score = column[Int]("score")

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Player object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Player case classes
      * apply and unapply methods.
      */
    def * = (id, nickName, score) <> ((Player.apply _).tupled, Player.unapply)
  }

  /**
    * The starting point for all queries on the players table.
    */
  private val players = TableQuery[PlayerTable]

  /**
    * Create a person with the given name and age.
    *
    * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
    * id for that person.
    */
  def create(nickName: String, score: Int): Future[Player] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (players.map(p => (p.nickName, p.score))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning players.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((nickNameScore, id) => Player(id, nickNameScore._1, nickNameScore._2))
      // And finally, insert the person into the database
      ) += (nickName, score)
  }

  /**
    * List all the players in the database.
    */
  def list(): Future[Seq[Player]] = db.run {
    players.result
  }
}