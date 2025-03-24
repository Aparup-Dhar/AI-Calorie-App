package com.example.calorie_app_prototype

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CaloriePredictionModel(assetManager: AssetManager, modelPath: String, labelPath: String) {
    private val tflite: Interpreter
    private val labels: List<String>

    init {
        tflite = Interpreter(loadModelFile(assetManager, modelPath))
        labels = loadLabels(assetManager, labelPath)
    }

    private fun loadModelFile(assetManager: AssetManager, modelFile: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabels(assetManager: AssetManager, labelFile: String): List<String> {
        return assetManager.open(labelFile).bufferedReader().use { it.readLines() }
    }

    fun predict(bitmap: Bitmap): Pair<Bitmap, String> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)

        val outputMap = Array(1) { FloatArray(labels.size) }
        tflite.run(inputBuffer, outputMap)

        val predictedLabelIndex = outputMap[0].indices.maxByOrNull { outputMap[0][it] } ?: -1
        val predictedLabel = labels.getOrNull(predictedLabelIndex) ?: "Unknown"

        return Pair(resizedBitmap, predictedLabel)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        buffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(224 * 224)

        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in intValues.indices) {
            val pixelValue = intValues[i]
            buffer.putFloat(((pixelValue shr 16 and 0xFF) / 255.0f)) // R
            buffer.putFloat(((pixelValue shr 8 and 0xFF) / 255.0f)) // G
            buffer.putFloat((pixelValue and 0xFF) / 255.0f) // B
        }

        return buffer
    }
}
