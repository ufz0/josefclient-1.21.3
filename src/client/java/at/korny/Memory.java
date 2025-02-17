package at.korny;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class Memory {
    public static double getMemoryUsagePercent() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        long usedMemory = heapMemoryUsage.getUsed();
        long maxMemory = heapMemoryUsage.getMax();

        return ((double) usedMemory / maxMemory) * 100.0;
    }

    public static void main(String[] args) {
        System.out.printf("Speichernutzung: %.2f%%%n", getMemoryUsagePercent());
    }
}
