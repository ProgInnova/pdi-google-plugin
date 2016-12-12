package com.proginnova.pentaho.gdrive.impl;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.LimitExceededException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Permissions;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.User;

@SuppressWarnings("unused")
public class DriveFileManagement {
	
	public static String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
	
	// https://developers.google.com/drive/v3/web/migration
	
	public static File copyFiles(Drive service, String originFile, String parentFolder, String copyFileName) throws IOException, LimitExceededException{
		About accountData = service.about().get().setFields("storageQuota").execute();
		File fileToCopy = getFile(service, originFile);
		if(fileToCopy != null && fileToCopy.getQuotaBytesUsed() + accountData.getStorageQuota().getUsage() <= accountData.getStorageQuota().getLimit() ){
			File file = new File();
			file.setName(copyFileName);
			List<String> parents = new LinkedList<>();
			parents.add(parentFolder);
			file.setParents(parents);
			File copiedFile = service.files().copy(fileToCopy.getId(), file).setFields("id, owners, permissions").execute();
			
			User owner = fileToCopy.getOwners().get(0);
			for(Permission permission:copiedFile.getPermissions()){
				if(permission.getEmailAddress().equals(owner.getEmailAddress())){
					Permission newPermission = new Permission();
					newPermission.setId(permission.getId());
					newPermission.setRole("owner");
					service.permissions().update(copiedFile.getId(), permission.getId(), newPermission).setFields("role").setTransferOwnership(true).execute();
				}
			}
			
			return copiedFile;
		}else if(fileToCopy != null){
			throw new LimitExceededException("Quota exceeded in this account");
		}else{
			throw new IOException("File to copy not found");
		}
	}
	
	
	public static File createOrGetFolder(Drive service, String folderName, String folderDestiny) throws IOException{
		File parentFolder = getFile(service, FOLDER_MIME_TYPE, folderDestiny);
		if(parentFolder == null){
			throw new IOException(String.format("Folder %s not found", folderDestiny));
		}
		File folder = getFile(service, FOLDER_MIME_TYPE, folderName);
		if(folder != null){
			return folder;
		}else{
			folder = new File();
			folder.setName(folderName);
			folder.setMimeType(FOLDER_MIME_TYPE);
			List<String> parents = new LinkedList<>();
			parents.add(parentFolder.getId());
			folder.setParents(parents);
			File createdFolder = service.files().create(folder).setFields("id, owners, permissions").execute();
			
			User owner = parentFolder.getOwners().get(0);
			for(Permission permission:createdFolder.getPermissions()){
				if(permission.getEmailAddress().equals(owner.getEmailAddress())){
					Permission newPermission = new Permission();
					newPermission.setId(permission.getId());
					newPermission.setRole("owner");
					service.permissions().update(createdFolder.getId(), permission.getId(), newPermission).setFields("role").setTransferOwnership(true).execute();
				}
			}
			
			return createdFolder;
		}
	}
	
	public static File getFile(Drive service, String fileName) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("trashed=false and name='%s'", fileName)).setPageToken(pageToken).setFields("nextPageToken, files(id, name, parents, mimeType, quotaBytesUsed, owners)").execute();
			for(File file: request.getFiles()){
				if(file.getName().equals(fileName)){
					return file;
				}
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return null;
	}
	
	public static File getFile(Drive service, String mimeType, String fileName) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("mimeType='%s' and trashed=false and name='%s'", mimeType, fileName)).setPageToken(pageToken).setFields("nextPageToken, files(id, name, parents, quotaBytesUsed, owners)").execute();
			for(File file: request.getFiles()){
				if(file.getName().equals(fileName)){
					return file;
				}
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return null;
	}
	
	public static File getFileWithParent(Drive service, String fileName, String parent) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("trashed=false and name='%s' and '%s' in parents", fileName, parent)).setPageToken(pageToken).setFields("nextPageToken, files(id, name, parents, mimeType, quotaBytesUsed, owners)").execute();
			for(File file: request.getFiles()){
				if(file.getName().equals(fileName)){
					return file;
				}
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return null;
	}
	
	public static File getFileWithParent(Drive service, String mimeType, String fileName, String parent) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("mimeType='%s' and trashed=false and name='%s' and '%s' in parents", mimeType, fileName, parent)).setPageToken(pageToken).setFields("nextPageToken, files(id, name, parents, quotaBytesUsed, owners)").execute();
			for(File file: request.getFiles()){
				if(file.getName().equals(fileName)){
					return file;
				}
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return null;
	}
	
	public static boolean existsFile(Drive service, String mimeType, String fileName) throws IOException{
		return getFile(service, mimeType, fileName) != null;
	}
	
	public static boolean existsFile(Drive service, String fileName) throws IOException{
		return getFile(service, fileName) != null;
	}
	
	public static boolean deleteAllFoldersInFolder(Drive service, String folderParent) throws IOException{
		String pageToken = null;
		FileList request = null;
		do{
			request = service.files().list().setQ(String.format("mimeType='%s' and '%s' in parents", FOLDER_MIME_TYPE, folderParent)).setPageToken(pageToken).setFields("nextPageToken, files(id, name, parents, quotaBytesUsed, owners)").execute();
			System.out.println(request.getFiles());
			for(File file: request.getFiles()){
				service.files().delete(file.getId()).execute();
			}
			pageToken = request.getNextPageToken();
		}while(pageToken != null);
		return false;
	}
	
}
