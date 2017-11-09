package org.jasig.portlet.announcements.model.validators;

import junit.framework.TestCase;
import org.junit.Test;

public class ValidationHelperTest extends TestCase {
  @Test
  public void testReplacementAllVisible() {
    String s = "A–A—A―A‖A‗A‚A‛A“A”A‘A’A„A…A′A″AâA";
    ValidationHelper vh = new ValidationHelper();
    String sFixed = vh.convertSpecialCharacters(s);
    assertEquals(sFixed, "A-A-A-A|A_A,A'A\"A\"A'A'A\"A...A'A\"AâA");
  }

  @Test
  public void testReplacementTypical() {
    String s = "Maintain Don't Gain “test” \n" +
        "‘test’ \n" +
        "Here is some text with a dash – and some other text";
    ValidationHelper vh = new ValidationHelper();
    String sFixed = vh.convertSpecialCharacters(s);
    assertEquals(sFixed, "Maintain Don't Gain \"test\" \n" +
        "'test' \n" +
        "Here is some text with a dash - and some other text");
  }


  @Test
  public void testReplacementControlCharacters() {
    String s = "â â\u0080\u0098A\u0093\u0099\u009C\u009D";
    ValidationHelper vh = new ValidationHelper();
    String sFixed = vh.convertSpecialCharacters(s);
    assertEquals(sFixed, "â A");
  }
}