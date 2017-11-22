package org.jasig.portlet.announcements.model.validators;

import junit.framework.TestCase;
import org.junit.Test;

public class UnwantedCharacterScrubberTest extends TestCase {
  @Test
  public void testReplacementAllVisible() {
    String s = "A–A—A―A‖A‗A‚A‛A“A”A‘A’A„A…A′A″AâA";
    UnwantedCharacterScrubber vh = new UnwantedCharacterScrubber();
    String sFixed = vh.scrubUnwantedCharacters(s);
    assertEquals(sFixed, "A-A-A-A|A_A,A'A\"A\"A'A'A\"A...A'A\"AâA");
  }

  @Test
  public void testReplacementTypical() {
    String s = "Maintain Don't Gain “test” \n" +
        "‘test’ \n" +
        "Here is some text with a dash – and some other text";
    UnwantedCharacterScrubber vh = new UnwantedCharacterScrubber();
    String sFixed = vh.scrubUnwantedCharacters(s);
    assertEquals(sFixed, "Maintain Don't Gain \"test\" \n" +
        "'test' \n" +
        "Here is some text with a dash - and some other text");
  }


  @Test
  public void testReplacementControlCharacters() {
    String s = "â â\u0080\u0098A\u0093\u0099\u009C\u009D";
    UnwantedCharacterScrubber vh = new UnwantedCharacterScrubber();
    String sFixed = vh.scrubUnwantedCharacters(s);
    assertEquals(sFixed, "â A");
  }
}