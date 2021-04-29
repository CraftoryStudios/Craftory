package studio.craftory.core.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studio.craftory.core.utils.ParseUtils;
import studio.craftory.core.utils.StringUtils;

public class TestStaticUtils {


  @Test
  @DisplayName("String utils tests - rawEnergyToPrefixed")
  public void testStringUtilsRawEnergyToPrefixed() {
    Assertions.assertEquals("1 Re", StringUtils.rawEnergyToPrefixed(1));
    Assertions.assertEquals("10 Re", StringUtils.rawEnergyToPrefixed(10));
    Assertions.assertEquals("100 Re", StringUtils.rawEnergyToPrefixed(100));
    Assertions.assertEquals("1000 Re", StringUtils.rawEnergyToPrefixed(1000));
    Assertions.assertEquals("10000 Re", StringUtils.rawEnergyToPrefixed(10000));
    Assertions.assertEquals("100 KRe", StringUtils.rawEnergyToPrefixed(100000));
    Assertions.assertEquals("1 MRe", StringUtils.rawEnergyToPrefixed(1000000));
    Assertions.assertEquals("10 MRe", StringUtils.rawEnergyToPrefixed(10000000));
    Assertions.assertEquals("100 MRe", StringUtils.rawEnergyToPrefixed(100000000));
    Assertions.assertEquals("1 GRe", StringUtils.rawEnergyToPrefixed(1000000000));
    Assertions.assertEquals("10 GRe", StringUtils.rawEnergyToPrefixed(10000000000L));
    Assertions.assertEquals("100 GRe", StringUtils.rawEnergyToPrefixed(100000000000L));
    Assertions.assertEquals("1 TRe", StringUtils.rawEnergyToPrefixed(1000000000000L));
    Assertions.assertEquals("10 TRe", StringUtils.rawEnergyToPrefixed(10000000000000L));
    Assertions.assertEquals("100 TRe", StringUtils.rawEnergyToPrefixed(100000000000000L));
    Assertions.assertEquals("1 PRe", StringUtils.rawEnergyToPrefixed(1000000000000000L));
    Assertions.assertEquals("10 PRe", StringUtils.rawEnergyToPrefixed(10000000000000000L));
    Assertions.assertEquals("100 PRe", StringUtils.rawEnergyToPrefixed(100000000000000000L));
    Assertions.assertEquals("1 ERe", StringUtils.rawEnergyToPrefixed(1000000000000000000L));
    Assertions.assertEquals("9.223 ERe", StringUtils.rawEnergyToPrefixed(Long.MAX_VALUE - 1));
    Assertions.assertEquals("A bukkit load", StringUtils.rawEnergyToPrefixed(Long.MAX_VALUE));

    Assertions.assertEquals("12.34 MRe", StringUtils.rawEnergyToPrefixed(12340000));
    Assertions.assertEquals("123.456 MRe", StringUtils.rawEnergyToPrefixed(123456000));
    Assertions.assertEquals("123.456 GRe", StringUtils.rawEnergyToPrefixed(123456123400L));
  }

