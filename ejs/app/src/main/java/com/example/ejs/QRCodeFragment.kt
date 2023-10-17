package com.example.ejs

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import kotlinx.android.synthetic.main.fragment_qr.view.imageQr
import java.io.File

class QRCodeFragment : DialogFragment() {
    lateinit var v: View
    var kd = ""
    lateinit var urlClass: UrlClass

    private val PERMISSION_REQUEST_CODE = 1
    private val FILENAME = "qrcode.pdf"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_qr, container, false)

        urlClass = UrlClass()

        val data = arguments
        kd = data?.get("kode").toString()
        Toast.makeText(v.context, kd, Toast.LENGTH_SHORT).show()

        val url = urlClass.local+"detail_arsip.php?id="+kd // 10.0.2.2 adalah IP address yang digunakan untuk mengakses localhost pada emulator Android
        val bitmap = generateBarcode(url)
        v.imageQr.setImageBitmap(bitmap)

        v.imageQr.setOnLongClickListener {
            var contextMenu = PopupMenu(it.context, it)
            contextMenu.menuInflater.inflate(R.menu.unduh_qr, contextMenu.menu)
            contextMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.unduhQr -> {
                        if (isWritePermissionGranted()) {
                            generateBarcodeAndSaveAsPDF()
                        } else {
                            requestWritePermission()
                        }
                    }
                }
                false
            }
            contextMenu.show()
            true
        }

        return v
    }

    private fun generateBarcodeAndSaveAsPDF() {
        val url = urlClass.local+"detail_arsip.php?id="+kd
        val bitmap = generateBarcode(url)

        if (isWritePermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveBitmapAsPDF(bitmap, FILENAME)
            }
        } else {
            requestWritePermission()
        }
    }

    private fun generateBarcode(content: String): Bitmap {
        val width = 400
        val height = 400
        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
        return bitMatrixToBitmap(bitMatrix)
    }

    private fun bitMatrixToBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveBitmapAsPDF(bitmap: Bitmap, filename: String) {
        val directory = File(Environment.getExternalStorageDirectory().toString() + "/QRCode")
        directory.mkdirs()

        val pdfFilePath = File(directory, filename)

        val pdfWriter = PdfWriter(pdfFilePath.absolutePath)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageData = ImageDataFactory.create(byteArrayOutputStream.toByteArray())
        val image = Image(imageData)

        document.add(image)

        document.close()
        pdfDocument.close()

        showToast("Barcode saved as PDF successfully")
    }

    private fun isWritePermissionGranted(): Boolean {
        val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permissionResult = ContextCompat.checkSelfPermission(this.requireContext(), writePermission)
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWritePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generateBarcodeAndSaveAsPDF()
            } else {
                showToast("Write permission denied. Cannot save PDF.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }
}