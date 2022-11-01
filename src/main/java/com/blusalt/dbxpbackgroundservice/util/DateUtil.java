package com.blusalt.dbxpbackgroundservice.util;

import com.blusalt.dbxpbackgroundservice.dto.Frequency;
import com.blusalt.dbxpbackgroundservice.models.enums.FrequencyType;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Objects;

public class DateUtil {
    public static void main(String[] args) {
        Frequency frequency = Frequency.builder().frequency("YEARLY").year(2024).dayOfMonth(29).month(2).startTimeHour(4).startTimeMinutes(12).build();
        Date nextRunDate = getStartDate(frequency);
        System.out.println(nextRunDate);

    }

    public static Date getNextRunDate(Frequency frequency) {

        Integer year = frequency.getYear();
        Integer month = frequency.getMonth();
        Integer dayOfMonth = frequency.getDayOfMonth();
        Integer hour = frequency.getStartTimeHour();
        Integer minute = frequency.getStartTimeMinutes();

        FrequencyType frequencyType = FrequencyType.valueOf(frequency.getFrequency());
        switch (frequencyType) {
            case ONCE:
                return getDateByFrequency(frequency);

            case DAILY:
                return getAbsoluteDate(year, month, dayOfMonth, hour, minute, ChronoUnit.DAYS, 1);
            case WEEKLY:
                return getAbsoluteDate(year, month, dayOfMonth, hour, minute, ChronoUnit.WEEKS, 1);
            case BI_WEEKLY:
                return getAbsoluteDate(year, month, dayOfMonth, hour, minute, ChronoUnit.WEEKS, 2);
            case MONTHLY:
                return getAbsoluteDate(year, month, dayOfMonth, hour, minute, ChronoUnit.MONTHS, 1);
            case QUARTERLY:
                return getAbsoluteDate(year, month, dayOfMonth, hour, minute, ChronoUnit.MONTHS, 4);
            case YEARLY:
                return getAbsoluteDate(year, month, dayOfMonth, hour, minute, ChronoUnit.YEARS, 1);
        }

        return null;
    }

    public static LocalDate getNextDate(LocalDate startDate, TemporalUnit period, int times) {
        if (!period.isDateBased()) {
            throw new IllegalArgumentException("Cannot add " + period + " to a date");
        }

        LocalDate today = LocalDate.now();

        if (startDate.isBefore(today)) {
            LocalDate yesterday = today.minusDays(1);
            long timesToAdd = period.between(startDate, yesterday) / times + 1;
            return startDate.plus(timesToAdd * times, period);
        }
        if (startDate.getYear() == today.getYear()
                && startDate.getMonth() == today.getMonth()
                & startDate.getDayOfMonth() > today.getDayOfMonth()) {
            return startDate;
        } else if (startDate.isAfter(today) || startDate.isEqual(today) || startDate.isBefore(today)) {
            long timesToAdd = period.between(startDate, startDate) / times + 1;
            return startDate.plus(timesToAdd * times, period);
        } else {
            return startDate;
        }
    }

    public static Date getDateByFrequency(Frequency frequency) {
        LocalDate nextDate = LocalDate.of(frequency.getYear(), frequency.getMonth(), frequency.getDayOfMonth());
        LocalTime localTime = LocalTime.of(frequency.getStartTimeHour(), frequency.getStartTimeMinutes());
        Instant instant = nextDate.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public static Date getAbsoluteDate(Integer year, Integer month, Integer dayOfMonth, Integer hour, Integer minute, TemporalUnit temporalUnit, int frequency) {
        if (Objects.isNull(year)) {
            year = LocalDate.now().getYear();
        }
        LocalDate nextDate = getNextDate(LocalDate.of(year, month, dayOfMonth), temporalUnit, frequency);
        LocalTime localTime = LocalTime.of(hour, minute);
        Instant instant = nextDate.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public static Date getStartDate(Frequency frequency) {
        if (getDateByFrequency(frequency).before(new Date())) {
            return getNextRunDate(frequency);
        }
        return getDateByFrequency(frequency);

    }

    public static Frequency getNewFrequency(Date date, Frequency frequency) {
        LocalDateTime nextDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        Frequency newFrequency = new Frequency();
        newFrequency.setFrequency(frequency.getFrequency());
        newFrequency.setYear(nextDate.getYear());
        newFrequency.setMonth(nextDate.getMonthValue());
        newFrequency.setDayOfMonth(nextDate.getDayOfMonth());
        newFrequency.setStartTimeHour(nextDate.getHour());
        newFrequency.setStartTimeMinutes(nextDate.getMinute());
        return newFrequency;
    }

}


