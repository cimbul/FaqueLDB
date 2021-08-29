package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement

interface Revision {
    val id: String
    val version: Long
    val data: IonElement
}
