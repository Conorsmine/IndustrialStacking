package com.conorsmine.net.industrialstacking.modconfigs;

/**
 * Enum to help parse .cfg files
 */
public enum DataTypes {
    INT,
    BOOL;

    /**
     * @param line Line of the config file
     * @return True if the line starts with "I:" or "B:"
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isData(String line) {
        return line.matches("\\s*[I|B]:.*");
    }

    /**
     * @param line Line of the config file
     * @return The corresponding enum
     */
    public static DataTypes getDataType(String line) {
        if (line.matches("\\s*I:.*")) return DataTypes.INT;
        else return DataTypes.BOOL;
    }

    /**
     * @param line Line of the config file
     * @return The key of the config
     */
    public static String getDataKey(String line) {
        return line.replaceAll("\\s", "").replaceAll("[I|B]:", "").replaceAll("=.*", "");
    }

    /**
     * @param line Line of the config file
     * @return The value of the config
     */
    public static Object getDataValue(String line) {
        final DataTypes dataType = getDataType(line);
        final String value = line.replaceAll("\\s", "").replaceAll(".*=", "");
        if (dataType == DataTypes.BOOL)
            return Boolean.valueOf(value);
        else
            return Integer.parseInt(value);
    }
}
