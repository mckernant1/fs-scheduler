package com.github.mckernant1.fs

import java.io.*
import java.time.Duration
import java.time.LocalDateTime

class FileHandler(
    private val location: String = "store",
    private val duration: Duration = Duration.ofHours(1),
    private val logger: PrintStream = PrintStream(object : OutputStream() {
        override fun write(p0: Int) {}
    })
) {

    private val storageFolder: File = File(location)

    init {
        storageFolder.mkdir()
    }

    fun <T : Serializable> getResult(name: String, alternativeFunction: () -> T): T {
        return when (val retrieved = retrieve<T>(name)) {
            null -> {
                log("Did not retrieve file. Calling alternative function")
                val res = alternativeFunction.invoke()
                save(name, res)
                res
            }
            else -> {
                log("Returning retrieved object")
                retrieved
            }
        }

    }

    private fun <T : Serializable> save(name: String, itemToStore: T): T {
        val content = FileContent(
            name,
            LocalDateTime.now(),
            itemToStore
        )
        val file = File(storageFolder, "$name.ser")
        ObjectOutputStream(FileOutputStream(file)).use { it.writeObject(content) }
        return itemToStore
    }

    @Throws
    private fun <T : Serializable> retrieve(name: String): T? {
        val file = File(storageFolder,"$name.ser")
        if (!file.exists()) {
            log("File does not exist")
            return null
        }
        val content =
            when (val anyObj = ObjectInputStream(FileInputStream(file)).readObject()) {
                is FileContent<*> -> anyObj
                else -> error("")
            }
        return if (Duration.between(content.date, LocalDateTime.now()) < duration) {
            content.storedItem as T
        } else {
            log("File is expired. Deleting")
            file.delete()
            null
        }
    }

    private fun log(s: String) = logger.println("[FileHandler] $s")

    private data class FileContent<T : Serializable>(
        val fileName: String,
        val date: LocalDateTime,
        val storedItem: T
    ) : Serializable
}


