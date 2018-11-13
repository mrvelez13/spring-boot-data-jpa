package com.typefy.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final static String UPLOADS_FOLDER = "uploads";
	
	@Override
	public Resource load(String fileName) throws MalformedURLException
	{
		Path pathPicture = getPath(fileName);
		log.info("pathPicture: " + pathPicture);
		Resource resource = null;
		
		resource = new UrlResource(pathPicture.toUri());
		
		if( !resource.exists() || !resource.isReadable())
		{
			throw new RuntimeException("No se puede cargar la imagen: " + pathPicture.toString());
		}
		
		
		return resource; 
	}

	@Override
	public String copy(MultipartFile file) throws IOException
	{
		String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootParh = getPath(uniqueFileName);
		
		log.info("rootPath: " + rootParh);

		Files.copy(file.getInputStream(), rootParh);
		
		return uniqueFileName;
	}

	@Override
	public boolean delete(String fileName) {
		Path rootPath = getPath(fileName);
		File file = rootPath.toFile();
		
		if(file.exists() && file.canRead())
		{
			if(file.delete())
			{
				return true;
			}
		}
		return false;
	}
	
	public Path getPath(String fileName)
	{
		return Paths.get(UPLOADS_FOLDER).resolve(fileName).toAbsolutePath();
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
		
	}

	@Override
	public void init() throws IOException {
		// TODO Auto-generated method stub
		Files.createDirectories(Paths.get(UPLOADS_FOLDER));
		
	}

}
