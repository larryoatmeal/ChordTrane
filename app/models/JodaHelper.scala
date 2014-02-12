package models

import anorm._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.Period
import org.joda.time.Duration
import org.joda.time.format.PeriodFormat
import org.joda.time.format.PeriodFormatterBuilder
import java.sql.Timestamp

//Converts java.sql.Timestamp to joda.time.DateTime
//Needed for anorm to parse dates from MySQL


object JodaHelper{

  //MySQL conversion------------------------------------
  implicit def rowToDateTime: Column[DateTime] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case t: Timestamp => {
        Right(new DateTime(t.getTime))
      }
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to TimeStamp for column " + qualified))
    }
  }
  def toTimestamp(dateTime: DateTime): Timestamp = {
    new Timestamp(dateTime.getMillis())
  }
  def toTimestamp(dateTime: Option[DateTime]): Option[Timestamp] = {
    dateTime.map(toTimestamp(_))
  }
  


  //Formatting--------------------------------------------
  def formatDate(dateTime: DateTime) = {
    //val year = dateTime.basicDate()
    val fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
    val output = fmt.print(dateTime);
    output
  }
  def formatDate(dateTime: Option[DateTime]): Option[String] = {
    dateTime match {
      case Some(dt) => Some(formatDate(dt))
      case None => None
    }
  }

  //Time difference--------------------------------------------
  def timeDiff(created: DateTime, finish: Option[DateTime]) = {
    finish match {
      case Some(f) => {
        val diff = f.getMillis() - created.getMillis()
        if (diff < 0){
          "Anachronism"
        }
        else{
          val duration = new Duration(diff)
          (if(duration.getStandardDays() >0){ duration.getStandardDays() + "d "}else{""}) + duration.getStandardHours()%24 + "h " + duration.getStandardMinutes()%60 + "m "
        }
      } 
      case None => "NA"
    }
  }

  def periodToMilliseconds(raw: String): Long = {
    val long = 2834590284569083456l

    val period = new Period(1, 2, 3, 4, 5, 6, 7, 8)

    val format = "DD*:HH:MM"


    val firstColon = raw.indexOf(':')
    val lastColon = raw.lastIndexOf(':')
    

    val minuteString = raw.drop(lastColon + 1)
    val hourString = raw.substring(firstColon + 1, lastColon)
    val dayString = raw.substring(0, firstColon)

    def stripLeadingZero(input: String): Long = {
      val normalized = if (input.head == '0'){
        input.tail
      }else{
        input
      }
      normalized.toInt
    }

    val minutes = stripLeadingZero(minuteString)
    val hours = stripLeadingZero(hourString)
    val days = stripLeadingZero(dayString)


    val seconds: Long = minutes * 60 + hours * 60 * 60 + days * 60 * 60 * 24
   

    seconds
  }

  //Form conversions--------------------------------------------
  def formTimeToJoda(stringTime: String):DateTime = {
    val dt = new DateTime()
    val fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm")
    play.api.Logger.debug(DateTime.parse(stringTime, fmt).toString)
    DateTime.parse(stringTime, fmt)
  }
  def formTimeToJoda(stringTime: Option[String]):Option[DateTime] = {
    stringTime.map(formTimeToJoda(_))
  }
  def jodaToFormTime(dateTime: DateTime): String = {
    val fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm")
    val output = fmt.print(dateTime);
    output
  }
  def jodaToFormTime(dateTime: Option[DateTime]): Option[String] = {
    dateTime.map(jodaToFormTime(_))
  }
  val timePattern = {
    import scala.util.matching.Regex
    new Regex("""[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}""")
  }



}