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
        // Look for the label anywhere in the output, followed by any number
        Pattern p = Pattern.compile(
                "\\b" + Pattern.quote(label) + "\\b[^\\d]*([\\d.Ee+-]+)",
                Pattern.CASE_INSENSITIVE
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

        // Check if we found any results at all
        if (sums.size() == 0 && products.size() == 0 && averages.size() == 0) {
            fail("❌ No results found! Make sure your program outputs lines containing 'Sum', 'Product', and 'Average' with numbers.\n" +
                 "Example: 'Sum: 10.5' or 'The sum is 10.5'\n" +
                 "Your output was:\n" + output);
        }

        // Check for missing results
        if (sums.size() == 0) {
            fail("❌ No 'Sum' results found! Your program should output sum calculations.\n" +
                 "Look for lines like 'Sum: 10.5' or 'The sum is 10.5'\n" +
                 "Your output was:\n" + output);
        }
        if (products.size() == 0) {
            fail("❌ No 'Product' results found! Your program should output product calculations.\n" +
                 "Look for lines like 'Product: 10.5' or 'The product is 10.5'\n" +
                 "Your output was:\n" + output);
        }
        if (averages.size() == 0) {
            fail("❌ No 'Average' results found! Your program should output average calculations.\n" +
                 "Look for lines like 'Average: 10.5' or 'The average is 10.5'\n" +
                 "Your output was:\n" + output);
        }

        // Check for wrong number of results
        if (sums.size() != 3) {
            fail("❌ Expected 3 sum calculations, but found " + sums.size() + ".\n" +
                 "Your program should process 3 lines of input and output 3 sets of results.\n" +
                 "Make sure you're reading all 3 lines: '222.2 222.2 222.2', '3.14159 25.6 17', '100 0 55.6'\n" +
                 "Your output was:\n" + output);
        }
        if (products.size() != 3) {
            fail("❌ Expected 3 product calculations, but found " + products.size() + ".\n" +
                 "Your program should process 3 lines of input and output 3 sets of results.\n" +
                 "Make sure you're reading all 3 lines: '222.2 222.2 222.2', '3.14159 25.6 17', '100 0 55.6'\n" +
                 "Your output was:\n" + output);
        }
        if (averages.size() != 3) {
            fail("❌ Expected 3 average calculations, but found " + averages.size() + ".\n" +
                 "Your program should process 3 lines of input and output 3 sets of results.\n" +
                 "Make sure you're reading all 3 lines: '222.2 222.2 222.2', '3.14159 25.6 17', '100 0 55.6'\n" +
                 "Your output was:\n" + output);
        }

        double eps = 1e-9;
        
        // Test first set: 222.2 222.2 222.2
        if (Math.abs(sums.get(0) - 666.5999999999999) > eps) {
            fail("❌ Wrong sum for first set (222.2 222.2 222.2)!\n" +
                 "Expected: 666.6, but got: " + sums.get(0) + "\n" +
                 "Check your addition: 222.2 + 222.2 + 222.2 = ?");
        }
        if (Math.abs(products.get(0) - 1.0970645047999999E7) > eps) {
            fail("❌ Wrong product for first set (222.2 222.2 222.2)!\n" +
                 "Expected: 10,970,645, but got: " + products.get(0) + "\n" +
                 "Check your multiplication: 222.2 × 222.2 × 222.2 = ?");
        }
        if (Math.abs(averages.get(0) - 222.19999999999996) > eps) {
            fail("❌ Wrong average for first set (222.2 222.2 222.2)!\n" +
                 "Expected: 222.2, but got: " + averages.get(0) + "\n" +
                 "Check your division: (222.2 + 222.2 + 222.2) ÷ 3 = ?");
        }
        
        // Test second set: 3.14159 25.6 17
        if (Math.abs(sums.get(1) - 45.74159) > eps) {
            fail("❌ Wrong sum for second set (3.14159 25.6 17)!\n" +
                 "Expected: 45.74159, but got: " + sums.get(1) + "\n" +
                 "Check your addition: 3.14159 + 25.6 + 17 = ?");
        }
        if (Math.abs(products.get(1) - 1367.219968) > eps) {
            fail("❌ Wrong product for second set (3.14159 25.6 17)!\n" +
                 "Expected: 1367.22, but got: " + products.get(1) + "\n" +
                 "Check your multiplication: 3.14159 × 25.6 × 17 = ?");
        }
        if (Math.abs(averages.get(1) - 15.247196666666667) > eps) {
            fail("❌ Wrong average for second set (3.14159 25.6 17)!\n" +
                 "Expected: 15.247, but got: " + averages.get(1) + "\n" +
                 "Check your division: (3.14159 + 25.6 + 17) ÷ 3 = ?");
        }
        
        // Test third set: 100 0 55.6
        if (Math.abs(sums.get(2) - 155.6) > eps) {
            fail("❌ Wrong sum for third set (100 0 55.6)!\n" +
                 "Expected: 155.6, but got: " + sums.get(2) + "\n" +
                 "Check your addition: 100 + 0 + 55.6 = ?");
        }
        if (Math.abs(products.get(2) - 0.0) > eps) {
            fail("❌ Wrong product for third set (100 0 55.6)!\n" +
                 "Expected: 0.0, but got: " + products.get(2) + "\n" +
                 "Check your multiplication: 100 × 0 × 55.6 = ? (anything times 0 is 0!)");
        }
        if (Math.abs(averages.get(2) - 51.86666666666667) > eps) {
            fail("❌ Wrong average for third set (100 0 55.6)!\n" +
                 "Expected: 51.867, but got: " + averages.get(2) + "\n" +
                 "Check your division: (100 + 0 + 55.6) ÷ 3 = ?");
        }
    }
}
