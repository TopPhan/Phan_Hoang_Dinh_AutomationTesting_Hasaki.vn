package com.utility.Capture;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.log.logTest;
import com.utility.PropertiesFile;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class CaptureVideo extends ScreenRecorder {

    // ------Record with Monte Media library---------
    private static ThreadLocal<ScreenRecorder> screenRecorderThreadLocal = new ThreadLocal<>();
    public String name;

    //Hàm xây dựng
    public CaptureVideo(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat, Format screenFormat,
                        Format mouseFormat, Format audioFormat, File movieFolder, String name) throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.name = name;
    }

    //Hàm này bắt buộc để ghi đè custom lại hàm trong thư viên viết sẵn
    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {

        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        return new File(movieFolder,
                name + "-" + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
    }

    // Hàm Start record video
    public static void startRecord(String methodName) throws Exception {
        //Tạo thư mục để lưu file video vào
        File file = new File(PropertiesFile.getPropValue("test-recordings"));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        Rectangle captureSize = new Rectangle(0, 0, width, height);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration();
        ScreenRecorder screenRecorder = new CaptureVideo(gc, captureSize,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                        Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                null, file, methodName);

        // Lưu vào ThreadLocal trước khi start
        screenRecorderThreadLocal.set(screenRecorder);
        screenRecorderThreadLocal.get().start();
    }

    // Stop record video
    public static void stopRecord() throws Exception {
        // Chỉ stop bộ ghi của luồng hiện tại
        if (screenRecorderThreadLocal.get() != null) {
            screenRecorderThreadLocal.get().stop();
            // Quan trọng: remove để giải phóng bộ nhớ
            screenRecorderThreadLocal.remove();
        }
    }

    // If Test success delete video
    public static void deleteVideo(String fileName) {
        try {
            // Lấy đường dẫn thư mục từ config
            String folderPath = PropertiesFile.getPropValue("test-recordings");
            File directory = new File(folderPath);
            File[] files = directory.listFiles();

            if (files != null) {
                for (File f : files) {
                    // Tìm file có chứa tên method vừa chạy (tên file Monte tạo ra có timestamp ở đuôi)
                    if (f.getName().startsWith(fileName) && f.getName().endsWith(".avi")) {
                        f.delete();
                        logTest.info("🗑️ Deleted passed test video: " + f.getName());
                    }
                }
            }
        } catch (Exception e) {
            logTest.error("❌ Cannot delete video: " + e.getMessage());
        }
    }
}
