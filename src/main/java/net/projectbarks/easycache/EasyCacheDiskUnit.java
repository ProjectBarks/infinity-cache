package net.projectbarks.easycache;

import lombok.Getter;

/**
 * Created by brandon on 10/5/14.
 */
public enum EasyCacheDiskUnit {

    Byte("b", 1l),
    Kilobyte("kb", 1000l),
    Megabyte("mb", 1000000l),
    Gigabyte("gb", 1000000000l);

    @Getter private String initial;
    @Getter private Long size;

    private EasyCacheDiskUnit(String initial, Long size) {
        this.initial = initial;
        this.size = size;
    }

    public long getAmount(long amount) {
        return size * amount;
    }

    public long toBytes(long amount) {
        return convert(amount, Byte);
    }

    public long toKilobytes(long amount) {
        return convert(amount, Kilobyte);
    }

    public long toMegabytes(long amount) {
        return convert(amount, Megabyte);
    }

    public long toGigabytes(long amount) {
        return convert(amount, Gigabyte);
    }

    public long convert(long amount, EasyCacheDiskUnit space) {
        return (size * amount) / space.getSize();
    }
}
