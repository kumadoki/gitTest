package com.custom.vo;

public class FileAttachVO {

	private String uuid;
	private String uploadpath;
	private String filename;
	private String filetype;
	private long fno;

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUploadPath() {
		return uploadpath;
	}
	public void setUploadPath(String uploadpath) {
		this.uploadpath = uploadpath;
	}
	public String getFileName() {
		return filename;
	}
	public void setFileName(String filename) {
		this.filename = filename;
	}
	public String getFileType() {
		return filetype;
	}
	public void setFileType(String filetype) {
		this.filetype = filetype;
	}
	public long getFno() {
		return fno;
	}
	public void setFno(long fno) {
		this.fno = fno;
	}


}
