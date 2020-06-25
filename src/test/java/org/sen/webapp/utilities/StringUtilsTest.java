package org.sen.webapp.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sen.webapp.utilities.StringUtils;

public class StringUtilsTest
    {
        @Test
        public void isNotNullOrEmptyTest()
            {
                assertFalse(StringUtils.isNotNullOrEmpty(""));
                assertTrue(StringUtils.isNotNullOrEmpty("test"));
                assertFalse(StringUtils.isNotNullOrEmpty(null));
            }
    }