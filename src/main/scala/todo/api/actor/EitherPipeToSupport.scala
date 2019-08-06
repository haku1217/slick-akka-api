package todo.api.actor

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import akka.actor.{Actor, ActorRef, Status}
import akka.pattern.PipeToSupport
import scalaz.{-\/, \/, \/-}


trait EitherPipeToSupport extends PipeToSupport {

  final class EitherPipeableFuture[L <: Throwable, R](val future: Future[\/[L, R]])(
    implicit ec: ExecutionContext) {
    def pipeTo(recipient: ActorRef)(implicit sender: ActorRef = Actor.noSender): Future[\/[L, R]] =
      future andThen {
        case Success(\/-(r)) =>
          recipient ! r
        case Success(-\/(f)) =>
          recipient ! Status.Failure(f)
        case Failure(f) =>
          recipient ! Status.Failure(f)
      }
    def to(recipient: ActorRef): EitherPipeableFuture[L, R] = to(recipient, Actor.noSender)

    def to(recipient: ActorRef, sender: ActorRef): EitherPipeableFuture[L, R] = {
      pipeTo(recipient)(sender)
      this
    }
  }
  implicit def eitherPipe[L <: Throwable, R](future: Future[\/[L, R]])(
    implicit ec: ExecutionContext): EitherPipeableFuture[L, R] = new EitherPipeableFuture(future)

}
