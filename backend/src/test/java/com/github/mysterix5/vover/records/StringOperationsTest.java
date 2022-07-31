package com.github.mysterix5.vover.records;

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
    void splitText() {
        assertThat(StringOperations.splitText("ein topf voll gold")).containsExactly("ein", "topf", "voll", "gold");
        assertThat(StringOperations.splitText("eintopf voll gold   ")).containsExactly("eintopf", "voll", "gold");
        assertThat(StringOperations.splitText("  eintopf  voll | gold")).containsExactly("eintopf", "voll", "|", "gold");
        assertThat(StringOperations.splitText("  ")).isEmpty();
        assertThat(StringOperations.splitText("")).isEmpty();
        assertThat(StringOperations.splitText("\n")).isEmpty();
    }
}