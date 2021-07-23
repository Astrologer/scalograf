package scalograf
package client

import model._

import io.circe.Json
import marshallers.CirceMarshaller._
import sttp.client3._
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.client3.circe._

import scala.concurrent.Future

case class GrafanaClient(config: GrafanaConfig) {
  import config._

  private val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()
  private val grafanaRequest = basicRequest
    .header("Authorization", s"Bearer $authToken")

  def search() = {
    grafanaRequest
      .get(
        uri"$url/api/search"
          .addParam("limit", "5000")
      )
      .response(asJsonAlways[Seq[DashboardSnippet]])
      .send(backend)
  }

  def dashboard(uid: String) = {
    dashboardInner(asJsonAlways[Dashboard])(uid)
  }

  private[scalograf] def dashboardRaw(uid: String) = {
    dashboardInner(asJsonAlways[Json])(uid)
  }

  private def dashboardInner[T](responseAs: ResponseAs[T, Any])(uid: String) = {
    grafanaRequest
      .get(uri"$url/api/dashboards/uid/$uid")
      .response(responseAs)
      .send(backend)
  }

  def close() = {
    backend.close()
  }
}