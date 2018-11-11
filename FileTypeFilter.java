package Mini;

import java.io.File;

import javax.swing.filechooser.*;

public class FileTypeFilter extends FileFilter  {

	private final String extension;
	private final String description;
	
	public FileTypeFilter (String ext, String descrip){
		this.extension= ext;
		this.description= descrip;
	 }

	@Override
	public boolean accept(File file) {
		if(file.isDirectory())
		{
			return true;
		}
		return file.getName().endsWith(extension);
	}

	@Override
	public String getDescription() {
		return description + String.format("(*%s)", extension);
	}
}