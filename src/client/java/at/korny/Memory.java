package at.korny;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class Memory {
    public static int getMemoryUsagePercent() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        long usedMemory = heapMemoryUsage.getUsed();
        long maxMemory = heapMemoryUsage.getMax();

        int percentage = (int) Math.floor(((double) usedMemory / maxMemory) * 100.0);
        return percentage;
    }

    public static void main(String[] args) {
        System.out.printf("Speichernutzung: %.2f%%%n", getMemoryUsagePercent());
    }
}
