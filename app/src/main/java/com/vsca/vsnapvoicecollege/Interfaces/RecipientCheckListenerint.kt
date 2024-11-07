package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelectedint

interface RecipientCheckListenerint {

    fun add(data: RecipientSelectedint?)
    fun remove(data: RecipientSelectedint?)
}