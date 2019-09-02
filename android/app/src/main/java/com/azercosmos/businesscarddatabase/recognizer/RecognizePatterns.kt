package com.azercosmos.businesscarddatabase.recognizer

import java.util.regex.Pattern

enum class RecognizePatterns(val pattern: Pattern) {
    NAME(Pattern.compile("[A-Z][a-z]+\\s[A-z][a-z]+'")),
    PHONE(Pattern.compile("\\(?\\+?\\d{0,3}\\)?[ -]?[1-2][1-2]\\)?[ -]?\\d{3}[0 -]\\d{2,}[ -]?\\d{2}")),
    // PHONE(Pattern.compile("([0-9]{1,3})[.)( ]*([0-9]{2,3})[.)( ]*([0-9]{4})")),
    EMAIL(Pattern.compile("\\S+@\\S+")),
    WEBSITE(Pattern.compile("[a-z.-]+\\.[a-z]+")),
    ADDRESS(Pattern.compile("[0-9-A-Za-zA-Za-z ,.]+,+[0-9 A-Za-zA-Za-z,.]+"))
    // EMAIL(Pattern.compile("^.+@.+$"))
}