package com.github.mysterix5.vover.static_tools;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StringOperationsTest {

    @Test
    void isWord() {
        assertThat(StringOperations.isWord("I")).isTrue();
        assertThat(StringOperations.isWord("Test")).isTrue();
        assertThat(StringOperations.isWord("Töst")).isTrue();
        assertThat(StringOperations.isWord("André")).isTrue();
        assertThat(StringOperations.isWord("superdupereinfachfantastischer")).isTrue();
        assertThat(StringOperations.isWord("superdupereinfachfantastischert")).isFalse();
        assertThat(StringOperations.isWord(" Test")).isFalse();
        assertThat(StringOperations.isWord("Test1")).isFalse();
        assertThat(StringOperations.isWord("\\Test")).isFalse();
        assertThat(StringOperations.isWord("ein Test")).isFalse();
        assertThat(StringOperations.isWord(" ")).isFalse();
        assertThat(StringOperations.isWord("")).isFalse();
        assertThat(StringOperations.isWord("Test ")).isFalse();
        assertThat(StringOperations.isWord("Te st")).isFalse();
        assertThat(StringOperations.isWord("Te/st")).isFalse();
    }
    @Test
    void isUsername() {
        assertThat(StringOperations.isUsername("Test")).isTrue();
        assertThat(StringOperations.isUsername("Test1")).isTrue();
        assertThat(StringOperations.isUsername("1Test")).isTrue();
        assertThat(StringOperations.isUsername("1_Test")).isTrue();
        assertThat(StringOperations.isUsername("Test_4")).isTrue();
        assertThat(StringOperations.isUsername("1-Test")).isTrue();
        assertThat(StringOperations.isUsername("superduperkrasserTyp")).isTrue();
        assertThat(StringOperations.isUsername("superduperkrasserTyp5")).isFalse();
        assertThat(StringOperations.isUsername("-Test")).isFalse();
        assertThat(StringOperations.isUsername("_Test")).isFalse();
        assertThat(StringOperations.isUsername("Test_")).isFalse();
        assertThat(StringOperations.isUsername("Test-")).isFalse();
        assertThat(StringOperations.isUsername("Test_-4")).isFalse();
        assertThat(StringOperations.isUsername("Test__4")).isFalse();
        assertThat(StringOperations.isUsername("Test--4")).isFalse();
        assertThat(StringOperations.isUsername("Test-_4")).isFalse();
        assertThat(StringOperations.isUsername("Test_-test")).isFalse();
        assertThat(StringOperations.isUsername("Töst")).isFalse();
        assertThat(StringOperations.isUsername("André")).isFalse();
        assertThat(StringOperations.isUsername("I")).isFalse();
        assertThat(StringOperations.isUsername(" Test")).isFalse();
        assertThat(StringOperations.isUsername("\\Test")).isFalse();
        assertThat(StringOperations.isUsername("ein Test")).isFalse();
        assertThat(StringOperations.isUsername(" ")).isFalse();
        assertThat(StringOperations.isUsername("")).isFalse();
        assertThat(StringOperations.isUsername("Test ")).isFalse();
        assertThat(StringOperations.isUsername("Te st")).isFalse();
        assertThat(StringOperations.isUsername("Te/st")).isFalse();
    }

    @Test
    void splitText() {
        assertThat(StringOperations.splitText("ein topf voll gold")).containsExactly("ein", "topf", "voll", "gold");
        assertThat(StringOperations.splitText("eintopf voll gold   ")).containsExactly("eintopf", "voll", "gold");
        assertThat(StringOperations.splitText("  eintopf  voll | gold")).containsExactly("eintopf", "voll", "|", "gold");
        assertThat(StringOperations.splitText("  ")).isEmpty();
        assertThat(StringOperations.splitText("")).isEmpty();
        assertThat(StringOperations.splitText("\n")).isEmpty();
    }
}