package mx.org.fimpes.saii.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public class MxDateTime {

    private static final ZoneId CDMX_ZONE = ZoneId.systemDefault(); //ZoneId.of("America/Mexico_City");
    public static final TimeZone CDMX_TIMEZONE = TimeZone.getTimeZone("America/Mexico_City");
    public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm.ss"); //DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    public static final DateFormat FILE_FORMAT = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");    

    public static ZonedDateTime now() {
        return ZonedDateTime.now(CDMX_ZONE);
    }

    public static Date date() {
        return Date.from(now().toInstant());
    }

    public static Date dateOnly() {
        Date d = null;
        try {
            d = DATE_FORMAT.parse(DATE_FORMAT.format(date()));
        } catch (ParseException e) {
        }

        return d;
    }

    public static Date dateFrom(long epochMilli) {
        return Date.from(ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), CDMX_ZONE).toInstant());
    }

}
