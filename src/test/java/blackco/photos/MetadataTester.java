package blackco.photos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MetadataTester {

	public static void main(String[] args) {
		String dir = "/Users/blackco/Documents/java/src/photos/web-seed/angular-seed/app/metadata";
	    MetadataTester meta = new MetadataTester();
	    String filename = "/Users/blackco/Pictures/test2/IMG_0380.jpg";
	    File f = new File(filename);
	    
	    if (f.exists()) {
	    	
	    	meta.printAllTags(f);
	    	/*
	        FileWriter f1;
			try {
				//f1 = new FileWriter(dir + "/Users/blackco/Pictures/test2/IMG_0380.json");
				//meta.serialize(f1, meta.printAllTags(f));
				
				meta.printAllTags(f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        */
	    } else {
	        System.out.println("cannot find file: " + filename);
	    }
	}
	
	private final static ObjectMapper mapper;
	   static {
	      mapper = new ObjectMapper();
	}

	public static String serialize(FileWriter f1, Object object) {
	      try {
	    	  
	    	  try {
				mapper.writeValue(f1, object);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	  
	         return mapper.writeValueAsString(object);
	      } catch (JsonProcessingException e) {
	         e.printStackTrace();
	      }
	      return null;
	   }
    void readAndDisplayMetadata( String fileName ) {
        try {

            File file = new File( fileName );
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {

                // pick the first available ImageReader
                ImageReader reader = readers.next();

                // attach source to the reader
                reader.setInput(iis, true);

                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                String[] names = metadata.getMetadataFormatNames();
                int length = names.length;
                for (int i = 0; i < length; i++) {
                    System.out.println( "Format name: " + names[ i ] );
                    displayMetadata(metadata.getAsTree(names[i]));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Metadata printAllTags(File jpegFile) {

		Metadata metadata = null;
    	try {
			 metadata = ImageMetadataReader.readMetadata(jpegFile);

			for (Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					System.out.println(tag);
				}
			}
		} catch (Exception e) {
			System.err.println("UsingDrewNoakes.printAllTags(): error");
			e.printStackTrace();
		}
    	
    	return metadata;
	}
    
    void displayMetadata(Node root) {
        displayMetadata(root, 0);
    }

    void indent(int level) {
        for (int i = 0; i < level; i++)
            System.out.print("    ");
    }

    void displayMetadata(Node node, int level) {
        // print open tag of element
        indent(level);
        System.out.print("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null) {

            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                System.out.print(" " + attr.getNodeName() +
                                 "=\"" + attr.getNodeValue() + "\"");
            }
        }

        Node child = node.getFirstChild();
        if (child == null) {
            // no children, so close element and return
            System.out.println("/>");
            return;
        }

        // children, so close current tag
        System.out.println(">");
        while (child != null) {
            // print children recursively
            displayMetadata(child, level + 1);
            child = child.getNextSibling();
        }

        // print close tag of element
        indent(level);
        System.out.println("</" + node.getNodeName() + ">");
    }

}