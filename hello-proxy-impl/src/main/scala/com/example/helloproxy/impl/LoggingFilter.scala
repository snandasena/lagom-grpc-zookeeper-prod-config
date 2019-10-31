package com.example.helloproxy.impl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.example.helloproxy.api.ExternalService
import javax.inject.Inject
import play.api.http.HttpEntity
import play.api.mvc._

import scala.concurrent.{ExecutionContextExecutor, Future}

class LoggingFilter @Inject()(externalService: ExternalService) extends Filter {

  private implicit val sys: ActorSystem = ActorSystem("ExtractorClientActor")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = sys.dispatcher

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
    //    val result = externalService.countryList.invoke()
    //
    //    val re = Await.result(result, Duration.Inf)
    //    println(re)
    try {

      val user = requestHeader.headers.apply("X-Authorization-User")
      println("Authorization user:", user)
      val token = requestHeader.headers.apply("X-Authorization-Key")
      println("Authorization key:", token)
      println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")

      nextFilter(requestHeader).map { result => result
      }

    } catch {
      case e: Exception => {
        println(e.getMessage)

      }
        Future.successful(Result(
          header = ResponseHeader(200, Map.empty),
          body = HttpEntity.Strict(ByteString("Unauthorized access !"), Some("text/plain"))
        ))
    }

  }
}
