package com.ohmycthulhu.businesscarddatabase.utils.recognizer

import java.util.regex.Pattern

enum class RecognizePatterns(val pattern: Pattern) {
    NAME(Pattern.compile("[A-Z][a-z]+.*?\\s[A-Z].*")),
    PHONE(Pattern.compile("\\+?[0-9\\s)(]+")),
    // PHONE(Pattern.compile("([0-9]{1,3})[.)( ]*([0-9]{2,3})[.)( ]*([0-9]{4})")),
    EMAIL(Pattern.compile("([a-zA-Z0-9._\\-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_\\-]+)")),
    WEBSITE(Pattern.compile("[a-z.-]+\\.[a-z]+"))
    // EMAIL(Pattern.compile("^.+@.+$"))
}