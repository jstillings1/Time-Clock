/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 *
 * This class formats the date in year Month day format
 */
public class DateLabelFormatter extends  AbstractFormatter
{
    private String datePattern = "yyyy-MM-dd HH:mm:ss ";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
    /** this is the empty constructor for the datelabelformatter class */
    public DateLabelFormatter() {
    }
    /** takes a string and parses it into an object */
    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }
    /** takes and object and parses it into a string */
    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }
}