  @Test
  @DisplayName("String utils tests - rawFluidToPrefixed")
  public void testStringUtilsRawFluidToPrefixed() {
    Assertions.assertEquals("1 mB", StringUtils.rawFluidToPrefixed(1));
    Assertions.assertEquals("10 mB", StringUtils.rawFluidToPrefixed(10));
    Assertions.assertEquals("100 mB", StringUtils.rawFluidToPrefixed(100));
    Assertions.assertEquals("1000 mB", StringUtils.rawFluidToPrefixed(1000));
    Assertions.assertEquals("10000 mB", StringUtils.rawFluidToPrefixed(10000));
    Assertions.assertEquals("100 B", StringUtils.rawFluidToPrefixed(100000));
    Assertions.assertEquals("1 KB", StringUtils.rawFluidToPrefixed(1000000));
    Assertions.assertEquals("10 KB", StringUtils.rawFluidToPrefixed(10000000));
    Assertions.assertEquals("100 KB", StringUtils.rawFluidToPrefixed(100000000));
    Assertions.assertEquals("1 MB", StringUtils.rawFluidToPrefixed(1000000000));
    Assertions.assertEquals("10 MB", StringUtils.rawFluidToPrefixed(10000000000L));
    Assertions.assertEquals("100 MB", StringUtils.rawFluidToPrefixed(100000000000L));
    Assertions.assertEquals("1 GB", StringUtils.rawFluidToPrefixed(1000000000000L));
    Assertions.assertEquals("10 GB", StringUtils.rawFluidToPrefixed(10000000000000L));
    Assertions.assertEquals("100 GB", StringUtils.rawFluidToPrefixed(100000000000000L));
    Assertions.assertEquals("1 TB", StringUtils.rawFluidToPrefixed(1000000000000000L));
    Assertions.assertEquals("10 TB", StringUtils.rawFluidToPrefixed(10000000000000000L));
    Assertions.assertEquals("100 TB", StringUtils.rawFluidToPrefixed(100000000000000000L));
    Assertions.assertEquals("1 PB", StringUtils.rawFluidToPrefixed(1000000000000000000L));
    Assertions.assertEquals("9.223 PB", StringUtils.rawFluidToPrefixed(Long.MAX_VALUE - 1));
    Assertions.assertEquals("A bukkit load", StringUtils.rawFluidToPrefixed(Long.MAX_VALUE));

    Assertions.assertEquals("12.34 KB", StringUtils.rawFluidToPrefixed(12340000));
    Assertions.assertEquals("123.456 KB", StringUtils.rawFluidToPrefixed(123456000));
    Assertions.assertEquals("123.456 MB", StringUtils.rawFluidToPrefixed(123456123400L));
  }

  @Test
  @DisplayName("Parse Utils tests - Int")
  public void parseUtilsTestInt() {
    Assertions.assertEquals(1, ParseUtils.parseOrDefault("abcd",1));
    Assertions.assertEquals(1, ParseUtils.parseOrDefault("11abcd",1));
    Assertions.assertEquals(2, ParseUtils.parseOrDefault("1.0",2));
    Assertions.assertEquals(1, ParseUtils.parseOrDefault("",1));
    Assertions.assertEquals(1, ParseUtils.parseOrDefault(" ",1));
    Assertions.assertEquals(1, ParseUtils.parseOrDefault(null,1));
    Assertions.assertEquals(1, ParseUtils.parseOrDefault("34-5",1));
    Assertions.assertEquals(1, ParseUtils.parseOrDefault("2147483648",1));
    Assertions.assertEquals(1, ParseUtils.parseOrDefault("-21474836483",1));

    Assertions.assertEquals(1, ParseUtils.parseOrDefault("1",2));
    Assertions.assertEquals(7, ParseUtils.parseOrDefault("007",2));
    Assertions.assertEquals(-4321, ParseUtils.parseOrDefault("-4321",2));
    Assertions.assertEquals(123456789, ParseUtils.parseOrDefault("123456789",2));
    Assertions.assertEquals(Integer.MAX_VALUE, ParseUtils.parseOrDefault("2147483647",1));
    Assertions.assertEquals(Integer.MIN_VALUE, ParseUtils.parseOrDefault("-2147483648",1));
  }

  @Test
  @DisplayName("Parse Utils tests - Long")
  public void parseUtilsTestLong() {
    Assertions.assertEquals(1L, ParseUtils.parseOrDefault("abcd",1L));
    Assertions.assertEquals(1L, ParseUtils.parseOrDefault("11abcd",1L));
    Assertions.assertEquals(2L, ParseUtils.parseOrDefault("1.0",2L));
    Assertions.assertEquals(1L, ParseUtils.parseOrDefault("",1L));
    Assertions.assertEquals(1L, ParseUtils.parseOrDefault(" ",1L));
    Assertions.assertEquals(1L, ParseUtils.parseOrDefault(null,1L));
    Assertions.assertEquals(1L, ParseUtils.parseOrDefault("34-5",1L));
    Assertions.assertEquals(1L, ParseUtils.parseOrDefault("9223372036854775808",1L));
    Assertions.assertEquals(1L, ParseUtils.parseOrDefault("-92233720368547758038",1L));

    Assertions.assertEquals(1L, ParseUtils.parseOrDefault("1",2L));
    Assertions.assertEquals(7L, ParseUtils.parseOrDefault("007",2L));
    Assertions.assertEquals(-4321L, ParseUtils.parseOrDefault("-4321",2L));
    Assertions.assertEquals(123456789L, ParseUtils.parseOrDefault("123456789",2L));
    Assertions.assertEquals(Long.MAX_VALUE, ParseUtils.parseOrDefault("9223372036854775807",1L));
    Assertions.assertEquals(Long.MIN_VALUE, ParseUtils.parseOrDefault("-9223372036854775808",1L));
  }

