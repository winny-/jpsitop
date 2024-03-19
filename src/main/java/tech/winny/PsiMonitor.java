package tech.winny;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PsiMonitor {

    public enum MetricType {
        SOME, FULL;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static MetricType getEnum(String v) {
            for (var type : values()) {
                if (type.toString().equalsIgnoreCase(v))
                    return type;
            }
            throw new IllegalArgumentException(String.format("Invalid MetricType \"%s\"", v));
        }
    }

    public enum ResourceType {
        IO, CPU, MEMORY;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static ResourceType getEnum(String v) {
            for (var type : values()) {
                if (type.toString().equalsIgnoreCase(v))
                    return type;
            }
            throw new IllegalArgumentException(String.format("Invalid ResourceType \"%s\"", v));
        }
    }

    public record MetricLine(MetricType type, BigDecimal avg10, BigDecimal avg60, BigDecimal avg300, int total) {
        static Pattern pattern = Pattern.compile("(some|full) avg10=(\\d+\\.\\d+) avg60=(\\d+\\.\\d+) avg300=(\\d+\\.\\d+) total=(\\d+)");
        public static MetricLine fromString(String line) {
            var m = pattern.matcher(line);
            if (!m.find()) {
                throw new IllegalArgumentException(String.format("Invalid MetricLine %s", line));
            }
            var type = MetricType.getEnum(m.group(1));
            var avg10 = new BigDecimal(m.group(2));
            var avg60 = new BigDecimal(m.group(3));
            var avg300 = new BigDecimal(m.group(4));
            var total = Integer.parseInt(m.group(5));
            return new MetricLine(type, avg10, avg60, avg300, total);
        }

        public String toString() {
            return String.format(
                    "%s avg10=%s avg60=%s avg300=%s total=%s",
                    type.toString().toLowerCase(),
                    avg10,
                    avg60,
                    avg300,
                    total
                    );
        }
    }
    public record PSIMetric(ResourceType type, MetricLine some, MetricLine full) {

        public String toString() {
            return String.format("%s:%n  %s%n  %s%n",
                    type.toString().toUpperCase(),
                    some,
                    full);
        }
        public static PSIMetric fromBufferedReader(ResourceType type, BufferedReader reader) throws IOException {
            MetricLine some, full;
            some = MetricLine.fromString(reader.readLine());
            full = MetricLine.fromString(reader.readLine());
            assert some.type.equals(MetricType.SOME);
            assert full.type.equals(MetricType.FULL);
            return new PSIMetric(type, some, full);
        }

        public static PSIMetric fromPath(ResourceType type, String path) throws FileNotFoundException, IOException {
            var r = new BufferedReader(new FileReader(path));
            return fromBufferedReader(type, r);
        }

        public static PSIMetric io() throws FileNotFoundException, IOException {
            return fromPath(ResourceType.IO, "/proc/pressure/io");
        }

        public static PSIMetric cpu() throws FileNotFoundException, IOException {
            return fromPath(ResourceType.CPU, "/proc/pressure/cpu");
        }

        public static PSIMetric memory() throws FileNotFoundException, IOException {
            return fromPath(ResourceType.MEMORY, "/proc/pressure/memory");
        }
    }

    // PSIMetric[] can be spoken as "Array of PSIMetric"
    public static PSIMetric[] getMetrics() throws FileNotFoundException, IOException {
        return new PSIMetric[] {
                PSIMetric.cpu(),
                PSIMetric.io(),
                PSIMetric.memory(),
        };
    }
}
