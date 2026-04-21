package com.it10x.foodappgstav7_09.fiscal

import com.it10x.foodappgstav7_09.fiskaly.GermanyFiscalService
import com.it10x.foodappgstav7_09.fiskaly.IndiaFiscalService
import com.it10x.foodappgstav7_09.fiskaly.SpainFiscalService
import com.it10x.foodappgstav7_09.fiskaly.FiskalyRepository

fun getFiscalService(
    country: String,
    fiskalyRepository: FiskalyRepository? = null
): FiscalService {

    return when (country) {
        "DE" -> GermanyFiscalService(fiskalyRepository!!)
        "IN" -> IndiaFiscalService()
        "ES" -> SpainFiscalService()
        else -> IndiaFiscalService()
    }
}