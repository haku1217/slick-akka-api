package todo.api.actor

import spray.json._

trait TodoActorJsonSerializer extends DefaultJsonProtocol {
  import TodoActor._

  implicit val findByIdCommand: RootJsonFormat[FindByIdCommand] = jsonFormat1(FindByIdCommand)
  implicit val createCommand: RootJsonFormat[CreateCommand] = jsonFormat1(CreateCommand)
  implicit val updateCommand: RootJsonFormat[UpdateCommand] = jsonFormat2(UpdateCommand)
  implicit val deleteCommand: RootJsonFormat[DeleteCommand] = jsonFormat1(DeleteCommand)

  implicit val todoReplayFormat: RootJsonFormat[TodoReply] = jsonFormat2(TodoReply)
  implicit val createdReply: RootJsonFormat[CreatedReply] = jsonFormat1(CreatedReply)
}
