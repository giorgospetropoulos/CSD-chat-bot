package com.example.csdbot.components;

import android.os.SystemClock;

import java.util.Comparator;

public class Reminder{
    private static int count = 0;
    private String name, description;
    private int day, month, year, hour, min, id;
    public enum priority {
        low,
        mid,
        high
    }
    private priority reminder_priority;

    /**
     *      Constructors
     */

    public Reminder(){
        this.id = count++;
    }

    public Reminder(String name, int day, int month, int year) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.id = (int) SystemClock.uptimeMillis();
    }

    public Reminder(String name, int day, int month, int year, priority rem_priority) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.id = (int) SystemClock.uptimeMillis();
        switch (rem_priority) {
            case low:
                this.reminder_priority = priority.low;
                break;
            case mid:
                this.reminder_priority = priority.mid;
                break;
            case high:
                this.reminder_priority = priority.high;
                break;
            default:
                this.reminder_priority = priority.low;
                break;
        }
    }

    public Reminder(String name, int day, int month, int year, int hour, int min, priority rem_priority) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.min = min;
        this.id = (int) SystemClock.uptimeMillis();
        switch (rem_priority) {
            case low:
                this.reminder_priority = priority.low;
                break;
            case mid:
                this.reminder_priority = priority.mid;
                break;
            case high:
                this.reminder_priority = priority.high;
                break;
            default:
                this.reminder_priority = priority.low;
                break;
        }
    }

    /**
     *      Sort Reminders by time
     */
    public static Comparator<Reminder> ReminderComparator = new Comparator<Reminder>() {
        @Override
        public int compare(Reminder r1, Reminder r2) {
            String comp = Integer.toString(r1.getYear());

            if(r1.getMonth() < 10) {
                comp += "0" + r1.getMonth();
            } else {
                comp += Integer.toString(r1.getMonth());
            }
            if(r1.getDay() < 10) {
                comp += "0" + r1.getDay();
            } else {
                comp += Integer.toString(r1.getDay());
            }
            if(r1.getHour() < 10) {
                comp += "0" + r1.getHour();
            } else {
                comp += Integer.toString(r1.getHour());
            }
            if(r1.getMin() < 10) {
                comp += "0" + r1.getMin();
            } else {
                comp += Integer.toString(r1.getMin());
            }

            String comp2 = Integer.toString(r2.getYear());

            if(r2.getMonth() < 10) {
                comp2 += "0" + r2.getMonth();
            } else {
                comp2 += Integer.toString(r2.getMonth());
            }
            if(r2.getDay() < 10) {
                comp2 += "0" + r2.getDay();
            } else {
                comp2 += Integer.toString(r2.getDay());
            }
            if(r2.getHour() < 10) {
                comp2 += "0" + r2.getHour();
            } else {
                comp2 += Integer.toString(r2.getHour());
            }
            if(r2.getMin() < 10) {
                comp2 += "0" + r2.getMin();
            } else {
                comp2 += Integer.toString(r2.getMin());
            }

            long compareRem = Long.parseLong(comp);
            long compareRem2 = Long.parseLong(comp2);

            return (int) (compareRem - compareRem2);
        }
    };

    /**
     *      Getters and Setters
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public priority getReminder_priority() {
        return reminder_priority;
    }

    public void setReminder_priority(priority reminder_priority) {
        this.reminder_priority = reminder_priority;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return getName();
    }
}
