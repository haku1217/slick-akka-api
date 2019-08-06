package todo.api
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.util.Timeout
import slick.jdbc.H2Profile.api._

import todo.api.repository.{TodoRepository, TodoRepositoryImpl}

object Main extends App{
  import  todo.api.actor._

  implicit val system: ActorSystem = ActorSystem("todo-api")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  val db = Database.forConfig("todo-slick-db")
  val todoRepository: TodoRepository = TodoRepositoryImpl(db)

  val todoSupervisor = system.actorOf(TodoSupervisor.props(3, 30.seconds))
  todoSupervisor ! TodoSupervisor.RegistrationCommand(TodoActor.props(todoRepository))

  val todoRoutes = TodoRoutes(todoSupervisor)
  val todoAPIServer = TodoAPIServer(todoRoutes)

  todoAPIServer.startServer("0.0.0.0", 8000, system)
}
