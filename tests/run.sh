#!/bin/bash
echo "🧪 Running SumAverageProduct tests..."
echo "=========================================="

# Run tests with a clean build to avoid stale classes
mvn -q clean test
TEST_EXIT_CODE=$?

# Locate surefire summary (txt) or fallback to XML
SUMMARY_TXT=$(ls target/surefire-reports/TEST-*.txt 2>/dev/null | head -1)
SUMMARY_XML=$(ls target/surefire-reports/TEST-*.xml 2>/dev/null | head -1)

TOTAL_TESTS=0
FAILURES=0
ERRORS=0
SKIPPED=0

if [ -f "$SUMMARY_TXT" ]; then
  LINE=$(grep "Tests run:" "$SUMMARY_TXT" | tail -1)
  TOTAL_TESTS=$(echo "$LINE" | sed -n 's/.*Tests run: \([0-9]\+\).*/\1/p')
  FAILURES=$(echo "$LINE" | sed -n 's/.*Failures: \([0-9]\+\).*/\1/p')
  ERRORS=$(echo "$LINE" | sed -n 's/.*Errors: \([0-9]\+\).*/\1/p')
  SKIPPED=$(echo "$LINE" | sed -n 's/.*Skipped: \([0-9]\+\).*/\1/p')
elif [ -f "$SUMMARY_XML" ]; then
  TOTAL_TESTS=$(grep -o 'tests="[0-9]\+"' "$SUMMARY_XML" | head -1 | grep -o '[0-9]\+')
  FAILURES=$(grep -o 'failures="[0-9]\+"' "$SUMMARY_XML" | head -1 | grep -o '[0-9]\+')
  ERRORS=$(grep -o 'errors="[0-9]\+"' "$SUMMARY_XML" | head -1 | grep -o '[0-9]\+')
  SKIPPED=$(grep -o 'skipped="[0-9]\+"' "$SUMMARY_XML" | head -1 | grep -o '[0-9]\+')
fi

PASSED_TESTS=$((TOTAL_TESTS - FAILURES - ERRORS - SKIPPED))

echo "📊 TEST RESULTS:"
echo "Tests run: $TOTAL_TESTS, Failures: $FAILURES, Errors: $ERRORS, Skipped: $SKIPPED"
echo "Passed: $PASSED_TESTS"

# 10-point scale
if [ "$TOTAL_TESTS" -gt 0 ]; then
  SCORE=$((PASSED_TESTS * 10 / TOTAL_TESTS))
else
  SCORE=0
fi

echo "=========================================="
echo "🎯 FINAL SCORE: $SCORE/10 points"
echo "=========================================="

# Show brief failure details if exist
if [ "$FAILURES" -gt 0 ] || [ "$ERRORS" -gt 0 ]; then
  echo "❌ FAILED TESTS (first 20 lines):"
  for f in target/surefire-reports/TEST-*.txt; do
    [ -f "$f" ] || continue
    echo "-- $(basename "$f") --"
    grep -n "<<< FAILURE!" -n "$f" -n -A 5 || true
  done | head -20
  echo ""
  echo "💡 HINT: Ensure sum/product/average calculations match expected values."
  exit 1
fi

# If Maven failed or no tests detected, mark as failure
if [ "$TEST_EXIT_CODE" -ne 0 ] || [ "$TOTAL_TESTS" -eq 0 ]; then
  echo "❌ Test run failed or no tests detected. Ensure test class names match *Test.java"
  exit 1
fi

echo "✅ All tests passed! Great job!"
exit 0


