import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.formdev.flatlaf.FlatIntelliJLaf;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.exceptions.BadRequestException;
import io.imagekit.sdk.exceptions.ConflictException;
import io.imagekit.sdk.exceptions.ForbiddenException;
import io.imagekit.sdk.exceptions.InternalServerException;
import io.imagekit.sdk.exceptions.NotFoundException;
import io.imagekit.sdk.exceptions.PartialSuccessException;
import io.imagekit.sdk.exceptions.TooManyRequestsException;
import io.imagekit.sdk.exceptions.UnauthorizedException;
import io.imagekit.sdk.exceptions.UnknownException;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.RenameFileRequest;
import io.imagekit.sdk.models.results.Result;


public class HTMLGenerator {
	
	public static File img;
	public static BufferedImage image;
	
	public static void main(String[] args) throws IOException, InternalServerException, BadRequestException, UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException, ConflictException, PartialSuccessException, NotFoundException
	{
		//Set up looping variables.
		boolean repeat = true;
		boolean cancel = false;
		Object correct = 1;
		
		//Set up the JFrame used for prompting information.
		FlatIntelliJLaf.setup();
		JFrame frame = new JFrame();
		ImageIcon mainIcon = new ImageIcon("C:\\Users\\retro\\OneDrive\\Documents\\GitHub\\ImageUpload\\ImageUpload\\favicon.ico");
	    Image mainImage = mainIcon.getImage();
		frame.setIconImage(mainImage);
		
		//Initialized the ImageKit SDK.
		ImageKitKeys keys = new ImageKitKeys();
		ImageKit imageKit = ImageKit.getInstance();
		Configuration config = new Configuration(keys.getPublic(), keys.getPrivate(), keys.getEndpoint());
		imageKit.setConfig(config);
		
		while(repeat)
		{
			do
		    {
				//Prompt User to select a jpg file to upload.
				JFileChooser jfc = new JFileChooser();
				File dir = new File("C:\\Users\\retro\\Downloads");
				jfc.setCurrentDirectory(dir);
				FileFilter filter = new FileFilter() {
				    public String getDescription(){return "JPG Images (*.jpg)";}
				    public boolean accept(File f) 
				    {
				        if (f.isDirectory()) return true;
				        else return f.getName().toLowerCase().endsWith(".jpg");
				    }
				};
				jfc.setFileFilter(filter);
			    jfc.showDialog(null,"Select Image:");
			    jfc.setVisible(true);
			    img = jfc.getSelectedFile();
			    if(img == null)
			    {
			    	cancel = true;
			    	return;
			    }
			    
			    //Ensure image is properly rotated.
			    image = ImageIO.read(img);
			    image = rotate(image, -90.0);
			        
			    //Verify that the selected image is correct.
			    ImageIcon icon = new ImageIcon(image);
			    Image newimage = icon.getImage();
			    Image newimg = newimage.getScaledInstance(300, 400,  java.awt.Image.SCALE_SMOOTH);
			    icon = new ImageIcon(newimg);      
			    correct = JOptionPane.showConfirmDialog(frame, null, "Confirm Image:", 0, 0, icon);
			}
			while(correct.equals(1));
		        
		    //Prompt User for new image name, then convert and compress the image.
			Object name = JOptionPane.showInputDialog(frame, "Enter New Image Name");
			if(name == null) 
			{
				cancel = true;
			    return;
			}
			renameImage(name);
			    
			//Prompt User to add Alt Text.
			Object alt = JOptionPane.showInputDialog(frame, "Enter Alt Text:");
			if(alt == null)
			{
			    cancel = true;
			    return;
			}
			  
			//Prompt User for pages to post images to.
			JCheckBox rid = new JCheckBox("Robots in Disguise");
			JCheckBox anim = new JCheckBox("Transformers Animated");
			JCheckBox wfc = new JCheckBox("War for Cybertron Trilogy");
			JCheckBox home = new JCheckBox("Home Page");
			JCheckBox showc = new JCheckBox("Showcase"); 
			Object[] options = new Object[] {rid, anim, wfc, home, showc, "Confirm"};
			    
			JOptionPane.showOptionDialog(null, "Add to which Pages?", "Page Selector", 
			    		JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			    
			String main = "<a href=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" class=\"image-link\">\r\n" + 
				"<img class=\"gal_image\" sizes=\"(min-width: 1260px) 299px, 12.33vw\" srcset=\"https://ik.imagekit.io/theperfectpixl/tr:n-size256/" + name + ".webp 256w,\r\n" + 
				"https://ik.imagekit.io/theperfectpixl/tr:n-size470/" + name + ".webp 470w, https://ik.imagekit.io/theperfectpixl/tr:n-size740/" + name + ".webp 740w,\r\n" + 
				"https://ik.imagekit.io/theperfectpixl/tr:n-size940/" + name + ".webp 940w\" \r\n" + 
				"src=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" height=\"400\" width=\"300\" alt=\"" + alt + "\"></a>";
			File f = new File("C:\\Users\\retro\\OneDrive\\Documents\\GitHub\\ThePerfectPixl.github.io\\Gallery.html");
			addHTML(f, main);
				
			//Generate HTML and post to specified pages.
			if(showc.isSelected())
			{
				f = new File("C:\\Users\\retro\\OneDrive\\Documents\\GitHub\\ThePerfectPixl.github.io\\Showcase.html");
				String show = "<a href=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" class=\"image-link\">\r\n" + 
					"<img class=\"index_image\" sizes=\"(min-width: 1260px) 350px, 20.91vw\" srcset=\"https://ik.imagekit.io/theperfectpixl/tr:n-size256/" + name + ".webp 256w,\r\n" + 
					"https://ik.imagekit.io/theperfectpixl/tr:n-size700/" + name + ".webp 700w, https://ik.imagekit.io/theperfectpixl/tr:n-size1000/" + name + ".webp 1000w,\r\n" + 
					"https://ik.imagekit.io/theperfectpixl/tr:n-size1220/" + name + ".webp 1220w, https://ik.imagekit.io/theperfectpixl/tr:n-size1410/" + name + ".webp 1410w\" \r\n" + 
					"src=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" height=\"400\" width=\"300\" alt=\"" + alt + "\"></a>";
				addHTML (f, show);
			}
			if(home.isSelected())
			{
				f = new File("C:\\Users\\retro\\OneDrive\\Documents\\GitHub\\ThePerfectPixl.github.io\\index.html");
				String show = "<a href=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" class=\"image-link\">\r\n" + 
						"<img class=\"index_image\" sizes=\"(min-width: 1260px) 350px, 20.91vw\" srcset=\"https://ik.imagekit.io/theperfectpixl/tr:n-size256/" + name + ".webp 256w,\r\n" + 
						"https://ik.imagekit.io/theperfectpixl/tr:n-size700/" + name + ".webp 700w, https://ik.imagekit.io/theperfectpixl/tr:n-size1000/" + name + ".webp 1000w,\r\n" + 
						"https://ik.imagekit.io/theperfectpixl/tr:n-size1220/" + name + ".webp 1220w, https://ik.imagekit.io/theperfectpixl/tr:n-size1410/" + name + ".webp 1410w\" \r\n" + 
						"src=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" height=\"400\" width=\"300\" alt=\"" + alt + "\"></a>";
				addHTML (f, show);
			}
			if(rid.isSelected())
			{
				f = new File("C:\\Users\\retro\\OneDrive\\Documents\\GitHub\\ThePerfectPixl.github.io\\RID2001.html");
				String gal = "<a href=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" class=\"image-link\">\r\n" + 
						"<img class=\"gen_image\" sizes=\"(min-width: 1260px) 350px, 15.18vw\" srcset=\"https://ik.imagekit.io/theperfectpixl/tr:n-size256/" + name + ".webp 256w,\r\n" + 
						"https://ik.imagekit.io/theperfectpixl/tr:n-size470/" + name + ".webp 470w, https://ik.imagekit.io/theperfectpixl/tr:n-size740/" + name + ".webp 740w,\r\n" + 
						"https://ik.imagekit.io/theperfectpixl/tr:n-size940/" + name + ".webp 940w\" \r\n" + 
						"src=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" height=\"400\" width=\"300\" alt=\"" + alt + "\"></a>";
				addHTML (f,gal);
			}
			if(anim.isSelected())
			{
				f = new File("C:\\Users\\retro\\OneDrive\\Documents\\GitHub\\ThePerfectPixl.github.io\\Animated.html");
				String gal = "<a href=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" class=\"image-link\">\r\n" + 
						"<img class=\"gen_image\" sizes=\"(min-width: 1260px) 350px, 15.18vw\" srcset=\"https://ik.imagekit.io/theperfectpixl/tr:n-size256/" + name + ".webp 256w,\r\n" + 
						"https://ik.imagekit.io/theperfectpixl/tr:n-size470/" + name + ".webp 470w, https://ik.imagekit.io/theperfectpixl/tr:n-size740/" + name + ".webp 740w,\r\n" + 
						"https://ik.imagekit.io/theperfectpixl/tr:n-size940/" + name + ".webp 940w\" \r\n" + 
						"src=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" height=\"400\" width=\"300\" alt=\"" + alt + "\"></a>";
				addHTML (f, gal);
			}
			if(wfc.isSelected())
			{
				f = new File("C:\\Users\\retro\\OneDrive\\Documents\\GitHub\\ThePerfectPixl.github.io\\WFC.html");
				String gal = "<a href=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" class=\"image-link\">\r\n" + 
						"<img class=\"gen_image\" sizes=\"(min-width: 1260px) 350px, 15.18vw\" srcset=\"https://ik.imagekit.io/theperfectpixl/tr:n-size256/" + name + ".webp 256w,\r\n" + 
						"https://ik.imagekit.io/theperfectpixl/tr:n-size470/" + name + ".webp 470w, https://ik.imagekit.io/theperfectpixl/tr:n-size740/" + name + ".webp 740w,\r\n" + 
						"https://ik.imagekit.io/theperfectpixl/tr:n-size940/" + name + ".webp 940w\" \r\n" + 
						"src=\"https://ik.imagekit.io/theperfectpixl/" + name + ".webp\" height=\"400\" width=\"300\" alt=\"" + alt + "\"></a>";
				addHTML (f, gal);
			}
				
			//Upload to ImageKit.io.
			uploadToImageKit(name);
				
			//Prompt for additional images.
			Object[] repeatChoice = new Object[]{"Yes", "No"};
			int repeatNum = JOptionPane.showOptionDialog(frame, "Would you like to add another image?", "Continue?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, repeatChoice, null);
			if(repeatNum == 1) repeat = false;
		}   
		if(!cancel)
	    {
	    	try
	    	{
		    	//Open Github Desktop once user is ready to commit.
	            String command = "C:\\Users\\retro\\AppData\\Local\\GitHubDesktop\\GitHubDesktop.exe";
	            Runtime run  = Runtime.getRuntime();
	            run.exec(command);
	        }
	        catch (IOException e){e.printStackTrace();}
		}
	}

	//Built by user Vinz on Stack Overflow
	public static BufferedImage rotate(BufferedImage bimg, Double angle) {
	    double sin = Math.abs(Math.sin(Math.toRadians(angle))),
	           cos = Math.abs(Math.cos(Math.toRadians(angle)));
	    int w = bimg.getWidth();
	    int h = bimg.getHeight();
	    int neww = (int) Math.floor(w*cos + h*sin),
	        newh = (int) Math.floor(h*cos + w*sin);
	    BufferedImage rotated = new BufferedImage(neww, newh, bimg.getType());
	    Graphics2D graphic = rotated.createGraphics();
	    graphic.translate((neww-w)/2, (newh-h)/2);
	    graphic.rotate(Math.toRadians(angle), w/2, h/2);
	    graphic.drawRenderedImage(bimg, null);
	    graphic.dispose();
	    return rotated;
	}
	
	public static void addHTML (File f, String content) throws IOException
	{
		//Convert file to document and append image HTML to the end of the gallery.
		Document doc = Jsoup.parse(f, "utf-8");
		Elements imgs = doc.select("div.body");
		imgs.append(content);
		
		//Convert document to byte array and write back to file.
		try(FileOutputStream fos = new FileOutputStream(f);
        BufferedOutputStream bos = new BufferedOutputStream(fos)){
        	byte[] bytes = doc.toString().getBytes();
        	bos.write(bytes);
        	bos.close();
        	fos.close();
        }
		catch (IOException e) {e.printStackTrace();}
	}
	
	public static void renameImage (Object name) throws IOException
	{
		//Save file to desktop in new location.
		File renamed = new File("C:\\Users\\retro\\Pictures\\Saved Pictures\\Toy Photos\\Cropped\\" + name + ".webp");
	    
	    //Compress image and rename to convert to webp format.
		OutputStream os = new FileOutputStream(renamed);
	    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
	    ImageWriter writer = (ImageWriter) writers.next();
	    
	    ImageOutputStream ios = ImageIO.createImageOutputStream(os);
	    writer.setOutput(ios);
	    ImageWriteParam param = writer.getDefaultWriteParam();

	    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	    param.setCompressionQuality(0.2f);
	    writer.write(null, new IIOImage(image, null, null), param);

	    os.close();
	    ios.close();
	    writer.dispose();
	    img.renameTo(renamed); 
	}
	
	public static void uploadToImageKit(Object name) throws IOException, InternalServerException, BadRequestException, UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException, ConflictException, PartialSuccessException, NotFoundException
	{
		//Convert image to base64 byte array. 
		byte[] fileContent = FileUtils.readFileToByteArray(img);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
			
		//Upload base64 image to ImageKit.io.
		FileCreateRequest fileCreateRequest = new FileCreateRequest(encodedString,  name.toString() + ".webp");
		Result result = ImageKit.getInstance().upload(fileCreateRequest);
		result.getName();
		
		//Rename image to accurately match user input.
		RenameFileRequest renameFileRequest = new RenameFileRequest();
		renameFileRequest.setFilePath(result.getFilePath());
		renameFileRequest.setNewFileName(name + ".webp");
		ImageKit.getInstance().renameFile(renameFileRequest);
	}
}