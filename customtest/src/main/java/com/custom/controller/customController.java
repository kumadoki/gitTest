package com.custom.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.custom.service.fileAttachService;
import com.custom.vo.FileAttachVO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class customController {
	@Inject
	fileAttachService fileattachService;
	@javax.annotation.Resource
	String UploadPath = "D:\\upload";

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/jsp/testpage")
	public void testClass() {
		logger.info("customtest/jsp/testpage");
	}
	@RequestMapping(value = "/fileList")
	public void fileList(HttpServletResponse response) {
		logger.info("customController- fileList");
		List<FileAttachVO> list = new ArrayList<FileAttachVO>();
		list = fileattachService.fileList();
		try {
			ObjectMapper mapper = new ObjectMapper();
			String str = mapper.writeValueAsString(list);
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(str);
		} catch (Exception e) {
			e.printStackTrace();
		};
	}
	@RequestMapping("/upload")
    public void fileUpload(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("file upload!");
        MultipartHttpServletRequest multi = request;
        List<MultipartFile> fileList = multi.getFiles("fileNm");
        for(int i = 0 ; i < fileList.size() ; i++){
        	if (!fileList.get(i).isEmpty()){
        		FileAttachVO fileAttachVO = new FileAttachVO();
        		UUID randomeUUID = UUID.randomUUID();
    			String uploadFileName = randomeUUID.toString();
	        	logger.info("file {} is ------------",i);
	        	logger.info(fileList.get(i).getOriginalFilename());
	        	logger.info("{}",fileList.get(i).getSize());
	        	logger.info("end ------------");
	            InputStream inputStream = null;
	            OutputStream outputStream = null;
	            String organizedfilePath="";
	    		String uploadFolderPath = getFolder();
	            try {
	                if (fileList.get(i).getSize() > 0) {
	                    inputStream = fileList.get(i).getInputStream();
	                    File uploadPath = new File(UploadPath, uploadFolderPath); // D:\\upload\\yyyy\\MM\\dd
			    		if (uploadPath.exists() == false) {
			    			uploadPath.mkdirs();
			    		} // make yyyy\MM\dd directory
	    		organizedfilePath = uploadPath + "\\" + uploadFileName;
	    		logger.info("organizedfilePath:: {}",organizedfilePath);
                outputStream = new FileOutputStream(organizedfilePath);
                int readByte = 0;
                byte[] buffer = new byte[8192];
                while ((readByte = inputStream.read(buffer, 0, 8120)) != -1) {
                    outputStream.write(buffer, 0, readByte);
                }
	        	fileAttachVO.setUuid(randomeUUID.toString());
	        	fileAttachVO.setUploadPath(uploadFolderPath.toString());
	        	fileAttachVO.setFileName(fileList.get(i).getOriginalFilename());
	        	fileAttachVO.setFileType("f");
	        	int str = fileattachService.insert(fileAttachVO);
				response.setCharacterEncoding("utf-8");
				response.getWriter().print(str);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                outputStream.close();
	                inputStream.close();
	            }
        	}
        }
    }

	@RequestMapping(value = "/downloadAction")
	public void downloadFile( String fileName, HttpServletRequest request, HttpServletResponse response) {
		logger.info("fileName is :: {}",fileName);
		int idx = fileName.lastIndexOf("\\");
		String filePath = UploadPath + "\\" + fileName.substring(0,idx);  // 파일이 업로드된 경로. db에서의 UploadPath
		String fileUuid = fileName.substring(idx+1); // uuid가 포함된 파일 이름.
		File file = new File(filePath, fileUuid);
		String userAgent = request.getHeader("User-Agent");
		Resource resource = new FileSystemResource(UploadPath+ "\\" + fileName);
		if(resource.exists() == false) {
			 // 파일이 서버의 경로에 존재하지 않을 경우.
			System.out.println("d'oh!");
		} else {
			String resourceOriginalName = fileattachService.fileExistCheck(fileUuid);
			logger.info("resourceOriginalName is :: {}",resourceOriginalName);
			FileInputStream fis = null;
	        BufferedInputStream bis =  null;
	        ServletOutputStream so =  null;
	        BufferedOutputStream bos =  null;
			try {
				String downloadName = null;
				if ( userAgent.contains("Trident")) {
					logger.info("IE browser");
					downloadName = URLEncoder.encode(resourceOriginalName, "UTF-8").replaceAll("\\+", " ");
				}else if(userAgent.contains("Edge")) {
					logger.info("Edge browser");
					downloadName =  URLEncoder.encode(resourceOriginalName,"UTF-8");
				}else {
					logger.info("Chrome browser");
					downloadName = new String(resourceOriginalName.getBytes("UTF-8"), "ISO-8859-1");
				}
				logger.info("downloadName:: {}",downloadName);
				fis = new FileInputStream(file);
				ServletContext context = request.getSession().getServletContext();
				// gets MIME type of the file
		        String mimeType = context.getMimeType(filePath);
		        if (mimeType == null) {
		            // set to binary type if MIME mapping not found
		            mimeType = "application/octet-stream";
		        }
		        // modifies response
		        response.setContentType(mimeType);
		        response.setContentLength((int) file.length());
		        String headerKey = "Content-Disposition";
		        String headerValue = String.format("attachment; filename=\"%s\"", downloadName);
		        response.setHeader(headerKey, headerValue);
		        bis=new BufferedInputStream(fis);
		        so=response.getOutputStream();
		        bos=new BufferedOutputStream(so);
		        byte[] data=new byte[2048];
		        int input=0;
		        while((input=bis.read(data))!=-1){
					bos.write(data,0,input);
					bos.flush();
		        }

			} catch (Exception e) {
				e.printStackTrace();
			} finally{
		        try {
			        if(bos!=null) bos.close();
			        if(bis!=null) bis.close();
			        if(so!=null) so.close();
			        if(fis!=null) fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 }

	@RequestMapping(value = "/deleteFileAction")
	public void deleteFile(String path, HttpServletResponse response) {
		logger.info("deleteFile Action");
		String temp = path.substring(path.lastIndexOf("\\")+1);
		String folderPath = path.substring(0, path.lastIndexOf("\\"));
		String[] arrTemp = temp.toString().split("_");
		String uuid = arrTemp[0];
		String uploadFolder = UploadPath + "\\" + folderPath;
		File deleteFile = new File(uploadFolder, temp);
		try {
			deleteFile.delete();
			int count = fileattachService.delete(uuid);
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String str = sdf.format(date);
		return str.replace("-", File.separator);
	}
	// 2020 07 13 excel test
}
