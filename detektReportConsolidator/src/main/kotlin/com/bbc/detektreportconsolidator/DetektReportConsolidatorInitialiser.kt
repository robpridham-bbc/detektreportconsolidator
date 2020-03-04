@file:JvmName("DetektReportConsolidatorMain")
package com.bbc.detektreportconsolidator

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 3) commandSyntaxError(args)

    val inputDirName = args.getOrNull(0)
    val outputDirName = args.getOrNull(1)
    val outputFilename = args.getOrNull(2)

    inputDirName ?: commandSyntaxError(args)
    outputDirName ?: commandSyntaxError(args)
    outputFilename ?: commandSyntaxError(args)

    if (inputDirName != null && outputDirName != null && outputFilename != null) {
        DetektReportConsolidatorInitialiser(inputDirName, outputDirName, outputFilename).initialise()
    }
}

fun commandSyntaxError(args: Array<String>) {
    System.err.println("Invalid arguments: ${args.joinToString(" ")}")
    System.err.println("Tool syntax: toolname [input directory] [output directory] [output filename]")
    exitProcess(-1)
}

class DetektReportConsolidatorInitialiser(private val inputDirName: String, private val outputDirName: String, private val outputFilename: String) {

    fun initialise() {
        val allFileContent = mutableListOf<String>()
        File(inputDirName).walk().filter { it.isFile && it.name != outputFilename && it.extension == "yml" }.forEach {
            allFileContent.addAll(it.readLines())
        }
        val filteredLines = allFileContent.filterNot {
            it.startsWith("<?xml") || it.startsWith("<checkstyle") || it.startsWith("</checkstyle")
        }

        val wholeFile = mutableListOf<String>()
        wholeFile.add("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
        wholeFile.add("<checkstyle version=\"4.3\">")
        wholeFile.addAll(filteredLines)
        wholeFile.add("</checkstyle>")

        File(outputDirName).mkdirs()

        val output = wholeFile.joinToString("\n")
        File(outputDirName, outputFilename).writeText(output)
    }
}