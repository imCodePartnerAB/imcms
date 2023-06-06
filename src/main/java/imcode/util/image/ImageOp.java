package imcode.util.image;

import imcode.util.ImcmsImageUtils;
import imcode.util.io.FileUtility;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageOp {
    private static final Log log = LogFactory.getLog(ImageOp.class);

    private static final boolean PLATFORM_WINDOWS = System.getProperty("os.name").contains("Windows");

    private static final Pattern FORMAT_PATTERN = Pattern.compile("format:'([^']+)'");
    private static final Pattern WIDTH_PATTERN = Pattern.compile("width:'([^']+)'");
    private static final Pattern HEIGHT_PATTERN = Pattern.compile("height:'([^']+)'");

    private List<String> args = new ArrayList<>();
    private byte[] inputData;
    private InputStream dataStream;
    private Format outputFormat;

    public ImageOp(String imageMagickPath) {
        args.add(addQuotes(getApplicationPath(imageMagickPath, "convert")));
    }

    private static String getApplicationPath(String imageMagickPath, String appName) {
        if (imageMagickPath != null
                && !"".equals(imageMagickPath)
                && SystemUtils.IS_OS_WINDOWS)
        {
            return new File(imageMagickPath, appName).getAbsolutePath();
        }

        return appName;
    }

    private static String addQuotes(String input) {
        if (PLATFORM_WINDOWS) {
            return "\"" + input + "\"";
        }

        return input;
    }

    public static ImageInfo getImageInfo(File file) {
        try {
            String fileToIdentify = addQuotes(file.getAbsolutePath() + "[0]");
            final String[] processArgs = getIdentifyProcessArgs(ImcmsImageUtils.imageMagickPath, fileToIdentify);
            Process process = new ProcessBuilder(processArgs).start();

            StringInputStreamHandler errorHandler = new StringInputStreamHandler(process.getErrorStream());
            StringInputStreamHandler inputHandler = new StringInputStreamHandler(process.getInputStream());
            errorHandler.start();
            inputHandler.start();
            inputHandler.join();

            return processImageInfo(inputHandler);
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
        }
        return null;
    }

    public static ImageInfo getImageInfo(String imageMagickPath, InputStream inputStream) {
        try {
            Process process = new ProcessBuilder(getIdentifyProcessArgs(imageMagickPath, "-[0]")).start();
            StringInputStreamHandler errorHandler = new StringInputStreamHandler(process.getErrorStream());
            StringInputStreamHandler inputHandler = new StringInputStreamHandler(process.getInputStream());
            errorHandler.start();
            inputHandler.start();

            OutputStream output = null;
            try {
                output = process.getOutputStream();
                IOUtils.copy(inputStream, output);
            } finally {
                IOUtils.closeQuietly(output);
            }

            inputHandler.join();

            return processImageInfo(inputHandler);
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return null;
    }

    private static String[] getIdentifyProcessArgs(String imageMagickPath, String... arguments) {
        String[] args = new String[4 + arguments.length];

        args[0] = addQuotes(getApplicationPath(imageMagickPath, "identify"));

        args[1] = "-quiet";
        args[2] = "-format";
        args[3] = "format:'%m'width:'%w'height:'%h'";

        int startIndex = 4;

        for (String arg : arguments) {
            args[startIndex++] = arg;
        }

        return args;
    }

    private static ImageInfo processImageInfo(StringInputStreamHandler inputHandler) {
        if (inputHandler.getData() != null) {
            String input = inputHandler.getData();

            Matcher formatMatcher = FORMAT_PATTERN.matcher(input);
            Format format = null;
            if (formatMatcher.find()) {
                format = Format.findFormat(formatMatcher.group(1));
            }

            Matcher widthMatcher = WIDTH_PATTERN.matcher(input);
            int width = 0;
            if (widthMatcher.find()) {
                try {
                    width = Integer.parseInt(widthMatcher.group(1), 10);
                } catch (Exception ex) {
                    log.warn(ex.getMessage(), ex);
                }
            }

            Matcher heightMatcher = HEIGHT_PATTERN.matcher(input);
            int height = 0;
            if (heightMatcher.find()) {
                try {
                    height = Integer.parseInt(heightMatcher.group(1), 10);
                } catch (Exception ex) {
                    log.warn(ex.getMessage(), ex);
                }
            }

            if (format == null) {
                return null;
            }

            return new ImageInfo(format, width, height);
        }

        return null;
    }

    public ImageOp input(byte[] data) {
        inputData = data;
        args.add("-[0]");

        return this;
    }

    public ImageOp input(InputStream input) {
        dataStream = input;
        args.add(addQuotes("-"));

        return this;
    }

    public ImageOp input(InputStream input, int index) {
        dataStream = input;
        args.add(addQuotes("-["+index+"]"));

        return this;
    }

    public ImageOp input(File file) {
        args.add(addQuotes(file.getAbsolutePath()));

        return this;
    }

    public ImageOp filter(Filter filter) {
        args.add("-filter");
        args.add(addQuotes(filter.getFilter()));

        return this;
    }

    public ImageOp strip() {
        args.add("-strip");

        return this;
    }

    public ImageOp size(int width, int height) {
        args.add("-size");
        args.add(addQuotes(String.format("%dx%d", width, height)));

        return this;
    }

    public ImageOp rawImage(Color color, int width, int height) {
        this.size(width, height);
        args.add(addQuotes("xc:" + color.getColor()));

        return this;
    }

    public ImageOp swap(int index1, int index2) {
        args.add("-swap");
        args.add(addQuotes(String.format("%d,%d", index1, index2)));

        return this;
    }

    public ImageOp swapLastTwo() {
        args.add("+swap");

        return this;
    }

    public ImageOp composite() {
        args.add("-composite");

        return this;
    }

    public ImageOp gravity(Gravity gravity) {
        args.add("-gravity");
        args.add(addQuotes(gravity.getGravity()));

        return this;
    }

    public ImageOp quality(int quality) {
        quality = Math.max(quality, 0);
        quality = Math.min(quality, 100);

        args.add("-quality");
        args.add(addQuotes(Integer.toString(quality, 10)));

        return this;
    }

    public ImageOp crop(int x, int y, int width, int height) {
        args.add("-crop");

        String cropParam = String.format("%dx%d+%d+%d!", width, height, x, y);
        args.add(addQuotes(cropParam));

        return this;
    }

    public ImageOp rotate(int angle) {
        args.add("-rotate");
        args.add(addQuotes(Integer.toString(angle)));

        return this;
    }

    public ImageOp resize(Integer width, Integer height, Resize type) {
        args.add("-resize");

        String size = "";

        if (width != null) {
            size = width.toString();
        }
        if (height != null) {
            size += String.format("x%d", height);
        }

        size += type.getModifier();
        args.add(addQuotes(size));

        return this;
    }

    public ImageOp colors(int colors){
        args.add("-colors");
        args.add(Integer.toString(colors));
        return this;
    }

    public ImageOp format(String expression){
        args.add("-format");
        args.add(addQuotes(expression));
        return this;
    }

    public ImageOp layers(Layer layer){
        args.add("-layers");
        args.add(addQuotes(layer.getLayer()));
        return this;
    }

    public ImageOp comment(String comment){
        args.add("-set");
        args.add("comment");
        args.add(comment);
        return this;
    }

    public ImageOp info(){
        args.add("info:");
        return this;
    }

    public ImageOp resizeProportional(int width, int height, Color backgroundColor, Gravity gravity) {
        this.filter(Filter.LANCZOS);
        this.resize(width, height, Resize.GREATER_THAN);
        this.rawImage(backgroundColor, width, height);
        this.swapLastTwo();
        this.gravity(Gravity.CENTER);
        this.composite();

        return this;
    }

    public ImageOp outputFormat(Format format) {
        this.outputFormat = format;

        return this;
    }

    public byte[] infoProcess(){
        this.info();
        return processToByteArray(args);
    }

    public byte[] processToByteArray() {
        String out = (outputFormat != null ? outputFormat.getFormat() + ":-" : "-");

        List<String> arguments = new ArrayList<>(args);
        arguments.add(addQuotes(out));

        return processToByteArray(arguments);
    }

    private byte[] processToByteArray(List<String> arguments) {
        try {
            Process process = new ProcessBuilder(arguments).start();

            StringInputStreamHandler errorHandler = new StringInputStreamHandler(process.getErrorStream());
            ByteArrayInputStreamHandler dataHandler = new ByteArrayInputStreamHandler(process.getInputStream());
            errorHandler.start();
            dataHandler.start();

            copyData(process);

            if (process.waitFor() != 0) {
                errorHandler.join();
                log.error(errorHandler.getData());
            } else {
                errorHandler.join();
                dataHandler.join();

                return dataHandler.getData();
            }
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(dataStream);
        }

        return null;
    }

    public boolean processToFile(File outputFile) {
        String out = null;
        if (outputFormat != null) {
            out = outputFormat.getFormat() + ":" + outputFile.getAbsolutePath();
        } else {
            out = outputFile.getAbsolutePath();
        }

        List<String> arguments = new ArrayList<>(args);
        arguments.add(addQuotes(out));

        try {
            Process process = new ProcessBuilder(arguments).start();
            StringInputStreamHandler errorHandler = new StringInputStreamHandler(process.getErrorStream());
            errorHandler.start();

            copyData(process);

            if (process.waitFor() != 0) {
                errorHandler.join();
                log.error(errorHandler.getData());

                if (outputFile.exists()) {
                    FileUtility.forceDelete(outputFile);
                }
            } else {
                return true;
            }
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);

            if (outputFile.exists()) {
                try {
                    FileUtility.forceDelete(outputFile);
                } catch (IOException e) {
                    log.error("Can't delete file " + outputFile, e);
                }
            }
        } finally {
            IOUtils.closeQuietly(dataStream);
        }

        return false;
    }

    private void copyData(Process process) throws IOException {
        if (inputData != null || dataStream != null) {
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(process.getOutputStream());

                if (inputData != null) {
                    output.write(inputData);
                } else if (dataStream != null) {
                    IOUtils.copy(dataStream, output);
                }
                IOUtils.closeQuietly(dataStream);
            } finally {
                IOUtils.closeQuietly(output);
            }
        }
    }
}