package components.sheetRoomComponents.sheet;

public class FormattedValuePrinter {

    public static String formatCellValue(Object value) {
        String formattedValue;

        if (value instanceof Number) {
            if (value instanceof Integer || value instanceof Long) {
                formattedValue = String.format("%,d", value);
            } else if (value instanceof Double || value instanceof Float) {
                double doubleValue = ((Number) value).doubleValue();
                if (doubleValue == Math.floor(doubleValue)) {
                    formattedValue = String.format("%,d", (long) doubleValue);
                } else {
                    formattedValue = String.format("%,.2f", doubleValue);
                }
            } else {
                formattedValue = value.toString();
            }
        } else if (value instanceof Boolean) {
            formattedValue = value.toString().toUpperCase();
        } else {
            formattedValue = value != null ? value.toString() : "";
        }

        return formattedValue;
    }
}
