package com.github.mckernant1.fs

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Duration

internal class FileHandlerTest {

    private val fileHandler = FileHandler(
        logger = System.out
    )

    private val fileHandler2 = FileHandler(
        location = "fstore",
        duration = Duration.ofMillis(1)
    )

    @Test
    fun getWithFile() {
        val x = {
            2 + 2
        }
        fileHandler.getResult("four", x)
        assertTrue(File("store/four.ser").exists())
        fileHandler.getResult("four", x)
    }

    @Test
    fun getWithExpired() {
        val x = {
            2 + 3
        }
        fileHandler.getResult("five", x)
        assertTrue(File("store/five.ser").exists())
        fileHandler.getResult("five", x)
    }
}
