package com.kuuhakuLL.tmailer.integration

import org.springframework.integration.mail.SearchTermStrategy
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.search.AndTerm
import javax.mail.search.FlagTerm
import javax.mail.search.SearchTerm

class SearchUnreadMessagesStrategy : SearchTermStrategy {
    override fun generateSearchTerm(supportedFlags: Flags?, folder: Folder?): SearchTerm {
        val flagTerm = FlagTerm(Flags(Flags.Flag.SEEN), false)
        val searchTermArray = arrayOfNulls<SearchTerm>(1)
        searchTermArray[0] = flagTerm
        return AndTerm(searchTermArray)
    }
}