package it.units.erallab.hmsrobots.viewers;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jcodec.scale.AWTUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author eric
 */
public class VideoUtils {

  public enum EncoderFacility {JCODEC, FFMPEG_LARGE, FFMPEG_SMALL}

  private static final EncoderFacility DEFAULT_ENCODER = EncoderFacility.JCODEC;
  private static final Logger L = Logger.getLogger(VideoUtils.class.getName());

  private VideoUtils() {
  }

  public static void encodeAndSave(List<BufferedImage> images, double frameRate, File file) throws IOException {
    encodeAndSave(images, frameRate, file, DEFAULT_ENCODER);
  }

  public static void encodeAndSave(List<BufferedImage> images, double frameRate, File file, EncoderFacility encoder) throws IOException {
    switch (encoder) {
      case JCODEC -> encodeAndSaveWithJCodec(images, frameRate, file);
      case FFMPEG_LARGE -> encodeAndSaveWithFFMpeg(images, frameRate, file, 18);
      case FFMPEG_SMALL -> encodeAndSaveWithFFMpeg(images, frameRate, file, 30);
    }
  }

  private static void encodeAndSaveWithJCodec(List<BufferedImage> images, double frameRate, File file) throws IOException {
    SeekableByteChannel channel = NIOUtils.writableChannel(file);
    SequenceEncoder encoder = new SequenceEncoder(
        channel,
        Rational.R((int) Math.round(frameRate), 1),
        Format.MOV,
        org.jcodec.common.Codec.H264,
        null
    );
    //encode
    try {
      for (BufferedImage image : images) {
        encoder.encodeNativeFrame(AWTUtil.fromBufferedImageRGB(image));
      }
    } catch (IOException ex) {
      L.severe(String.format("Cannot encode image due to %s", ex));
    }
    encoder.finish();
    NIOUtils.closeQuietly(channel);
  }

  private static void encodeAndSaveWithFFMpeg(List<BufferedImage> images, double frameRate, File file, int compression) throws IOException {
    //save all files
    String workingDirName = file.getAbsoluteFile().getParentFile().getPath();
    String imagesDirName = workingDirName + File.separator + "imgs." + System.currentTimeMillis();
    Files.createDirectories(Path.of(imagesDirName));
    List<Path> toDeletePaths = new ArrayList<>();
    L.fine(String.format("Saving %d files in %s", images.size(), imagesDirName));
    for (int i = 0; i < images.size(); i++) {
      File imageFile = new File(imagesDirName + File.separator + String.format("frame%06d", i) + ".jpg");
      ImageIO.write(images.get(i), "jpg", imageFile);
      toDeletePaths.add(imageFile.toPath());
    }
    toDeletePaths.add(Path.of(imagesDirName));
    //invoke ffmpeg
    String command = String.format(
        "ffmpeg -y -r %d -i %s/frame%%06d.jpg -vcodec libx264 -crf %d -pix_fmt yuv420p %s",
        (int) Math.round(frameRate),
        imagesDirName,
        compression,
        file.getPath()
    );
    L.fine(String.format("Running: %s", command));
    ProcessBuilder pb = new ProcessBuilder(command.split(" "));
    pb.directory(new File(workingDirName));
    StringBuilder sb = new StringBuilder();
    try {
      Process process = pb.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      reader.close();
      int exitVal = process.waitFor();
      if (exitVal < 0) {
        throw new IOException(String.format("Unexpected exit val: %d. Full output is:%n%s", exitVal, sb.toString()));
      }
    } catch (IOException | InterruptedException e) {
      throw (e instanceof IOException) ? (IOException) e : (new IOException(e));
    } finally {
      //delete all files
      L.fine(String.format("Deleting %d paths", toDeletePaths.size()));
      for (Path path : toDeletePaths) {
        try {
          Files.delete(path);
        } catch (IOException e) {
          L.log(Level.WARNING, String.format("Cannot delete %s", path), e);
        }
      }
    }
  }

}
