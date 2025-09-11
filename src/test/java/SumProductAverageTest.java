import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SumProductAverageTest {

    private static List<Double> extractAllDoubles(String output, String label) {
        Pattern p = Pattern.compile(
                "^.*\\b" + Pattern.quote(label) + "\\b.*?([\\d.Ee+-]+)\\s*$",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
        );
        Matcher m = p.matcher(output);
        List<Double> values = new ArrayList<>();
        while (m.find()) {
            values.add(Double.parseDouble(m.group(1)));
        }
        return values;
    }

    @Test
    public void testSampleRunParsesAndMatchesExpectedValues() {
        String input = String.join("\n",
                "3",
                "222.2 222.2 222.2",
                "3.14159 25.6 17",
                "100 0 55.6",
                "");

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        java.io.InputStream origIn = System.in;

        System.setIn(in);
        System.setOut(new PrintStream(outBytes));
        try {
            SumProductAverage.main(new String[]{});
        } finally {
            System.setOut(origOut);
            System.setIn(origIn);
        }

        String output = outBytes.toString(StandardCharsets.UTF_8);

        List<Double> sums = extractAllDoubles(output, "Sum");
        List<Double> products = extractAllDoubles(output, "Product");
        List<Double> averages = extractAllDoubles(output, "Average");

        assertEquals(3, sums.size(), "Expected 3 Sum lines");
        assertEquals(3, products.size(), "Expected 3 Product lines");
        assertEquals(3, averages.size(), "Expected 3 Average lines");

        double eps = 1e-9;
        assertEquals(666.5999999999999, sums.get(0), eps);
        assertEquals(1.0970645047999999E7, products.get(0), eps);
        assertEquals(222.19999999999996, averages.get(0), eps);
        assertEquals(45.74159, sums.get(1), eps);
        assertEquals(1367.219968, products.get(1), eps);
        assertEquals(15.247196666666667, averages.get(1), eps);
        assertEquals(155.6, sums.get(2), eps);
        assertEquals(0.0, products.get(2), eps);
        assertEquals(51.86666666666667, averages.get(2), eps);
    }
}
