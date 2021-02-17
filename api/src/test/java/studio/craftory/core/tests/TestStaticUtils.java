package studio.craftory.core.tests;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
}
