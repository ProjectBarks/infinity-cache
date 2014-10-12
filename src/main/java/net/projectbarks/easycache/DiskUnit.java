package net.projectbarks.easycache;

import lombok.Getter;

/**
 * Disk unit is class based off of {@link java.util.concurrent.TimeUnit} but
 * for disk space. This class enables for easy conversions and interpretation.
 * For more information regarding this class look at the method documentation.
 *
 * Created by brandon on 10/5/14.
 */
public enum DiskUnit {

    Byte("b", 1l),
    Kilobyte("kb", 1000l),
    Megabyte("mb", 1000000l),
    Gigabyte("gb", 1000000000l);

    /**
     * This function gets the value of the initial string.
     * The initial string is used to denote the  initial
     * version of the data type of our memory.
     *
     * @return the appropriate unit for the size of the memory used.
     */
    @Getter private String initial;

    /**
     * This function is used to get the value of the exact size
     * of the memory being used by the array-list. It will be used to
     * find out if a function is small enough to be added to the database.
     *
     * @return the size of the data object.
     */
    @Getter private Long size;

    /**
     * This function is used to easily reset some variables. This makes
     * it easier to change important values and recalculate values for
     * memory size.
     *
     * @param initial the initial units of memory of the array-list object.
     * @param size the raw size of the data in units of whatever initial is.
     */

    private DiskUnit(String initial, Long size) {
        this.initial = initial;
        this.size = size;
    }

    /**
     * This function converts a value into bytes. It is used to find the memory
     * we are using in bytes. We use the function {@link #convert(long, DiskUnit)}
     * achieve this. We get the size of the function from the above enum, which We then divide
     * by the value of the data unit.
     *
     * @param amount the amount of data we are trying to convert
     * @return the amount converted to bytes.
     */
     public long toBytes(long amount) {
        return convert(amount, Byte);
    }

    /**
     * This function converts a value into kilobytes. It is used to find the memory
     * we are using in kilobytes. We use the function {@link #convert(long, DiskUnit)}
     * achieve this. We get the size of the function from the above enum, which we then divide
     * by the value of the data unit.
     *
     * @param amount the amount of data we are trying to convert
     * @return the amount converted to kilobytes.
     */
    public long toKilobytes(long amount) {
        return convert(amount, Kilobyte);
    }

    /**
     * This function converts a value into megabytes. It is used to find the memory
     * we are using in megabytes. We use the function {@link #convert(long, DiskUnit)}
     * achieve this. We get the size of the function from the above enum, which We then divide
     * by the value of the data unit.
     *
     * @param amount the amount of data we are trying to convert
     * @return the amount converted to megabytes.
     */
    public long toMegabytes(long amount) {
        return convert(amount, Megabyte);
    }

    /**
     * This function converts a value into gigabytes. It is used to find the memory
     * we are using in gigabytes. We use the function {@link #convert(long, DiskUnit)}
     * achieve this. We get the size of the function from the above enum, which We then divide
     * by the value of the data unit.
     *
     * @param amount the amount of data we are trying to convert.
     * @return the amount converted to gigabytes.
     */
    public long toGigabytes(long amount) {
        return convert(amount, Gigabyte);
    }

    /**
     * This function is used as a basis for the other functions above. It is the basic mathematical
     * formula. It is based on the unit you are trying to convert to in an enum format and the amount
     * convert.
     *
     * @param amount the amount of data we are trying to convert.
     * @param space the type of unit we are converting to.
     * @return We return the converted unit.
     */
    public long convert(long amount, DiskUnit space) {
        return (size * amount) / space.getSize();
    }
}
