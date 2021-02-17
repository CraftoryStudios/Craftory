package studio.craftory.core.tests;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import studio.craftory.core.utils.StringUtils;

public class TestStaticUtils {


  @Test
  @DisplayName("String utils tests")
  public void testStringUtils() {
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
  }

}
