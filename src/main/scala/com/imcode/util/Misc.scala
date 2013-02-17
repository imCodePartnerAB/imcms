package com.imcode.util

object Misc {
  /**
   * Converts camel-case string into underscore.
   * ex: IPAccess => ip_access, SearchTerms => search_terms, mrX => mr_x, iBot => i_bot
   */
  def camelCaseToUnderscore(s: String): String = {
    def camelCaseToUnderscore(chars: List[Char]): List[Char] =
      chars.span(c => c.isLower || !c.isLetter) match {
        case (lowers, Nil) => lowers
        case (Nil, rest) => (rest.span(_.isUpper) : @unchecked) match {
          case (u :: Nil, rest2) => camelCaseToUnderscore(u.toLower :: rest2)
          case (uppers@(u1 :: u2 :: _), rest2) =>
            if (rest2.isEmpty) uppers.map(_.toLower)
            else uppers.init.map(_.toLower) ++ ('_' :: camelCaseToUnderscore(uppers.last.toLower :: rest2))
        }
        case (lowers, rest) => lowers ++ ('_' :: camelCaseToUnderscore(rest))
      }

    camelCaseToUnderscore(s.toList).mkString
  }
}