  @Test
  @DisplayName("Parse Utils tests - Float")
  public void parseUtilsTestFloat() {
    Assertions.assertEquals(1f, ParseUtils.parseOrDefault("abcd",1f));
    Assertions.assertEquals(1f, ParseUtils.parseOrDefault("11abcd",1f));
    Assertions.assertEquals(1f, ParseUtils.parseOrDefault("",1f));
    Assertions.assertEquals(1f, ParseUtils.parseOrDefault(" ",1f));
    Assertions.assertEquals(1f, ParseUtils.parseOrDefault(null,1f));
    Assertions.assertEquals(1f, ParseUtils.parseOrDefault("34-5",1f));

    Assertions.assertEquals(1.0f, ParseUtils.parseOrDefault("1.0",2f));
    Assertions.assertEquals(1f, ParseUtils.parseOrDefault("1",2f));
    Assertions.assertEquals(7f, ParseUtils.parseOrDefault("007",2f));
    Assertions.assertEquals(-4321f, ParseUtils.parseOrDefault("-4321",2f));
    Assertions.assertEquals(123456789f, ParseUtils.parseOrDefault("123456789",2f));
    Assertions.assertEquals(Float.MAX_VALUE, ParseUtils.parseOrDefault("340282346638528860000000000000000000000.000000",1f));
    Assertions.assertEquals(Float.POSITIVE_INFINITY, ParseUtils.parseOrDefault("34028234663852886143242340000000000000000000.12",1f));
    Assertions.assertEquals(Float.NEGATIVE_INFINITY, ParseUtils.parseOrDefault("-9223372036854775803832432432423432432432.4",1f));
    Assertions.assertEquals(-Float.MIN_VALUE, ParseUtils.parseOrDefault("-0.0000000000000000000000000000000000000000000014012985",1f));
    Assertions.assertEquals(Float.MIN_VALUE, ParseUtils.parseOrDefault("0.0000000000000000000000000000000000000000000014",1f));
  }

  @Test
  @DisplayName("Parse Utils tests - Double")
  public void parseUtilsTestDouble() {
    Assertions.assertEquals(1d, ParseUtils.parseOrDefault("abcd",1d));
    Assertions.assertEquals(1d, ParseUtils.parseOrDefault("11abcd",1d));
    Assertions.assertEquals(1d, ParseUtils.parseOrDefault("",1d));
    Assertions.assertEquals(1d, ParseUtils.parseOrDefault(" ",1d));
    Assertions.assertEquals(1d, ParseUtils.parseOrDefault(null,1d));
    Assertions.assertEquals(1d, ParseUtils.parseOrDefault("34-5",1d));

    Assertions.assertEquals(1.0d, ParseUtils.parseOrDefault("1.0",2d));
    Assertions.assertEquals(1d, ParseUtils.parseOrDefault("1",2d));
    Assertions.assertEquals(7d, ParseUtils.parseOrDefault("007",2d));
    Assertions.assertEquals(-4321d, ParseUtils.parseOrDefault("-4321",2d));
    Assertions.assertEquals(123456789d, ParseUtils.parseOrDefault("123456789",2d));
    Assertions.assertEquals(Double.POSITIVE_INFINITY, ParseUtils.parseOrDefault("1797693134862315700000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",1d));
    Assertions.assertEquals(123456789123456789123456789123456789123456789123456789d, ParseUtils.parseOrDefault("123456789123456789123456789123456789123456789123456789",2d));
  }
}
