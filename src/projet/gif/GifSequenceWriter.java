package src.projet.gif;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Iterator;
import java.io.File;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class GifSequenceWriter {
    private ImageWriter gifWriter;
    private ImageWriteParam imageWriteParam;
    private IIOMetadata imageMetaData;
    private File ajouteGIF;
    private boolean avant;

    public GifSequenceWriter(ImageOutputStream outputStream, int imageType, int timeBetweenFramesMS, boolean loopContinuously, File ajouteGIF, boolean avant) throws IIOException, IOException {
        gifWriter = getWriter();
        imageWriteParam = gifWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
        this.ajouteGIF = ajouteGIF;
        this.avant = avant;

        imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by MAH");

        IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");

        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop = loopContinuously ? 0 : 1;

        child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        gifWriter.setOutput(outputStream);

        gifWriter.prepareWriteSequence(null);

        if(ajouteGIF != null && avant){
            extraitEtAjouteFrames(ajouteGIF);
        }
    }

    private static ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Exist");
        } else {
            return iter.next();
        }
    }

    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }

    public void writeToSequence(RenderedImage img) throws IOException {
        gifWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
    }

    /**
     * Extrait les frames d'un gif pré-existant afin de les ajouter au GIF final
     * @param fichierGif
     * @throws IOException
     */
    public void extraitEtAjouteFrames(File fichierGif) throws IOException {
        try{
            ImageInputStream stream = ImageIO.createImageInputStream(fichierGif);
            ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
            if (reader == null) {
                throw new IIOException("Erreur GIF");
            }
            reader.setInput(stream);
            int numFrames = reader.getNumImages(true);

            // on ajoute chaque frame :
            for (int i = 0; i < numFrames; i++) {
                writeToSequence(reader.read(i));
            }
        }catch(IIOException e){       
        }
    }

    public void close() throws IOException {
        if(ajouteGIF != null && !avant){
            extraitEtAjouteFrames(ajouteGIF);
        }
        gifWriter.endWriteSequence();
    }
}
