package com.it10x.foodappgstav7_09.printer.queue

import android.util.Log
import com.it10x.foodappgstav7_09.data.printqueue.PrintQueueEntity
import com.it10x.foodappgstav7_09.data.PrinterRole
import com.it10x.foodappgstav7_09.data.printqueue.PrintQueueDao

import com.it10x.foodappgstav7_09.printer.PrinterManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.UUID
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class PrintQueueManager(
    private val dao: PrintQueueDao,
    private val printerManager: PrinterManager
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val billingChannel = Channel<PrintQueueEntity>(Channel.UNLIMITED)

    init {
        startWorker(billingChannel)

        // Restore pending jobs on app start
        scope.launch {
            loadPendingJobs()
        }
    }

    suspend fun enqueue(role: PrinterRole, text: String) {

        val job = PrintQueueEntity(
            id = UUID.randomUUID().toString(),
            role = role.name,
            text = text,
            status = "PENDING",
            retryCount = 0,
            createdAt = System.currentTimeMillis()
        )

        dao.insert(job)
        billingChannel.send(job)
    }

    private fun startWorker(channel: Channel<PrintQueueEntity>) {
        scope.launch {
            for (job in channel) {
                processJob(job)
            }
        }
    }

    private suspend fun processJob(job: PrintQueueEntity) {

        dao.updateStatus(job.id, "PRINTING", job.retryCount)

        Log.d("PRINT_QUEUE", "Printing job ${job.id}")

        suspendCancellableCoroutine<Unit> { cont ->

            printerManager.printText(
                PrinterRole.valueOf(job.role),
                job.text
            ) {
                if (cont.isActive) cont.resume(Unit)
            }
        }

        // ✅ ALWAYS mark success after first attempt
        dao.delete(job.id)

        Log.d("PRINT_QUEUE", "DONE ${job.id}")
    }

    private suspend fun loadPendingJobs() {
        val jobs = dao.getPending()
        jobs.forEach {
            billingChannel.send(it)
        }
    }
}