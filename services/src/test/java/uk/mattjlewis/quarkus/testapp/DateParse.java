package uk.mattjlewis.quarkus.testapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateParse {
	public static void main(String[] args) {
		String val = "2020-02-17T11:37:41.522Z[UTC]";

		System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));
		//System.out.println(DateTimeFormatter.RFC_1123_DATE_TIME.format(LocalDateTime.now()));
		System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("UTC"))));
		try {
			System.out.println(DateTimeFormatter.ISO_DATE_TIME.parse(val));
		} catch (DateTimeParseException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}

		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ'[UTC]'";
		
		DateFormat df = new SimpleDateFormat(pattern);
		System.out.println(df.format(new Date()));
		try {
			Date d = df.parse(val);
			System.out.println("Got date " + d);
		} catch (ParseException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}
}
