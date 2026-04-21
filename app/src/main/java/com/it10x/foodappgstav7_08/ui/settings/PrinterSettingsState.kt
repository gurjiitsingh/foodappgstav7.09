package com.it10x.foodappgstav7_09.ui.settings

import com.it10x.foodappgstav7_09.data.PrinterConfig
import com.it10x.foodappgstav7_09.data.PrinterRole

data class PrinterSettingsState(
    val printers: Map<PrinterRole, PrinterConfig> = emptyMap()
)
