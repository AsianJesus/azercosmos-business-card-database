package com.ohmycthulhu.businesscarddatabase

import java.util.regex.Pattern

enum class RecognizePatterns(val pattern: Pattern) {
    NAME(Pattern.compile("[A-Z][a-z]+.*?\\s[A-Z].*")),
    PHONE(Pattern.compile("(?:Pho.*?\\s|Mo.*?\\s)(.*)")),
    EMAIL(Pattern.compile("^.+@.+$"))
}