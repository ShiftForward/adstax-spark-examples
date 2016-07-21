package eu.shiftforward.adstax.spark.examples

import com.github.nscala_time.time.Imports._
import eu.shiftforward.adstax.spark.{ AdStaxSparkContext, SparkJob }
import scala.math.random
import spray.json._

class EventTypes extends SparkJob {
  val name = "Event Types Count"

  def run(args: Array[String])(implicit context: AdStaxSparkContext): Unit = {
    val end = new DateTime()
    val start = end - 7.days
    val events = context.eventsRDD(Set(), start, end).map(_.parseJson)
    val groupedEvents = events.map { ev =>
      val tpe = for {
        d <- ev.asJsObject.fields.get("data")
        t <- d.asJsObject.fields.get("type")
      } yield t.toString
      (tpe.getOrElse("unknown"), ev)
    }.countByKey

    println("Number of events by type in the last 7 days:")
    groupedEvents.foreach {
      case (t, c) =>
        println("%20s -> %d".format(t, c))
    }
  }
}
