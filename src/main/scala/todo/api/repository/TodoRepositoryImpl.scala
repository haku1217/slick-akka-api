package todo.api.repository

import scala.concurrent.{ExecutionContext,Future}
import scala.util.control.NonFatal
import scalaz.\/
import scalaz.syntax.ToEitherOps
import slick.dbio.DBIOAction
import slick.jdbc.H2Profile.api._

object TodoRepositoryImpl{
  def apply(db: Database)(implicit ec: ExecutionContext): TodoRepositoryImpl =
    new TodoRepositoryImpl(db)
}

class TodoRepositoryImpl(db: Database)(implicit ec: ExecutionContext) extends TodoRepository with ToEitherOps {
  import Model._

  lazy val todos = TableQuery[TodoTable]

  private def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[\/[Throwable, R]] =
    db.run(a).map(_.right[Throwable]).recover {
      case NonFatal(ex) =>
        ex.left[R]
    }
  override def findAll(): Future[\/[Throwable, Seq[Todo]]] = run(todos.result)

  override def findById(id: Int): Future[\/[Throwable, Option[Todo]]] = {
    val  q = for {
      r <- todos if r.id === id
    } yield r
    run(q.result.headOption)
  }

  override def create(data: Todo): Future[\/[Throwable, Int]] =
    run(todos returning todos.map(_.id) += data)

  override def update(data: Todo): Future[\/[Throwable, Int]] = {
    val q = for {
      r <- todos if r.id === data.id
    } yield r.body
    run(q.update(data.body))
  }
  override def delete(id: Int): Future[\/[Throwable, Int]] = {
    val q = for {
      r <- todos if r.id === id
    } yield r
    run(q.delete)
  }
}
