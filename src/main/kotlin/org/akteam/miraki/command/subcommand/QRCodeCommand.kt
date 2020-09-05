package org.akteam.miraki.command.subcommand

import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.NotFoundException
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.detector.Detector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.queryUrl
import okhttp3.OkHttpClient
import org.akteam.miraki.command.Intent
import org.akteam.miraki.command.NaturalCommand
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.UserLevel
import org.akteam.miraki.util.BotUtils.get
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


class QRCodeCommand : NaturalCommand {
    private val client = OkHttpClient()

    override suspend fun entry(event: MessageEvent, user: BotUser) {
        val img = event.message[Image]
        if (img != null) {
            val imgUrl = img.queryUrl()
            val rep = client.get(imgUrl)
            if (rep.isSuccessful && rep.body != null) {
                val bitmap = withContext(Dispatchers.IO) {
                    val image: BufferedImage = ImageIO.read(rep.body!!.byteStream())
                    val source: LuminanceSource = BufferedImageLuminanceSource(image)
                    BinaryBitmap(HybridBinarizer(source))
                }
                val result = QRCodeReader().decode(bitmap)
                event.reply(PlainText(result.text))
            }
        }
    }

    override suspend fun intent(event: MessageEvent, user: BotUser): Intent {
        val img = event.message[Image]
        if (img != null) {
            val imgUrl = img.queryUrl()
            val rep = client.get(imgUrl)
            if (rep.isSuccessful && rep.body != null) {
                val bitmap = withContext(Dispatchers.IO) {
                    val image: BufferedImage = ImageIO.read(rep.body!!.byteStream())
                    val source: LuminanceSource = BufferedImageLuminanceSource(image)
                    BinaryBitmap(HybridBinarizer(source))
                }
                return try {
                    Detector(bitmap.blackMatrix).detect()
                    Intent(80, this)
                } catch (t: NotFoundException) {
                    Intent(0, null)
                }
            }
        }
        return Intent(0, null)
    }

    override val name: String = "扫码"
    override val userLevel: UserLevel = UserLevel.NORMAL
}