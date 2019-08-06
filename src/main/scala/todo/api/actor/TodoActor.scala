package todo.api.actor


import scala.concurrent.{ExecutionContext, Future}
import akka.actor.{Actor, ActorLogging, Props}
import scalaz.std.scalaFuture.futureInstance
import scalaz.{EitherT, \/}

import todo.api.repository.TodoRepository

object TodoActor {

  def props(todoRepository: TodoRepository) = Props(new TodoActor(todoRepository))

  sealed trait Command
  final case object FindAllCommand extends Command
  final case class FindByIdCommand(id: Int) extends Command
  final case class CreateCommand(body: String) extends Command
  final case class UpdateCommand(id: Int, body: String) extends Command
  final case class DeleteCommand(id: Int) extends Command

  sealed trait Reply
  final case class TodoReply(id: Int, body: String) extends Reply
  final case class CreatedReply(id: Int) extends Reply

  final case object UpdatedReply extends Reply
  final case object DeletedReply extends Reply

}

class TodoActor(todoRepository: TodoRepository) extends Actor with ActorLogging with EitherPipeToSupport {
  import TodoActor._
  import todo.api.repository.Model._

  implicit val executor: ExecutionContext = context.dispatcher

  implicit class RichFutureEither[A, B](self: Future[\/[A, B]]) {
    def toEitherT: EitherT[Future, A, B] = EitherT[Future, A, B](self)
  }

  private def findAll() =
    for {
      todos <- todoRepository.findAll().toEitherT
    } yield todos.map(t => TodoReply(t.id, t.body))

  private def findById(cmd: FindByIdCommand) =
    for {
      todo <- todoRepository.findById(cmd.id).toEitherT
    } yield todo.map(t => TodoReply(t.id, t.body))
  private def create(cmd: CreateCommand) =
    for {
      createdId <- todoRepository.create(Todo(0, cmd.body)).toEitherT
    } yield CreatedReply(createdId)
  private def update(cmd: UpdateCommand) =
    for {
      _ <- todoRepository.update(Todo(cmd.id, cmd.body)).toEitherT
    } yield UpdatedReply
  private def delete(cmd: DeleteCommand) =
    for {
      _ <- todoRepository.delete(cmd.id).toEitherT
    } yield DeletedReply

  override def preStart(): Unit = log.info("starting todo actor.")
  override def postStop(): Unit = log.info("stopping todo actor.")
  override def receive: Receive = {
    case FindAllCommand =>
      log.info("receive find all command.")
      eitherPipe(findAll().run) to sender()
      ()
    case cmd: FindByIdCommand =>
      log.info("receive find by id command.")
      eitherPipe(findById(cmd).run) to sender()
      ()
    case cmd: CreateCommand =>
      log.info("receive create command.")
      eitherPipe(create(cmd).run) to sender()
      ()
    case cmd: UpdateCommand =>
      log.info("receive update command.")
      eitherPipe(update(cmd).run) to sender()
      ()
    case cmd: DeleteCommand =>
      log.info("receive delete command.")
      eitherPipe(delete(cmd).run) to sender()
      ()
    case unknown =>
      log.error(s"receive unknown type. type: ${unknown.getClass.getName}")
  }

}